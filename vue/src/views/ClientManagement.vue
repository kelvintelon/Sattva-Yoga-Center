<template>
  <div>
    <v-container
      :key="align"
    >
   
      <v-row>
        <v-col cols="12"> <client-table-list></client-table-list>
      </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
// import ClientTableList from "../components/ClientTablePaginatedList.vue";
import ClientTableList from "../components/ClientTableList.vue";

export default {
  name: "client-management",
  components: {
    ClientTableList,
  },
};
</script>

<style>

</style>