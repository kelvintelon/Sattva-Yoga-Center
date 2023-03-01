<template>
  <v-app>

      <AppBar/>
      <v-main>
        <router-view/>
      </v-main>
      <AppFooter/>

  </v-app>
</template>

<script>

import AppBar from "@/layout/AppBar.vue";
import AppFooter from "@/layout/AppFooter.vue";

export default {
  name: 'App',
  components: {AppFooter, AppBar},

  data: () => ({
    //
  }),
};
</script>

<style>
@font-face {
  font-family: "Garamond";

}
</style>
