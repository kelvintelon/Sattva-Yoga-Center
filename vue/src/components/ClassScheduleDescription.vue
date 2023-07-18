<template>
    <v-card class="mx-auto px-4 my-4 rounded-xl" max-width="900px" min-width="200px">
          <v-textarea readonly :value="readOnlyDescription" v-if="$store.state.user.username != 'admin'">

          </v-textarea>
          <v-textarea @keyup="updateClassScheduleDescription($event)" :value="adminOnlyDescription" v-if="$store.state.user.username == 'admin'">
            
          </v-textarea>
      </v-card>
      
 </template>
 
 <script>
 import websiteDescriptionService from "../services/WebsiteDescriptionService";

 export default {
   name: "class-schedule-description-card",
   components: {},
   data() {
     return {
      readOnlyDescription: '',
      adminOnlyDescription: '',
     };
   },
   methods: {
    getClassScheduleDescription() {
      websiteDescriptionService.getClassSchedule().then((response) => {
        if (response.status == 200) {
          this.readOnlyDescription = response.data;
          this.adminOnlyDescription = response.data;
        } else {
          alert("Error retrieving class schedule")
        }
      })
    },
    updateClassScheduleDescription(event) {
      // let newDescription = {description: ''};
      // newDescription.description = event.target.value;
      // alert(newDescription.description)
      // this.adminOnlyDescription = event.target.value;
      websiteDescriptionService.updateClassScheduleDescription({description: event.target.value}).then((response) => {
        if (response.status == 200) {
          
          // this.readOnlyDescription = newDescription.description;
        }
      });
    },
     
   },
   created() {
      this.getClassScheduleDescription();
   },
 };
 </script>
 
 <style></style>
 