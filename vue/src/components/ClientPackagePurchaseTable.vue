<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1>Purchase a Package</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />

    <v-data-table :headers="headers" :items="packages" class="elevation-5">
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Available Packages</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon small class="mr-2" @click="Purchase(item)">
          mdi-card-plus
        </v-icon>
      </template>
    </v-data-table>
    <br />
    <br />
  </v-container>
</template>

<script>
import packageDetailService from "../services/PackageDetailService";
import packagePurchaseService from "../services/PackagePurchaseService";

export default {
  name: "client-package-purchase-table",
  components: {},
  data() {
    return {
      headers: [
        {
          text: "Package Description",
          align: "start",
          value: "description",
        },
        { text: "Cost $", value: "package_cost", sortable: true },
        { text: "Purchase", value: "actions", sortable: false },
      ],
      packages: [],
      newClient: true,
      packagePurchase: {
        package_purchase_id: "",
        client_id: "",
        date_purchased: "",
        package_id: "",
        is_expired: "",
        classes_remaining: "",
        activation_date: "",
        expiration_date: "",
        total_amount_paid: "",
        is_monthly_renew: "",
        discount: "",
        package_description: "",
      },

      packageName: {},
    };
  },
  created() {
    this.getPublicPackagesTable();

    if (this.$store.state.clientDetails.is_new_client == false) {
        this.newClient = false;
    }

  },
  methods: {
    getPublicPackagesTable() {
      packageDetailService.getAllPublicPackages().then((response) => {
        if (response.status == 200) {
          // adjust this commit in the future perhaps
          this.$store.commit("SET_PACKAGE_LIST", response.data);

          this.packages = response.data;
        } else {
          alert("Error retrieving package information");
        }
      });
    },
    Purchase(item) {
      this.packageName = item.description.toLowerCase();
      if (
        this.packageName.includes("new") &&
        this.$store.state.clientDetails.is_new_client == false
      ) {
        alert("You are not a new client, please choose a different package");
      } else {
        // prepare packagePurchase object 
        
        // handle if it's a gift certificate (form)

        this.packagePurchase.client_id = this.$store.state.clientDetails.client_id;
        this.packagePurchase.date_purchased = Date.now();
        this.packagePurchase.package_id = item.package_id;
        this.packagePurchase.is_expired = false;
        this.packagePurchase.classes_remaining = item.classes_amount;
        if (item.is_subscription == true) {
            this.packagePurchase.activation_date = Date.now();
            // handle logic for expiration date depending if it's a 1 month or 6 month

            // https://codingbeautydev.com/blog/javascript-add-months-to-date/


        }
        this.packagePurchase.total_amount_paid = item.package_cost;
        this.packagePurchase.is_monthly_renew = false;
        
        

        packagePurchaseService
          .createPackagePurchase(this.packagePurchase)
          .then((response) => {
            if (response.status == 201) {
                alert("Succesfully purchased class")
              // call method that updates the client details and also the list of active packages
              this.$root.$refs.A.getActivePurchasePackageTable();
              this.$root.$refs.B.getPackageHistoryTable();
              // update client.is_new_client to false through mutation
               this.$store.commit("SET_CLIENT_DETAILS_NEW_CLIENT", false);
            }
          });
      }
    },
  },
};
</script>

<style>
</style>