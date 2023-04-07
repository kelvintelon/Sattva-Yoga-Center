<template>
  <!-- shrink-on-scroll -->
  <v-app-bar
    app
    elevation="1"
    color="#F9A825"
    dark
    prominent
    famde-img-on-scroll
    height="150px"
    
  >
    
    <template v-slot:img="{ props }">
      <v-img v-bind="props"></v-img>
    </template>
    <v-menu offset-y bottom right>
      <template v-slot:activator="{ on, attrs }">
        <v-btn v-bind="attrs" v-on="on" icon tile>
          <v-icon>mdi-menu</v-icon>
        </v-btn>
      </template>
      <div></div>
      <v-list>
        <v-list-item v-for="(link) in links" :key="link.text" router :to="link.route">
        {{ link.text }}          
         </v-list-item>
      </v-list>
    </v-menu>
    
    <HeaderLogo/>
    <v-spacer></v-spacer>
    <LoginButton/>
  </v-app-bar>
</template>

<script>
import LoginButton from '../components/LoginButton.vue';
import HeaderLogo from '../components/HeaderLogo.vue';


export default {
    name: "AppBar",
    data() {
        return {
            menu: false,
            links: [
              // { text: '  Login  ', route: '/login'},
              { text: 'Home', route: '/'},
              { text: 'Class Management', route: '/classManagement'},
              { text: 'Client Details', route: '/clientDetails/:clientId'},
              { text: 'Client Management', route: '/clientManagement'},
              { text: 'Package Management', route: '/packageManagement'},
              { text: 'Event Details', route: '/eventDetails/:eventId'},             
            ]
        };
    },
    methods: {
        goToLogin() {
            this.$router.push({ name: "login" });
        },
    },
    watch: {
      
    },
    components: { HeaderLogo, LoginButton }
};
</script>

<style scoped></style>
