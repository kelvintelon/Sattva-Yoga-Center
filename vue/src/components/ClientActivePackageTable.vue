<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1 v-if="$store.state.user.username != 'admin'">Active Packages</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />

    <v-data-table :headers="headers" :items="packages" class="elevation-5" sort-by="date_purchased" sort-desc="[true]">
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Active Packages</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
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
    };
  },
  created() {
    this.getActivePurchaseServerRequest();
    
    this.$root.$refs.A = this;

    if (this.$store.state.user.username == "admin") {
      this.headers.unshift({ text: "Package ID", value: "package_purchase_id", sortable: false });
       this.headers.push( { text: "Cancel", value: "actions", sortable: false });
    }
  },
  methods: {
     
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