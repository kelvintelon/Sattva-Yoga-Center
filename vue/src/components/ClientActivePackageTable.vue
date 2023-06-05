<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1 v-if="$store.state.user.username != 'admin'">Active Packages</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />
    <v-snackbar
      v-if="$store.state.user.username == 'admin'"
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
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click.prevent="allowClientReconcile"
        >
          Continue
        </v-btn>
      </template>
    </v-snackbar>
    <v-snackbar
      v-if="$store.state.user.username == 'admin'"
      v-model="snackBarReconcilePackagesSuccessful"
      color="green darken-2"
      elevation="24"
      :vertical="vertical"
      pill
    >
      Successfully Reconciled For Missing Payments
    </v-snackbar>
    <v-data-table
      :headers="headers"
      :items="packages"
      class="elevation-5"
      :sort-by.sync="sortBy"
      :sort-desc.sync="sortDesc"
      @update:sort-by="sortTable"
      @update:sort-desc="sortTable"
      :loading="loading"
      loading-text="Loading... Please wait"
      dense
      :options.sync="options"
      :server-items-length="totalPackagesPurchased"
      hide-default-footer
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Active Packages</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-dialog
            v-model="dialog"
            max-width="500px"
            v-if="$store.state.user.username == 'admin'"
          >
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

              <v-card-title>
                <v-container>
                  <v-row>
                    <v-col>
                      <v-select
                        label="Choose one"
                        :items="availablePackages"
                        v-model="selectedPackage"
                        item-text="description"
                        return-object
                      ></v-select>
                      <v-row>
                        <v-col>
                          <v-text-field
                            v-if="!showPercentDiscount"
                            v-model="selectedPackage.discount"
                            class="mt-0 pt-0"
                            type="number"
                            label="Discount: $"
                            min="0"
                          ></v-text-field>
                          <v-text-field
                            v-if="showPercentDiscount"
                            v-model="percentDiscount"
                            class="mt-0 pt-0"
                            type="number"
                            label="Discount: %"
                            min="0"
                          ></v-text-field
                        ></v-col>
                        <v-col>
                          <v-btn
                            @click="showPercentDiscount = true"
                            v-if="!showPercentDiscount"
                            ><v-icon>mdi-percent</v-icon></v-btn
                          >
                          <v-btn
                            @click="showPercentDiscount = false"
                            v-if="showPercentDiscount"
                            ><v-icon>mdi-currency-usd</v-icon></v-btn
                          >
                        </v-col></v-row
                      >
                      <div class="text--primary">
                        Package Cost: ${{ selectedPackage.package_cost }}
                      </div>
                      <div class="text--primary">
                        Package Discount: -${{ returnDiscount }}
                      </div>
                      <div class="text--primary" style="border-top: 1px solid">
                        Total Cost: ${{ returnTotal }}
                      </div>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-title>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <v-btn
                  color="blue darken-1"
                  text
                  @click.prevent="addPackageForClient"
                >
                  Save
                </v-btn>
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
      <template
        v-slot:[`item.actions`]="{ item }"
        v-if="$store.state.user.username == 'admin'"
      >
        <v-icon small class="mr-2" @click="Remove(item)">
          mdi-close-thick
        </v-icon>
      </template>
    </v-data-table>
    <v-row>
      <v-col cols="11">
        <v-pagination
          v-model="page"
          :length="Math.ceil(totalPackagesPurchased / pageSize)"
          @input="temporaryPageMethod"
          total-visible="8"
        ></v-pagination>
      </v-col>
      <v-col col="1" class="mt-2">
        <v-select
          v-model="pageSize"
          :items="[10, 20, 30, 40, 50]"
          outlined
          filled
          @change="temporaryPageSizeMethod"
        >
        </v-select>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
    <br />
    <br />
    <v-overlay :value="overlay">
      <v-progress-circular indeterminate size="70"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script>
import packagePurchaseService from "../services/PackagePurchaseService";
import packageDetailService from "../services/PackageDetailService";
import eventService from "../services/EventService";
import clientDetailService from "../services/ClientDetailService";


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
      page: 1,
      pageSize: 10,
      sortBy: 'date_purchased',
      sortDesc: false,
      totalPackagesPurchased: 0,
      paginatedObject: {},
      packages: [],
      packagePurchase: {
        package_purchase_id: "",
        client_id: "",
        date_purchased: "",
        package_id: "",
        classes_remaining: "",
        activation_date: "",
        expiration_date: "",
        total_amount_paid: 0,
        is_monthly_renew: "",
        discount: 0,
        package_description: "",
      },
      availablePackages: [],
      dialog: false,
      selectedPackage: {},
      timeout: -1,
      snackBarReconcilePackages: false,
      showPercentDiscount: false,
      percentDiscount: 0,
      snackBarReconcilePackagesSuccessful: false,
      overlay: false,
      loading: true,
      sharedPackages: [],
    };
  },
  watch: {
    options: {
      handler() {
        this.getActivePurchaseServerRequest();
      },
      deep: true,
    },
  },
  beforeCreate() {
    clientDetailService.getClientDetailsByClientId(this.$route.params.clientId).then((response) => {
        if (response.data.client_id != 0) {
          this.clientDetails = response.data;
          this.$store.commit("SET_CLIENT_DETAILS", response.data);
          // alert("active package table client details")
          if (this.clientDetails.redFlag == true) {
            this.snackBarReconcileWarning = true
          }
        }
      });
    // TODO: CAREFUL DELETING THIS BECAUSE WE FORGOT WHAT IT DOES
    //    this.$root.$on("getActivePurchasePackageTable", () => {
    //   this.getActivePurchaseServerRequest();
    // });
  },
  created() {
    
    this.getSharedActivePackages();
    setTimeout(() => {
      this.getActivePurchaseServerRequest();
    }, 1500)
    
    this.$root.$refs.A = this;

    if (this.$store.state.user.username == "admin") {
      this.headers.unshift({
        text: "Package ID",
        value: "package_purchase_id",
        sortable: true,
      });
      this.headers.push({ text: "Cancel", value: "actions", sortable: false });
      this.getPublicPackagesTable();
    }
  },
  methods: {
    // snackbars not showing and not unshowing
    allowClientReconcile() {
      this.loading = true;
      this.overlay = !this.overlay;
      eventService
        .reconcileClassesForClient(this.$route.params.clientId)
        .then((response) => {
          if (response.status == 200) {
            this.loading = false;
            this.overlay = false;
            alert("Success");
            this.getActivePurchaseServerRequest();
            this.$root.$refs.B.getPackageHistoryTable();
            this.$root.$refs.C.getClientDetails();
            this.$root.$refs.D.getClientEventTable();
            this.$root.$refs.E.getEventDetailsCall();
            this.snackBarReconcilePackagesSuccessful = true;
            this.snackBarReconcilePackages = false;
          } else {
            this.snackBarReconcilePackages = false;
            alert("Error Reconciling Classes");
            this.getActivePurchaseServerRequest();
          }
        });
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
      this.percentDiscount = 0;
      this.selectedPackage.discount = 0;
    },
    temporaryPageMethod() {
      this.$vuetify.goTo(0);
      this.getActivePurchaseServerRequest();
    },
    temporaryPageSizeMethod() {
      if (this.page == 1) {
        this.getActivePurchaseServerRequest();
      } else {
        this.$vuetify.goTo(0);
        this.page = 1;
        this.getActivePurchaseServerRequest();
      }
    },
    sortTable() {
      if (this.sortDesc == undefined) {
        this.sortDesc = false;
      } 
      this.getActivePurchaseServerRequest();
    },
    getActivePurchaseServerRequest() {
      this.loading = true;
      this.overlay = !this.overlay;
      if (this.$store.state.user.username == "admin") {
        packagePurchaseService
          .getPaginatedUserPurchasedPackagesByClientId(this.$route.params.clientId,this.page, this.pageSize, this.sortBy, this.sortDesc)
          .then((response) => {
            if (response.status == 200) {
              this.loading = false;
              this.overlay = false;
              // focus on if it's expired or not
              var today = new Date();
              var dd = String(today.getDate()).padStart(2, "0");
              var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
              var yyyy = today.getFullYear();
              today = yyyy + "-" + mm + "-" + dd;
              this.paginatedObject = response.data;
              this.totalPackagesPurchased = this.paginatedObject.totalRows;
              this.packages = this.paginatedObject.listOfPurchasedPackages.filter((item) => {
                return (
                  (item.is_subscription && item.expiration_date >= today) ||
                  (!item.is_subscription &&
                    item.expiration_date >= today &&
                    item.classes_remaining > 0)
                );
              });
              this.packages.forEach((item) => {
                item.date_purchased = new Date(item.date_purchased);
              });
              // console.log(this.$store.state.clientDetails.redFlag)
              // console.log(this.packages.length > 0)
              // console.log(this.$store.state.sharedPackages.length > 0)
              // console.log(this.$store.state.clientDetails.redFlag &&
                // (this.packages.length > 0 ||
                // this.sharedPackages.length > 0);
                // alert(this.$store.state.clientDetails.redFlag)
              if (
                this.$store.state.clientDetails.redFlag &&
                (this.packages.length > 0 ||
                this.sharedPackages.length > 0)
              ) {
                this.snackBarReconcilePackages = true;
              }
              this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
            } else {
              alert("Error retrieving package information");
            }
          });
      } else {
        packagePurchaseService.getPaginatedUserPurchasedPackages(this.page, this.pageSize, this.search, this.sortBy, this.sortDesc).then((response) => {
          if (response.status == 200) {
            this.loading = false;
            this.overlay = false;
            // focus on if it's expired or not
            var today = new Date();
            var dd = String(today.getDate()).padStart(2, "0");
            var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
            var yyyy = today.getFullYear();
            today = yyyy + "-" + mm + "-" + dd;
            this.paginatedObject = response.data;
              this.totalPackagesPurchased = this.paginatedObject.totalRows;
              this.packages = this.paginatedObject.listOfPurchasedPackages.filter((item) => {
              // return (item.expiration_date >= today) || (item.expiration_date == null && item.classes_remaining > 0) || (item.expiration_date >= today && item.classes_remaining > 0);
              return (
                (item.is_subscription && item.expiration_date) ||
                (!item.is_subscription &&
                  item.expiration_date >= today &&
                  item.classes_remaining > 0)
              );
            });
            this.packages.forEach((item) => {
              item.date_purchased = new Date(item.date_purchased);
            });
            if (
              this.$store.state.clientDetails.redFlag &&
              this.packages.length > 0 
            ) {
              this.snackBarReconcilePackages = true;
            }
            this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
          } else {
            alert("Error retrieving package information");
          }
        });
      }
    },
    getSharedActivePackages() {
      packagePurchaseService
        .getAllSharedActiveQuantityPackagesByClientId(this.$route.params.clientId)
        .then((response) => {
          if (response.status == 200) {
            this.sharedPackages = response.data;
          }
          this.$store.commit("SET_SHARED_PACKAGE_LIST", this.sharedPackages);
        });
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
      this.loading = true;
      this.overlay = !this.overlay;
      this.packagePurchase.client_id = this.$route.params.clientId;
      const jsonDate = new Date().toJSON();
      this.packagePurchase.date_purchased = jsonDate;
      this.packagePurchase.package_id = this.selectedPackage.package_id;
      this.packagePurchase.is_expired = false;

      // if you're buying a subscription and there's a subscription that's active

      if (this.packages.length > 0) {
        let foundSubscription = false;
        // loop to find another package that is a subscription
        for (let index = 0; index < this.packages.length; index++) {
          if (
            this.packages[index].is_subscription &&
            this.selectedPackage.is_subscription
          ) {
            foundSubscription = true;
            // use that expiration date to create the activation date and expiration date of the new subscription
            let formatDate = this.packages[index].expiration_date;
            formatDate = formatDate.replaceAll("-", "/");

            let newActivationDate = new Date(formatDate);

            newActivationDate = newActivationDate.setDate(
              newActivationDate.getDate() + 1
            );
            this.packagePurchase.activation_date = new Date(newActivationDate);

            this.packagePurchase.expiration_date = this.addMonths(
              new Date(newActivationDate),
              this.selectedPackage.subscription_duration
            );
          }
        }

        // what to do if you don't find a subscription
        if (!this.selectedPackage.is_subscription) {
          this.packagePurchase.expiration_date = this.addMonths(new Date(), 12);
        } else if (!foundSubscription && this.selectedPackage.is_subscription) {
          this.packagePurchase.activation_date = new Date();
          if (this.selectedPackage.subscription_duration > 0) {
            this.packagePurchase.expiration_date = this.addMonths(
              new Date(),
              this.selectedPackage.subscription_duration
            );
          }
        }
      }

      if (this.selectedPackage.is_subscription && this.packages.length == 0) {
        this.packagePurchase.activation_date = new Date();
        if (this.selectedPackage.subscription_duration > 0) {
          this.packagePurchase.expiration_date = this.addMonths(
            new Date(),
            this.selectedPackage.subscription_duration
          );
        }
      }
      if (!this.selectedPackage.is_subscription && this.packages.length == 0) {
        this.packagePurchase.expiration_date = this.addMonths(new Date(), 12);
      }

      if (this.showPercentDiscount) {
        // if it's a percent
        let num =
          this.selectedPackage.package_cost * (1 - this.percentDiscount / 100);
        this.packagePurchase.discount = this.selectedPackage.package_cost - num;
        this.packagePurchase.total_amount_paid = Math.round(num * 100) / 100;
      } else if (
        this.selectedPackage.discount >= 0 &&
        this.selectedPackage.package_cost >= 0 &&
        !this.showPercentDiscount
      ) {
        // if it's in dollars
        this.packagePurchase.discount = this.selectedPackage.discount;
        this.packagePurchase.total_amount_paid =
          this.selectedPackage.package_cost - this.selectedPackage.discount;
      } else {
        this.packagePurchase.total_amount_paid =
          this.selectedPackage.package_cost;
      }

      //  this.selectedPackage.package_cost;
      this.packagePurchase.is_monthly_renew = false;
      this.packagePurchase.classes_remaining =
        this.selectedPackage.classes_amount;

      packagePurchaseService
        .createPackagePurchase(this.packagePurchase)
        .then((response) => {
          if (response.status == 201) {
            this.loading = false;
            this.overlay = false;
            alert("Succesfully purchased package");
            // call method that updates the list of active packages
            this.getActivePurchaseServerRequest();
            this.$root.$refs.B.getPackageHistoryTable();
            this.$root.$refs.D.getClientEventTable();

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
      date.setDate(date.getDate() - 1);
      return date;
    },
  },

  computed: {
    returnDiscount() {
      if (this.showPercentDiscount && this.selectedPackage.package_cost >= 0) {
        // this.selectedPackage.discount = this.selectedPackage.package_cost * (1-this.percentDiscount);
        let num =
          this.selectedPackage.package_cost -
          this.selectedPackage.package_cost * (1 - this.percentDiscount / 100);
        let math = Math.round(num * 100) / 100;
        if (this.selectedPackage.package_cost > math) {
          return math;
        }
        return 0;
      } else if (
        this.selectedPackage.discount >= 0 &&
        this.selectedPackage.package_cost >= 0
      ) {
        // TODO: Change the following line
        if (this.selectedPackage.discount < this.selectedPackage.package_cost) {
          return this.selectedPackage.discount;
        }
      }
      return 0;
    },
    returnTotal() {
      if (this.showPercentDiscount && this.selectedPackage.package_cost >= 0) {
        let num =
          this.selectedPackage.package_cost * (1 - this.percentDiscount / 100);
        if (num > 0) {
          return Math.round(num * 100) / 100;
        }
        return 0;
      } else if (
        this.selectedPackage.discount >= 0 &&
        this.selectedPackage.package_cost >= 0
      ) {
        let difference =
          this.selectedPackage.package_cost - this.selectedPackage.discount;
        if (difference < this.selectedPackage.package_cost && difference > 0) {
          return difference;
        }
        return 0;
      } else if (this.selectedPackage.package_cost >= 0) {
        return this.selectedPackage.package_cost;
      } else {
        return 0;
      }
    },
  },
};
</script>

<style>
</style>