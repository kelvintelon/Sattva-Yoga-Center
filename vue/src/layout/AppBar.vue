<template>
  <v-app-bar
    app
    elevation="1"
    color="#FBC02D"
    dark
    prominent
    shrink-on-scroll
    famde-img-on-scroll
    height="150px"
    
  >
    
    <template v-slot:img="{ props }">
      <v-img v-bind="props"></v-img>
    </template>
    <v-menu bottom right>
      <template v-slot:activator="{ on, attrs }">
        <v-btn v-bind="attrs" v-on="on" icon tile>
          <v-icon>mdi-menu</v-icon>
        </v-btn>
      </template>
      <div></div>
      <v-list>
        <v-list-item v-for="(item, i) in items" :key="i">
          <v-list-item-title>{{ item.link }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
    <v-spacer></v-spacer>
    <HeaderLogo/>
    <v-spacer></v-spacer>
  </v-app-bar>
</template>

<script>
import HeaderLogo from '../components/HeaderLogo.vue';

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
    components: { HeaderLogo }
};
</script>

<style scoped></style>
