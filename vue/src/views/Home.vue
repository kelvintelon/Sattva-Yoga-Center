<template>
  <v-container>
    <top-header></top-header>
    <v-row>
      <v-col v-for="n in 3" :key="n" cols="12" sm="4">
        <home-card></home-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>

import HomeCard from "../components/HomeCard.vue";
import TopHeader from '../components/TopHeader.vue';


export default {
  name: "home",
  components: {

    HomeCard,
    TopHeader,
    
  },
};
</script>

<style></style>````