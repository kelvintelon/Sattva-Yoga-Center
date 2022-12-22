<template>
  <div class="logout-button">
    <v-btn elevation="5" v-on:click="goToLogout()">
      <v-icon> mdi-logout-variant </v-icon> Logout
    </v-btn>
  </div>
</template>

<script>
export default {
  name: "logout-button",
  data() {
    return {};
  },
  methods: {
    goToLogout() {
      this.$router.push({ name: "logout" });
    },
  },
  created() {},
};
</script>

<style></style>
