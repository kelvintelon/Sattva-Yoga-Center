<template>
  <div>
    <v-container>
      <v-row>
        <v-col cols="6"> <teacher-table-list></teacher-table-list></v-col>
        <!-- <v-col cols="6"> <create-teacher-form></create-teacher-form></v-col> -->
      </v-row>
      <!--
      <v-simple-table>
        <template v-slot:default>
          <thead>
            <tr>
              <th class="text-left">Name</th>
              <th class="text-left">Is Active</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in desserts" :key="item.name">
              <td>{{ item.name }}</td>
              <td>{{ item.calories }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      -->
    </v-container>
  </div>
</template>

<script>
// import CreateTeacherForm from "../components/CreateTeacherForm.vue";
import TeacherTableList from "../components/TeacherTableList.vue"
import clientDetailService from "../services/ClientDetailService";


export default {
  name: "teacher-management",
  components: {
    // CreateTeacherForm,
    TeacherTableList
  },
  created() {
    if (this.$store.state.user.username != 'admin') {
      this.$router.push({name: 'home'})
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
};
</script>

<style>
</style>