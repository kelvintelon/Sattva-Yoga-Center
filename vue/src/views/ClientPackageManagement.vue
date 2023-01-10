<template>
  <div>
    <top-header></top-header>

    <v-container
      :key="align"
    >
   
      <v-row>
        <v-col cols="14">
           <client-package-purchase-table></client-package-purchase-table>
           <client-active-package-table></client-active-package-table>
           <client-package-history-table></client-package-history-table>
      </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ClientPackagePurchaseTable from "../components/ClientPackagePurchaseTable.vue";
import ClientActivePackageTable from "../components/ClientActivePackageTable.vue";
import ClientPackageHistoryTable from "../components/ClientPackageHistoryTable.vue";


export default {
  name: "client-package-management",
  components: {
    TopHeader,
    ClientPackagePurchaseTable,
    ClientActivePackageTable,
    ClientPackageHistoryTable,
  },
};
</script>

<style>
</style>