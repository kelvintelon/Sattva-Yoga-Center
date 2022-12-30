<template>
  <v-container>
    <v-row>
      <v-spacer></v-spacer>
      <v-col>
        <h1>My Account Settings</h1>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
    <v-row>
      <v-col>
        <v-btn elevation="5" v-on:click="goToEditProfilePage()">
          <v-icon> mdi-yoga </v-icon> Edit profile</v-btn
        >
      </v-col>
      <v-col>
        <logout-button></logout-button>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn elevation="5" v-on:click="goToRegisterClassesPage()"> <v-icon> mdi-family-tree </v-icon> View Classes </v-btn>
      </v-col>
      <v-col>
        <v-btn elevation="5" > My Recent Purchases </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn elevation="5" > Reset Password </v-btn>
      </v-col>
      <v-col>
        <v-btn elevation="5" > My Subscriptions </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import LogoutButton from "../components/LogoutButton.vue";

export default {
  name: "user-account-links",
  components: {
    LogoutButton,
  },
  data() {
    return {};
  },
  methods: {
    goToEditProfilePage() {
      this.$router.push({ name: "edit-profile-page" });
    },
    goToRegisterClassesPage() {
      this.$router.push({ name: "class-registration" });
    },
  },
};
</script>

<style>
</style>