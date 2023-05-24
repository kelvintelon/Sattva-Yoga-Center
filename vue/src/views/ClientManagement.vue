<template>
  <div>
    <v-container
      :key="align"
    >
   
      <v-row>
        <v-col cols="12"> <client-table-paginated-list></client-table-paginated-list>
      </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
// import ClientTableList from "../components/ClientTableList.vue";
import ClientTablePaginatedList from "../components/ClientTablePaginatedList.vue";

export default {
  name: "client-management",
  components: {
    ClientTablePaginatedList,
  },
};
</script>

<style>

</style>