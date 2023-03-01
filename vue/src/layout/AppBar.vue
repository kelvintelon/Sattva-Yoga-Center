<template>
  <v-app-bar app
             color="#6A76AB"
             dark
             prominent
             src="https://picsum.photos/id/306/1920/?blur=2"
             scroll-target="#scrolling-techniques-3"
  >
    <template v-slot:img="{ props }">
      <v-img
          v-bind="props"

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
