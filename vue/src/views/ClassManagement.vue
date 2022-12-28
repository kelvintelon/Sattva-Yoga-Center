<template>
  <div>
    <top-header></top-header>

    <v-container
      :key="align"
    >
   
      <v-row>
        <v-col cols="14">
            <!-- <class-calendar></class-calendar> -->
            <br>
            <br>
           <class-table-list></class-table-list>
      </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ClassTableList from "../components/ClassTableList.vue";
// import ClassCalendar from "../components/ClassCalendar.vue";

export default {
  name: "class-management",
  components: {
    TopHeader,
    ClassTableList,
    // ClassCalendar,
  },
};
</script>

<style>
</style>