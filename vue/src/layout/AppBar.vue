<template>
  <div role="appBar">
    <!-- src="..\\assets\\stockphoto1.png"-->
    <!-- src="..\\assets\\stockphoto2.png"  color="rgba(255, 183, 0, 0.9)"  -->
    <!-- src="..\\assets\\pexels-neosiam-4498792.png" color="rgba(245, 104, 71, 0.95)" -->
    <v-app-bar
      shrink-on-scroll
      app
      elevation="1"
      dark
      prominent
      fade-img-on-scroll
      height="150px"
      contain
      
      color="rgba(245, 104, 71, 0.95)"
    >
      <template v-slot:img="{ props }">
        <v-img v-bind="props"></v-img>
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
      <v-btn
        class="ma-1"
        x-large
        icon
        tile
        @click="checkToken()"
        role="button"
        :aria-label="menuLabel"
        tabindex="0"
      >
        <v-icon x-large aria-label="Menu Icon">mdi-menu</v-icon>
      </v-btn>
      <v-toolbar-side-icon>
        <HeaderLogo />
      </v-toolbar-side-icon>

      <!-- <template v-slot:extension>
        <v-tabs centered center-active show-arrows>
         
          <v-tab style="color:white" @click="$vuetify.goTo(firstTarget, options)" v-if="computeFirstTab != '<'">{{ computeFirstTab }}</v-tab>
           <v-btn v-else small fab depressed light @click="$vuetify.goTo(firstTarget, options)"><v-icon x-large color="rgba(245, 104, 71, 0.95)">mdi-chevron-left</v-icon></v-btn>
           <v-tab style="color:white" @click="$vuetify.goTo(secondTarget, options)">{{ computeSecondTab }}</v-tab>
          <v-tab style="color:white" @click="$vuetify.goTo(thirdTarget, options)" v-if="computeThirdTab != '>'" >{{ computeThirdTab }}</v-tab>
          <v-btn v-else small fab depressed light @click="$vuetify.goTo(thirdTarget, options)"><v-icon x-large color="rgba(245, 104, 71, 0.95)">mdi-chevron-right</v-icon></v-btn>
        </v-tabs>
      </template> -->
    </v-app-bar>
    <v-expand-x-transition appear>
      <v-navigation-drawer
        role="navigation"
        v-if="drawer"
        v-model="drawer"
        temporary
        app
        hide-overlay
      >
        <v-list-item
          aria-label="Close Menu Navigation"
          role="button"
          tabindex="0"
          @click="drawer = false"
        >
          <v-icon large aria-label="Menu Open Icon">mdi-menu-open</v-icon>
        </v-list-item>

        <v-divider></v-divider>

        <v-list dense>
          <v-list-item
            tabindex="0"
            v-for="link in links"
            :key="link.text"
            link
            router
            :to="link.route"
          >
            <v-list-item-content>
              <v-list-item-title>{{ link.text }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-navigation-drawer></v-expand-x-transition
    >
  </div>
</template>

<script>
import HeaderLogo from "../components/HeaderLogo.vue";
import clientDetailService from "../services/ClientDetailService";

export default {
  name: "AppBar",
  data() {
    return {
      menu: false,
      menuLabel: "Open Menu",
      clientProfile: {
        last_name: "",
        first_name: "",
        is_client_active: true,
        is_new_client: true,
        street_address: "",
        city: "",
        state_abbreviation: "",
        zip_code: "",
        phone_number: "",
        is_on_email_list: false,
        email: "",
        has_record_of_liability: false,
        date_of_entry: "",
        user_id: 0,
      },
      links: [],
      drawer: false,
      selectedTarget: "",
      selectedIndex: 0,
      availableTargets: [
        "Home",
        "Classes",
        "Studio Guidelines",
        "About Us",
        "Instructor Training",
        "Find Us",
      ],
      availableTargetIDs: [
        "#first-tag-id",
        "#classes-id",
        "Studio Guidelines",
        "About Us",
        "Instructor Training",
        "Find Us",
      ],
    };
  },
  computed: {
    // selectedPreviousIndex(event) {
    //   alert(event.target.id)
    //   return event.target.id;
    // },
    options() {
      return {
        duration: 1000,
        offset: 500,
        easing: "easeInOutCubic",
      };
    },

    computeFirstTab() {
      if (this.selectedIndex == 0) {
        return "Home";
      } else {
        return "<";
      }
    },
    firstTarget() {
      // alert(event.target.id)
      if (this.computeFirstTab === "Home") {
        return "#first-tab-id";
      } else {
        if (this.selectedIndex - 1 <= 0) {
          this.setSelectedIndex(0);
          return "#first-tab-id'";
        } else {
          this.setSelectedIndex(this.selectedIndex - 1);
          return this.availableTargetIDs[this.selectedIndex];
        }
      }
    },
    computeSecondTab() {
      if (this.computeFirstTab == "Home") {
        return "Classes";
      } else {
        return this.availableTargets[this.selectedIndex];
      }
    },
    secondTarget() {
      // alert(event.target.id)
      if (this.selectedIndex == 0) {
        return "#classes-id";
      } else {
        alert(this.selectedIndex);
        return this.availableTargetIDs[this.selectedIndex];
      }
    },
    computeThirdTab() {
      if (this.selectedIndex == this.availableTargets.length - 1) {
        return "Find Us";
      } else {
        return ">";
      }
    },
    thirdTarget() {
      if (this.computeFirstTab === "Home") {
        this.setSelectedIndex(1);
        return "#studio-guidelines-id";
      } else {
        if (this.selectedIndex + 1 > this.availableTargets.length - 1) {
          this.setSelectedIndex(this.availableTargets.length - 1);
          return "#find-us-id'";
        } else {
          this.setSelectedIndex(this.selectedIndex + 1);
          return this.availableTargetIDs[this.selectedIndex];
        }
      }
    },
  },
  methods: {
    setSelectedIndex(num) {
      this.selectedIndex = num;
    },
    goToLogin() {
      this.$router.push({ name: "login" });
    },
    checkToken() {
      this.drawer = !this.drawer;
      this.links = [];
      if (this.$store.state.token != "") {
        if (this.$store.state.user.username == "admin") {
          clientDetailService
            .getClientDetailsOfAdminUser()
            .then((response) => {
              response;
              this.checkLinks();
            })
            .catch((error) => {
              const response = error.response;
              if (response.status === 401) {
                this.$store.state.token = "";
                this.links = [];
                this.links.push(
                  { text: "Login", route: "/login" },
                  { text: "Register", route: "/register" }
                );
                if (
                  this.$router.currentRoute.name != "home" &&
                  this.$router.currentRoute.name != "login" &&
                  this.$router.currentRoute.name != "register"
                ) {
                  this.$router.push({ name: "login" });
                }
                this.checkLinks();
              }
              if (response.status == 403) {
                this.$router.push({name: "logout"});
              }
            });
        } else {
          clientDetailService
            .getClientDetailsOfLoggedInUser()
            .then((response) => {
              if (response.data.client_id != 0) {
                this.clientProfile = response.data;
                this.$store.commit("SET_CLIENT_DETAILS", response.data);
              } else {
                this.$router.push("/clientRegistration");
              }
              this.checkLinks();
            })
            .catch((error) => {
              const response = error.response;
              if (response.status === 401) {
                this.$store.state.token = "";
                this.links = [];
                this.links.push(
                  { text: "Login", route: "/login" },
                  { text: "Register", route: "/register" }
                );
                if (
                  this.$router.currentRoute.name != "home" &&
                  this.$router.currentRoute.name != "login" &&
                  this.$router.currentRoute.name != "register"
                ) {
                  this.$router.push({ name: "login" });
                }
                this.checkLinks();
              }
            });
        }
      }
      this.checkLinks();
    
    },
    checkLinks() {
      this.links = [];
      if (this.$store.state.token == "") {
        this.links.push(
          { text: "Login", route: "/login" },
          { text: "Register", route: "/register" }
        );
      } else if (this.$store.state.user.username == "admin") {
        this.links = [];
        this.links.push(
          { text: "Home", route: "/" },
          { text: "Class Management", route: "/classManagement" },
          { text: "Teacher Management", route: "/teacherManagement" },
          { text: "Client Management", route: "/clientManagement" },
          { text: "Package Management", route: "/packageManagement" },
          { text: "Reset Password", route: "/resetPassword" },
          { text: "Videos", route: "/videos" },
          { text: "Database Upload", route: "/databaseUpload" },
          { text: "Logout", route: "/logout" }
        );
      } else if (this.$store.state.token != "") {
        this.links = [];
        if (
          Object.keys(this.$store.state.clientDetails).length === 0 ||
          this.$store.state.clientDetails.client_id == 0
        ) {
          this.links = [];
          this.links.push(
            { text: "Fill Out Client Form", route: "/clientRegistration" },
            { text: "Logout", route: "/logout" }
          );
        } else {
          this.links = [];
          this.links.push(
            { text: "Home", route: "/" },
            { text: "Edit Profile", route: "/editProfile" },
            { text: "View Classes", route: "/registerForClass" },
            { text: "Package Management", route: "/clientPackageManagement" },
            { text: "Reset Password", route: "/resetPassword" },
            { text: "Videos", route: "/videos" },
            { text: "Logout", route: "/logout" }
          );
        }
      }
    }
  },
  watch: {},
  components: { HeaderLogo },
};
</script>

<style scoped></style>
