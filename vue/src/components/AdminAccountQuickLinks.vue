<template>
  <v-container>
    <v-row>
      <v-col>
        <h1>My Account Settings</h1>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn elevation="5" v-on:click="goToClassManagementPage()">
          <v-icon> mdi-family-tree </v-icon>  - Class Management</v-btn
        >
      </v-col>
      <v-col>
        <logout-button></logout-button>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn elevation="5" v-on:click="goToTeacherManagementPage()"> <v-icon> mdi-account-group </v-icon> - Teacher Management </v-btn>
      </v-col>
      <v-col>
        <v-btn elevation="5"> <v-icon> mdi-book-open-outline </v-icon> - Package Management </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import LogoutButton from "../components/LogoutButton.vue";

export default {
  name: "admin-account-links",
  components: {
    LogoutButton,
  },
  data() {
    return {};
  },
  methods: {
    goToClassManagementPage() {
      this.$router.push({ name: "class-management" });
    },
    goToTeacherManagementPage() {
        this.$router.push({name: "teacher-management"})
    }
  },
};
</script>

<style>
</style>