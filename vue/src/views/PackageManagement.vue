<template>
  <div>
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
import PackageTableList from "../components/PackageTableList.vue";


export default {
  name: "package-management",
  components: {
    PackageTableList,
    
  },
};
</script>

<style>
</style>