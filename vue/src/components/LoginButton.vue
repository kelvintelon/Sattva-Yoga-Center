<template>
  <div class="login-button">
    <v-btn-text elevation="0" v-on:click="goToLogin()">
      <v-icon> mdi-login-variant </v-icon> Login
    </v-btn-text>
  </div>
</template>

<script>
export default {
  name: "login-button",
  data() {
    return {};
  },
  methods: {
    goToLogin() {
      this.$router.push({ name: "login" });
    },
  },
  created() {},
};
</script>

<style></style>
