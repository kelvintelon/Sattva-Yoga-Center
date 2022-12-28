<template>
  <div>
    <top-header></top-header>

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
import TopHeader from "../components/TopHeader2.vue";
import ClientTableList from "../components/ClientTableList.vue";

export default {
  name: "client-management",
  components: {
    TopHeader,
    ClientTableList,
  },
};
</script>

<style>

</style>