<template>
  <div>
    <v-container
      :key="align"
    >
   
      <v-row>
        <v-col cols="14">
           <family-table-list></family-table-list>
      </v-col>
      </v-row>
    </v-container>
  </div>
</template>
  
  <script>

import clientDetailService from "../services/ClientDetailService";
import FamilyTableList from "../components/FamilyTableList.vue"

export default {
  name: "family-management",
  components: { 
    FamilyTableList
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