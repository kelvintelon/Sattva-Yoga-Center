<template>
  <div>
    <admin-account-quick-links
      v-show="adminIsLoggedin"
    ></admin-account-quick-links>
    <user-account-quick-links
      v-show="userIsLoggedin"
    ></user-account-quick-links>
  </div>
</template>

<script>
import clientDetailService from "../services/ClientDetailService";
import adminAccountQuickLinks from "../components/AdminAccountQuickLinks.vue";
import userAccountQuickLinks from "../components/UserAccountQuickLinks.vue";

export default {
  name: "profile-page",
  components: {
    adminAccountQuickLinks,
    userAccountQuickLinks,
  },
  data() {
    return {
      clientProfile: {},
      adminIsLoggedin: false,
      userIsLoggedin: false,
    };
  },
  created() {
    this.findClientDetails();
  },
  methods: {
    findClientDetails() {
      this.clientProfile = this.$store.state.clientDetails;

      if (this.$store.state.user.username != "admin") {
        this.adminIsLoggedin = false;
        this.userIsLoggedin = true;
        // in case it can't retrieve the client details from the store/local storage
        if (
          Object.keys(this.clientProfile).length === 0 ||
          this.$store.state.clientDetails.client_id == 0
        ) {
          clientDetailService
            .getClientDetailsOfLoggedInUser()
            .then((response) => {
              this.clientProfile = response.data;
              this.$store.commit("SET_CLIENT_DETAILS", response.data);
              // in case the api call never found the client because they hadn't registered
              if (
                Object.keys(this.$store.state.clientDetails).length === 0 ||
                this.$store.state.clientDetails.client_id == 0
              ) {
                this.$router.push({ name: "clientRegistration" });
              }
            })
            .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            }
      });
        }
      } else {
        this.adminIsLoggedin = true;
        this.userIsLoggedin = false;
      }
    },
  },
};
</script>

<style>
</style>