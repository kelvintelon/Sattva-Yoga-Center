<template>
  <v-card>
    <v-system-bar color="deep-orange darken-2"></v-system-bar>
    <v-toolbar-title>Sattva Yog Center</v-toolbar-title>

    <v-app-bar color="deep-orange accent-2" prominent>
      <header-logo></header-logo>
      <login-button v-if="$store.state.token == ''"></login-button>
      <my-account-button v-else></my-account-button>
    </v-app-bar>
  </v-card>
</template>

<script>
import HeaderLogo from "./HeaderLogo.vue";
import LoginButton from "./LoginButton.vue";
import MyAccountButton from "./MyAccountButton.vue";

export default {
  name: "header-bar",
  components: { MyAccountButton, LoginButton, HeaderLogo },
  data() {
    return {
      expand: false,
    };
  },
  created() {},
};
</script>

<style></style>