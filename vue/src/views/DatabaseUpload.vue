<template>
  <div>
    <upload-client-component></upload-client-component>
    <upload-teacher-component></upload-teacher-component>
    <upload-event-component></upload-event-component>
    <upload-packages-component></upload-packages-component>
    <upload-sales-component></upload-sales-component>
    <upload-gift-card-sales-component></upload-gift-card-sales-component>
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
import uploadGiftCardSalesComponent from "../components/uploadGiftCardSalesComponent.vue";
import uploadGiftReportComponent from "../components/uploadGiftReportComponent.vue";
import uploadAttendanceComponent from "../components/uploadAttendanceComponent.vue";
import clientDetailService from "../services/ClientDetailService";


export default {
  name: "database-upload",
  components: { uploadClientComponent, 
    uploadTeacherComponent, 
    uploadEventComponent, 
    uploadPackagesComponent,
    uploadSalesComponent,
    uploadGiftCardSalesComponent,
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
    } else {
      clientDetailService
            .getClientDetailsOfAdminUser()
            .then((response) => {
              response;
              this.checkLinks();
            })
            .catch((error) => {
              const response = error.response;
              if (response.status === 401) {
                this.$store.state.token = "";
                this.links = [];
                this.links.push(
                  { text: "Login", route: "/login" },
                  { text: "Register", route: "/register" }
                );
                if (
                  this.$router.currentRoute.name != "home" &&
                  this.$router.currentRoute.name != "login" &&
                  this.$router.currentRoute.name != "register"
                ) {
                  this.$router.push({ name: "login" });
                }
                this.checkLinks();
              }
              if (response.status == 403) {
                this.$router.push({name: "logout"});
              }
            });
    }
  },
  methods: {},
};
</script>
  
  <style>
</style>