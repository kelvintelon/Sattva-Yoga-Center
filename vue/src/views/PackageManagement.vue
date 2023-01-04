<template>
  <div>
    <top-header></top-header>

    <v-container
      :key="align"
    >
   
      <v-row>
        <v-col cols="14">
           <package-table-list></package-table-list>
      </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import PackageTableList from "../components/PackageTableList.vue";


export default {
  name: "package-management",
  components: {
    TopHeader,
    PackageTableList,
    
  },
};
</script>

<style>
</style>