<template>

    <v-toolbar dense color="amber lighten 1">
      <header-logo></header-logo>

      <v-toolbar-title>Sattva Yoga Center</v-toolbar-title>

      <v-spacer></v-spacer>

      <login-button v-if="$store.state.token == ''"></login-button>
      <v-div v-else>
        <my-account-button></my-account-button>
      </v-div>
    </v-toolbar>


</template>

<script>
import LoginButton from "@/components/LoginButton.vue";
import HeaderLogo from "@/components/HeaderLogo.vue";
import MyAccountButton from "@/components/MyAccountButton.vue";

export default {
  name: "TopHeader",
  components: { MyAccountButton, HeaderLogo, LoginButton
},
data() {
  return {
    
  }
},
created() {

},
}
</script>

<style scoped>

</style>