<template>
  <v-container>
    <v-row><br></v-row>
    <v-row>
    <v-spacer></v-spacer>
    <h1>Sign up for classes</h1>
    <v-spacer></v-spacer></v-row>
    <br>
    <v-app>
      
      <v-data-table
        :headers="headers"
        :items="classes"
        class="elevation-5"
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>Available Classes</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
          </v-toolbar>
      
        </template>
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon small class="mr-2" @click="SignUp(item)">
            mdi-account-plus
          </v-icon>
        </template>
      </v-data-table>
      <br>
      <br>
      <v-data-table
        :headers="clientClassHeaders"
        :items="clientClasses"
        class="elevation-5"
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>My Classes</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
          </v-toolbar>
        </template>
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon small class="mr-2" @click="RemoveClassForClient(item)">
            mdi-close-thick
          </v-icon>
        </template>
      </v-data-table>
    </v-app>
    
    
   
   
      
    



    <!-- <v-btn elevation="5" v-on:click="buyFirstClass()">
      <v-icon> mdi-cart </v-icon> Sign up For Class
    </v-btn> -->
  </v-container>
</template>

<script>
// import packagePurchaseService from "../services/PackagePurchaseService";
import classDetailService from "../services/ClassDetailService";

export default {
  name: "class-registration",
  components: {},
  data() {
    return {
      headers: [
        {
          text: "Class Description",
          value: "class_description",
          align: "start",
        },
        { text: "Teacher", value: "teacher_name" },
        { text: "Duration (minutes)", value: "class_duration" },
        { text: "Class Time", value: "start_time", sortable: false },
        { text: "Days", value: "date_range" },
        { text: "Sign Up", value: "actions", sortable: false },
      ],
       clientClassHeaders: [
        {
          text: "Class Description",
          value: "class_description",
          align: "start",
        },
        { text: "Teacher", value: "teacher_name" },
        { text: "Duration (minutes)", value: "class_duration" },
        { text: "Class Time", value: "start_time", sortable: false },
        { text: "Days", value: "date_range" },
        { text: "Remove Me From Class", value: "actions", sortable: false },
      ],
      classes: [],
      clientClasses: [],
      classClientSignUp: {
        class_id: "",
        client_id: "",
      },
      validSignUp:true,
    };
  },
  methods: {
    SignUp(item) {
      this.classClientSignUp.class_id = item.class_id;
      this.classClientSignUp.client_id =
        this.$store.state.clientDetails.client_id;

        this.clientClasses.forEach((item) => {
          if (item.class_id == this.classClientSignUp.class_id) {
            alert("You have already signed up for this class!");
            this.validSignUp = false;
          } else {
            this.validSignUp = true;
          }
        })
        if (this.validSignUp == true) {
          classDetailService
        .registerForClass(this.classClientSignUp)
        .then((response) => {
          if (response.status == 201) {
            // call method that updates the client_class_table
            this.getClientClassTable();
          }
        });
        }  
    },
    RemoveClassForClient(item) {
      classDetailService
        .removeClassForClient(item.class_id)
        .then((response) => {
          if (response.status == 200) {
            // call method that updates the client_class_table
            this.getClientClassTable();
          }
        });
    },
    getClassTable() {
      classDetailService.getAllClasses().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_CLASS_LIST", response.data);
          this.classes = response.data;
        } else {
          alert("Error retrieving class information");
        }
      });
    },
    getClientClassTable() {
      classDetailService.getAllClientClasses().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_CLIENT_CLASS_LIST", response.data);
          this.clientClasses = response.data;
        } else {
          alert("Error retrieving class information");
        }
      });
    },
  },
  created() {
    this.getClassTable();
    this.getClientClassTable();
  },
};
</script>

<style>
</style>