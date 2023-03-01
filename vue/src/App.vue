<template>
  <v-app>
    <v-layout>
      <NavDrawer>

      </NavDrawer>
      <v-main>
        <router-view/>
      </v-main>

    </v-layout>

  </v-app>
</template>

<script>

import NavDrawer from "@/layout/NavDrawer.vue";

export default {
  name: 'App',
  components: {NavDrawer},

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
