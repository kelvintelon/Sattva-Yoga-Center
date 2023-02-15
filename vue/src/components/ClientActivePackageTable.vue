<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1>Active Packages</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />

    <v-data-table :headers="headers" :items="packages" class="elevation-5">
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Active Packages</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
      <template v-slot:[`item.is_expired`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_expired"
          disabled
        ></v-simple-checkbox>
      </template>
      <template v-slot:[`item.is_monthly_renew`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_monthly_renew"
          disabled
        ></v-simple-checkbox>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
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
          text: "Expired?",
          value: "is_expired",
        },
        {
          text: "Monthly Renewal?",
          value: "is_monthly_renew",
        },
        { text: "Cancel", value: "actions", sortable: false },
      ],
      packages: [],
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
    };
  },
  created() {
    this.getActivePurchaseServerRequest();
    
    this.$root.$refs.A = this;
  },
  methods: {
    getActivePurchaseServerRequest() {
      packagePurchaseService.getUserPurchasedPackages().then((response) => {
        if (response.status == 200) {
          // focus on if it's expired or not

          this.packages = response.data.filter((item) => {
            return item.is_expired == false;
          });
          this.packages.forEach((item) => {
            item.date_purchased = new Date(item.date_purchased);
          })
          this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
        } else {
          alert("Error retrieving package information");
        }
      });
    },
    Remove(item) {
      // this will be an update
      packagePurchaseService.expirePackage(item).then((response) => {
        if (response.status == 200) {
          alert("You have canceled this package");
          this.getActivePurchasePackageTable();

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