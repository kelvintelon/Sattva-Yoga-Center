<template>
  <div>

    <v-container
      :key="align"
    >
   
      <v-row>
        <v-col cols="14">
           <client-package-purchase-table></client-package-purchase-table>
           <client-active-package-table></client-active-package-table>
           <client-purchase-history-table></client-purchase-history-table>
      </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import ClientPackagePurchaseTable from "../components/ClientPackagePurchaseTable.vue";
import ClientActivePackageTable from "../components/ClientActivePackageTable.vue";
import ClientPurchaseHistoryTable from "../components/ClientPurchaseHistoryTable.vue";


export default {
  name: "client-package-management",
  components: {
    ClientPackagePurchaseTable,
    ClientActivePackageTable,
    ClientPurchaseHistoryTable,
  },
};
</script>

<style>
</style>