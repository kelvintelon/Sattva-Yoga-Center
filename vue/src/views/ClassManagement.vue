<template>
  <div>
    <top-header></top-header>

    <v-container
      :key="align"
    >
   
      <v-row>
        <v-col cols="12"> <class-table-list></class-table-list>
      </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ClassTableList from "../components/ClassTableList.vue";

export default {
  name: "class-management",
  components: {
    TopHeader,
    ClassTableList,
  },
};
</script>

<style>
</style>