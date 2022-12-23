<template>
  <v-container>
    <v-row>
      <v-col>
        <h1>My Account Settings</h1>
      </v-col>
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
        <v-btn> <v-icon> mdi-family-tree </v-icon> View my Classes </v-btn>
      </v-col>
      <v-col>
        <v-btn> My Recent Purchases </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-btn> Reset Password </v-btn>
      </v-col>
      <v-col>
        <v-btn> My Subscriptions </v-btn>
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
  },
};
</script>

<style>
</style>