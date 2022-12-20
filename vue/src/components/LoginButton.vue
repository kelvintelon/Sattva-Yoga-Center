<template>
  <div class="login-button">
    <v-btn elevation="5" v-on:click="goToLogin()">
      <v-icon > mdi-login-variant
      </v-icon> Login
    </v-btn>
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
        this.$router.push({name:"login"});
      },
  },
  created() {},
};
</script>

<style>



</style>