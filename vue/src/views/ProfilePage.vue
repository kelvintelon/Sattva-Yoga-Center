<template>
  <div>
    <top-header></top-header>

    <h1>{{ clientProfile.first_name }} {{ clientProfile.last_name }}</h1>
    <h1>
      {{ clientProfile.street_address }} {{ clientProfile.state_abbreviation }}
      {{ clientProfile.zip_code }}
    </h1>
    <h1>{{ clientProfile.email }}</h1>
    <h1>{{ clientProfile.phone_number }}</h1>
  </div>
</template>

<script>
import TopHeader from "../components/TopHeader.vue";
import clientDetailService from "../services/ClientDetailService";

export default {
  name: "profile-page",
  components: {
    TopHeader,
  },
  data() {
    return {
      clientProfile: {},
    };
  },
  mounted() {
    this.findClientDetails();
  },
  methods: {
    findClientDetails() {
      this.clientProfile = this.$store.state.clientDetails;
      // in case it can't retrieve the client details from the store/local storage
      if (Object.keys(this.clientProfile).length === 0) {
        clientDetailService
          .getClientDetailsOfLoggedInUser()
          .then((response) => {
            this.clientProfile = response.data;
            this.$store.commit("SET_CLIENT_DETAILS", response.data);
          });
      }
    },
  },
};
</script>

<style>
</style>