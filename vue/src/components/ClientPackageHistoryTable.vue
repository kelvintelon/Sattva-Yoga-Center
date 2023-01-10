<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1>Package History</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />

    <v-data-table :headers="headers" :items="packageHistoryList" class="elevation-5">
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Package History</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
    </v-data-table>
    <br />
    <br />
  </v-container>
</template>

<script>
import packagePurchaseService from "../services/PackagePurchaseService";

export default {
  name: "client-package-history-table",
  components: {},
  data() {
    return {
      headers: [
        {
          text: "Package Description",
          align: "start",
          value: "package_description",
        },
        { text: "Timestamp", value: "date_purchased", sortable: true },
        {
          text: "Classes Remaning",
          value: "classes_remaining",
          sortable: true,
        },
        {
          text: "Classes Remaning",
          value: "classes_remaining",
          sortable: true,
        },
      ],
      packageHistoryList: [],
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
    this.getPackageHistoryTable();

    this.$root.$refs.B = this;
  },
  methods: {
    getPackageHistoryTable() {
      packagePurchaseService.getUserPurchasedPackages().then((response) => {
        if (response.status == 200) {
          this.packageHistoryList = response.data;
          this.$store.commit("SET_PACKAGE_HISTORY_LIST", response.data);
        } else {
          alert("Error retrieving package information");
        }
      });
    }
  },
};
</script>

<style>
</style>