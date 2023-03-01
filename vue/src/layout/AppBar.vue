<template>
  <v-app-bar app
             absolute
             color="#FFC107"
             light
             shrink-on-scroll
             prominent

             fade-img-on-scroll
             scroll-target="#scrolling-techniques-3"
  >
    <template v-slot:img="{ props }">
      <v-img
          v-bind="props"
          gradient="to bottom right, rgba(255, 250, 179), rgba(255,193,7)"
      ></v-img>
    </template>

    <v-app-bar-nav-icon ></v-app-bar-nav-icon>

    <v-toolbar-title>Sattva Yoga Center</v-toolbar-title>

    <v-spacer></v-spacer>


    <v-btn icon tile>
      <v-icon>mdi-dots-vertical</v-icon>
    </v-btn>


  </v-app-bar>
</template>

<script>
export default {
  name: "AppBar",
  data() {
    return {}
  },
  methods: {
    goToLogin() {
      this.$router.push({name: "login"});
    },
  }

}
</script>

<style scoped>

</style>
