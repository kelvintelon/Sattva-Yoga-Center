<template>
  <v-card >
    <v-toolbar dense color="amber lighten 1">
      <header-logo></header-logo>

      <v-toolbar-title>Sattva Yoga Center</v-toolbar-title>

      <v-spacer></v-spacer>

      <login-button v-if="$store.state.token == ''"></login-button>
      <v-div v-else>
        <logout-button></logout-button>
      </v-div>
    </v-toolbar>
  </v-card>

</template>

<script>
import LoginButton from "@/components/LoginButton.vue";
import HeaderLogo from "@/components/HeaderLogo.vue";
import LogoutButton from "@/components/LogoutButton.vue";

export default {
  name: "TopHeader",
  components: {LogoutButton, HeaderLogo, LoginButton}
}
</script>

<style scoped>

</style>