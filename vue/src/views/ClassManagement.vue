<template>
  <div>
    <v-container :key="align">
      <v-row>
        <v-col cols="14">
          <class-calendar></class-calendar>
          <br />
          <br />
          <v-row
            ><v-spacer></v-spacer>
            <v-btn @click.prevent="flipTable()">Table</v-btn>
            <v-spacer></v-spacer>
            <v-btn @click.prevent="flipWeek()">Week</v-btn>
            <v-spacer></v-spacer
          ></v-row>
          <br/>
          <div>
          <class-table-list v-show="showTable"></class-table-list></div>
         <div> <class-week-table v-show="showWeek"></class-week-table></div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import ClassTableList from "../components/ClassTableList.vue";
import ClassCalendar from "../components/ClassCalendar.vue";
import ClassWeekTable from "../components/ClassWeekTableList.vue";

export default {
  name: "class-management",
  data() { return {
    showTable:true,
  showWeek:false,
  }},
  
  components: {
    ClassTableList,
    ClassCalendar,
    ClassWeekTable,
  },
  methods: {
    flipWeek() {
      this.showTable = false;
      this.showWeek = true;
    },
    flipTable() {
      this.showTable = true;
      this.showWeek = false;
    }
  },
  created() {
  
  },
};
</script>

<style>
</style>