<template>
  <div class="logout-button">
    <v-btn-text elevation="0" v-on:click="goToLogout()">
      <v-icon> mdi-logout-variant </v-icon> Logout
    </v-btn-text>
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
