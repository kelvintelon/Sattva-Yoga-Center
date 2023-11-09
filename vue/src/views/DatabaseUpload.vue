<template>
  <div>
    <upload-client-component></upload-client-component>
    <upload-teacher-component></upload-teacher-component>
    <upload-event-component></upload-event-component>
    <upload-packages-component></upload-packages-component>
    <upload-sales-component></upload-sales-component>
    <upload-gift-report-component></upload-gift-report-component>
    <upload-attendance-component></upload-attendance-component>
  </div>
</template>
  
  <script>
import uploadClientComponent from "../components/uploadClientComponent.vue";
import uploadTeacherComponent from "../components/uploadTeacherComponent.vue";
import uploadEventComponent from "../components/uploadEventComponent.vue";
import uploadPackagesComponent from '../components/uploadPackagesComponent.vue';
import uploadSalesComponent from '../components/uploadSalesComponent.vue';
import uploadGiftReportComponent from "../components/uploadGiftReportComponent.vue";
import uploadAttendanceComponent from "../components/uploadAttendanceComponent.vue";

export default {
  name: "database-upload",
  components: { uploadClientComponent, 
    uploadTeacherComponent, 
    uploadEventComponent, 
    uploadPackagesComponent,
    uploadSalesComponent,
    uploadGiftReportComponent,
    uploadAttendanceComponent
   },
  data() {
    return {
    };
  },
  created() {
    if (this.$store.state.user.username != "admin") {
      this.$router.push({ name: "home" });
    }
  },
  methods: {},
};
</script>
  
  <style>
</style>