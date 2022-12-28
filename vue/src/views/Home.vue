<template>
  <v-container>
    <top-header></top-header>
    <v-row class="mb-lg-16">
      <v-col cols="4">
        <home-card></home-card>
      </v-col>
      <v-col cols="4">
        <welcome-card></welcome-card>
      </v-col>
      <v-col cols="4">
        <about-card></about-card>
      </v-col>
    </v-row>
    <info-footer></info-footer>
  </v-container>
</template>

<script>

import HomeCard from "../components/HomeCard.vue";
import TopHeader from '../components/TopHeader.vue';
import WelcomeCard from "@/components/WelcomeCard.vue";
import AboutCard from "@/components/AboutCard.vue";
import InfoFooter from "@/components/InfoFooter.vue";


export default {
  name: "home",
  components: {
    InfoFooter,
    AboutCard,
    WelcomeCard,
    HomeCard,
    TopHeader,

  },
};
</script>

<style></style>