<template>
  <div>
    <top-header></top-header>

    <h1>{{ clientProfile.first_name }} {{ clientProfile.last_name }}</h1>
    <h1>{{ clientProfile.street_address}} {{clientProfile.state_abbreviation}} {{ clientProfile.zip_code}} </h1>
    <h1>{{ clientProfile.email }}</h1>
    <h1>{{ clientProfile.phone_number }}</h1>
  </div>
</template>

<script>
import TopHeader from "../components/TopHeader.vue";

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
    this.checkUser();
  },
  methods: {
    checkUser() {
      this.clientProfile = this.$store.state.clientDetails;
    },
  },
};
</script>

<style>
</style>