<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1 v-if="$store.state.user.username != 'admin'">Active Packages</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />
    <v-snackbar v-if="$store.state.user.username == 'admin'"
      v-model="snackBarReconcilePackages"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
      :timeout="timeout"
    >
      Would You Like To Reconcile Missing Payments With Current Active Package
      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarReconcilePackages = false"
          left
        >
          Close
        </v-btn>
        <v-btn color="white" text v-bind="attrs" @click="allowClientReconcile">
          Continue
        </v-btn>
      </template>
    </v-snackbar>
    <v-snackbar v-if="$store.state.user.username == 'admin'"
      v-model="snackBarReconcilePackagesSuccessful"
      color="green darken-2"
      elevation="24"
      :vertical="vertical"
      pill
      
    >
      Successfully Reconciled For Missing Payments
    </v-snackbar>
    <v-data-table :headers="headers" :items="packages" class="elevation-5" sort-by="date_purchased" sort-desc="[true]">
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Active Packages</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-dialog v-model="dialog" max-width="500px"  v-if="$store.state.user.username == 'admin'">
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
                Add a package
              </v-btn>
            </template>
            <v-card>
              <!-- Add a Package Form starts here -->
              <v-card-title>
                <span class="text-h5">Add a package</span>
              </v-card-title>

              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col>
                      <v-select
                        label="Choose one or multiple"
                        :items="availablePackages"
                        v-model="selectedPackage"
                        item-text="description"
                        return-object
                        
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <v-btn color="blue darken-1" text @click="addPackageForClient"> Save </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <template v-slot:[`item.is_monthly_renew`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_monthly_renew"
          disabled
        ></v-simple-checkbox>
      </template>
      <template v-slot:[`item.actions`]="{ item }"  v-if="$store.state.user.username == 'admin'">
        <v-icon small class="mr-2" @click="Remove(item)">
          mdi-card-plus
        </v-icon>
      </template>
    </v-data-table>
    <br />
    <br />
  </v-container>
</template>

<script>
import packagePurchaseService from "../services/PackagePurchaseService";
import packageDetailService from "../services/PackageDetailService";
import eventService from "../services/EventService";

export default {
  name: "client-active-package-table",
  components: {},
  data() {
    return {
      headers: [
        {
          text: "Package Description",
          align: "start",
          value: "package_description",
        },
        { text: "Purchase Date", value: "date_purchased", sortable: true },
        {
          text: "Total Cost",
          value: "total_amount_paid",
        },
        {
          text: "Activation Date",
          value: "activation_date",
          sortable: true,
        },
        {
          text: "Expiration Date",
          value: "expiration_date",
          sortable: true,
        },
        {
          text: "Classes Remaning",
          value: "classes_remaining",
          sortable: true,
        },
        {
          text: "Monthly Renewal?",
          value: "is_monthly_renew",
        },
      ],
      packages: [],
      packagePurchase: {
        package_purchase_id: "",
        client_id: "",
        date_purchased: "",
        package_id: "",
        classes_remaining: "",
        activation_date: "",
        expiration_date: "",
        total_amount_paid: "",
        is_monthly_renew: "",
        discount: "",
        package_description: "",
      },
      availablePackages: [],
      dialog: false,
      selectedPackage: {},
      timeout: -1,
      snackBarReconcilePackages: false,
      snackBarReconcilePackagesSuccessful: false,
    };
  },
  created() {
    this.getActivePurchaseServerRequest();
    
    this.$root.$refs.A = this;

    if (this.$store.state.user.username == "admin") {
      this.headers.unshift({ text: "Package ID", value: "package_purchase_id", sortable: false });
       this.headers.push( { text: "Cancel", value: "actions", sortable: false });
    this.getPublicPackagesTable();
    }
  },
  methods: {
    allowClientReconcile() {
      eventService.reconcileClassesForClient(this.$route.params.clientId).then((response) => {
        if (response.status == 200) {
          this.snackBarReconcilePackages = false;
          alert("Success")
          this.getActivePurchaseServerRequest();
          this.$root.$refs.B.getPackageHistoryTable();
          this.$store.commit("SET_CLIENT_DETAILS_RED_FLAG", false);
          this.snackBarReconcilePackagesSuccessful = true;
          
        } else {
          this.snackBarReconcilePackages = false;
          alert("Error Reconciling Classes")
          this.getActivePurchaseServerRequest();
        }
      })
    },
     getPublicPackagesTable() {
      packageDetailService.getAllPublicPackages().then((response) => {
        if (response.status == 200) {
          // adjust this commit in the future perhaps
          this.$store.commit("SET_PACKAGE_LIST", response.data);

          this.availablePackages = response.data;
        } else {
          alert("Error retrieving package information");
        }
      });
    },
    close() {
      this.dialog = false;
    },
    getActivePurchaseServerRequest() {
      if (this.$store.state.user.username == "admin") {
        packagePurchaseService.getUserPurchasedPackagesByClientId(this.$route.params.clientId).then((response) => {
        if (response.status == 200) {
          // focus on if it's expired or not
          var today = new Date();
          var dd = String(today.getDate()).padStart(2, '0');
          var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
          var yyyy = today.getFullYear();
          today = yyyy + '-' + mm + '-' + dd;

          this.packages = response.data.filter((item) => {
            return (item.expiration_date >= today) || (item.expiration_date == null && item.classes_remaining > 0) || (item.expiration_date >= today && item.classes_remaining > 0);
          });
          this.packages.forEach((item) => {
            item.date_purchased = new Date(item.date_purchased);
          })
          if (this.$store.state.clientDetails.redFlag && this.packages.length > 0) {
            this.snackBarReconcilePackages = true;
          }
          this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
        } else {
          alert("Error retrieving package information");
        }
      })
     } else { 
      packagePurchaseService.getUserPurchasedPackages().then((response) => {
        if (response.status == 200) {
          // focus on if it's expired or not
          var today = new Date();
          var dd = String(today.getDate()).padStart(2, '0');
          var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
          var yyyy = today.getFullYear();
          today = yyyy + '-' + mm + '-' + dd;

          this.packages = response.data.filter((item) => {
            return (item.expiration_date >= today) || (item.expiration_date == null && item.classes_remaining > 0) || (item.expiration_date >= today && item.classes_remaining > 0);
          });
          this.packages.forEach((item) => {
            item.date_purchased = new Date(item.date_purchased);
          })
          if (this.$store.state.clientDetails.redFlag && this.packages.length > 0) {
            this.snackBarReconcilePackages = true;
          }
          this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
        } else {
          alert("Error retrieving package information");
        }
      });
     }
    },
    Remove(item) { 
      // this will be an update
      packagePurchaseService.expirePackage(item).then((response) => {
        if (response.status == 200) {
          alert("You have canceled this package");
          this.getActivePurchaseServerRequest();

          // call the method to update the purchase history table so it updates the expired column
          this.$root.$refs.B.getPackageHistoryTable();
        } else {
          alert("Error canceling class");
        }
      });
    },
    addPackageForClient() {

      this.packagePurchase.client_id = this.$route.params.clientId;
      const jsonDate = new Date().toJSON();
          this.packagePurchase.date_purchased = jsonDate;
          this.packagePurchase.package_id = this.selectedPackage.package_id;
          this.packagePurchase.is_expired = false;
          if (this.selectedPackage.is_subscription) {
            this.packagePurchase.activation_date = new Date();
            if (this.selectedPackage.subscription_duration > 0) {
                this.packagePurchase.expiration_date = this.addMonths(
                  new Date(),
                  this.selectedPackage.subscription_duration);
              }
          }
           this.packagePurchase.total_amount_paid =
              this.selectedPackage.package_cost;
            this.packagePurchase.is_monthly_renew = false;
             this.packagePurchase.classes_remaining =
              this.selectedPackage.classes_amount;

      packagePurchaseService
              .createPackagePurchase(this.packagePurchase)
              .then((response) => {
                if (response.status == 201) {
                  alert("Succesfully purchased package");
                  // call method that updates the list of active packages
                  this.getActivePurchaseServerRequest();
                  this.$root.$refs.B.getPackageHistoryTable();
                  
                  this.selectedPackage = {};
                  this.packagePurchase = {};
                  this.close();
                }
              });
          this.close();

    },
    addMonths(date, months) {
      var d = date.getDate();
      date.setMonth(date.getMonth() + months);
      if (date.getDate() != d) {
        date.setDate(0);
      }
      // fixing  fix: (3/3 → 4/2) NOT (3/3 → 4/3)
      date.setDate(date.getDate()-1);
      return date;
    },
  },
  mounted(){
    this.$root.$on('getActivePurchasePackageTable', () => {
      this.getActivePurchaseServerRequest();
    })
  }
};
</script>

<style>
</style>