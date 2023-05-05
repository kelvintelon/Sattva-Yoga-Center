<template>
  <div>
    <!-- src="..\\assets\\stockphoto1.png"-->
    <!-- src="..\\assets\\stockphoto2.png"  color="rgba(255, 183, 0, 0.9)"  -->
    <!-- src="..\\assets\\pexels-neosiam-4498792.png" color="rgba(245, 104, 71, 0.95)" -->
    <v-app-bar shrink-on-scroll app elevation="1"  dark prominent fade-img-on-scroll height="150px" contain src="..\\assets\\pexels-neosiam-4498792.png" color="rgba(245, 104, 71, 0.95)" 
    >

      <template v-slot:img="{ props }">
        <v-img  v-bind="props" ></v-img>
      </template>
      <!-- <v-menu offset-y bottom right>
      <template v-slot:activator="{ on, attrs }">
        <v-btn class="ma-1" x-large v-bind="attrs" v-on="on" icon tile @click.prevent="checkToken">
          <v-icon>mdi-menu</v-icon>
        </v-btn>
      </template>
      <div></div>
      <v-list>
        <v-list-item v-for="(link) in links" :key="link.text" router :to="link.route">
          {{ link.text }}
        </v-list-item>
      </v-list>
    </v-menu> -->
      <v-btn class="ma-1" x-large icon tile @click="checkToken()">
        <v-icon x-large>mdi-menu</v-icon>
      </v-btn>
      <v-toolbar-side-icon>
        <HeaderLogo />
      </v-toolbar-side-icon>

      <v-spacer></v-spacer>

    </v-app-bar>
    <v-expand-x-transition appear>
      <v-navigation-drawer v-if="drawer" v-model="drawer" temporary app hide-overlay>

        <v-list-item @click="drawer = false">

          <v-icon large>mdi-menu-open</v-icon>

        </v-list-item>

        <v-divider></v-divider>


        <v-list dense>
          <v-list-item v-for="link in links" :key="link.text" link router :to="link.route">


            <v-list-item-content>
              <v-list-item-title>{{ link.text }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-navigation-drawer></v-expand-x-transition>
  </div>
</template>

<script>
import HeaderLogo from '../components/HeaderLogo.vue';
import clientDetailService from "../services/ClientDetailService";


export default {
  name: "AppBar",
  data() {
    return {
      menu: false,
      links: [],
      drawer: false,
    };
  },
  methods: {
    goToLogin() {
      this.$router.push({ name: "login" });
    },
    checkToken() {
      this.drawer = !this.drawer
      this.links = [];
      if (this.$store.state.token != "") {
        clientDetailService.getClientDetailsOfLoggedInUser().then((response) => {
          response
        })
          .catch((error) => {
            const response = error.response;
            if (response.status === 401) {
              this.$store.state.token = ""
              this.links = [];
              this.links.push({ text: 'Login', route: '/login' },
                { text: 'Register', route: '/register' })

              this.$router.push('/login')
            }
          });
      }


      this.links = [];
      if (this.$store.state.token == '') {
        this.links.push({ text: 'Login', route: '/login' },
          { text: 'Register', route: '/register' })
      }
      else if (this.$store.state.user.username == "admin") {
        this.links = [];
        this.links.push({ text: 'Home', route: '/' },
          { text: 'Class Management', route: '/classManagement' },
          { text: 'Teacher Management', route: '/teacherManagement' },
          { text: 'Client Management', route: '/clientManagement' },
          { text: 'Package Management', route: '/packageManagement' },
          { text: 'Logout', route: '/logout' })
      } else if (this.$store.state.token != '') {
        this.links = [];
        if (
          Object.keys(this.$store.state.clientDetails).length === 0 ||
          this.$store.state.clientDetails.client_id == 0
        ) {
          this.links = [];
          this.links.push({ text: 'Fill Out Client Form', route: '/clientRegistration' },
            { text: 'Logout', route: '/logout' })
        } else {
          this.links = [];
          this.links.push({ text: 'Home', route: '/' },
            { text: 'Edit Profile', route: '/editProfile' },
            { text: 'View Classes', route: '/registerForClass' },
            { text: 'Package Management', route: '/clientPackageManagement' },
            { text: 'Reset Password', route: '/' },
            { text: 'Logout', route: '/logout' })
        }
      }
    }
  },
  watch: {

  },
  components: { HeaderLogo },


};
</script>

<style scoped></style>
