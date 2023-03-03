<template>
  <v-app-bar
    app
    elevation="1"
    color="#FFC107"
    light
    prominent
    scroll-target="#scrolling-techniques-3"
  >  
  <v-menu bottom right>
      <template v-slot:activator="{ on, attrs }">
        <v-btn v-bind="attrs" v-on="on" icon tile>
          <v-icon>mdi-menu</v-icon>
        </v-btn>
      </template>
      <v-list>
        <v-list-item v-for="(item, i) in items" :key="i">
          <v-list-item-title>{{ item.link }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>


    <v-toolbar-title>Sattva Yoga Center</v-toolbar-title>

    <v-spacer></v-spacer>
  </v-app-bar>
</template>

<script>
export default {
  name: "AppBar",
  data() {
    return {
      menu: false,
      items: [{ link: "logout" }],
    };
  },
  methods: {
    goToLogin() {
      this.$router.push({ name: "login" });
    },
  },
};
</script>

<style scoped></style>
