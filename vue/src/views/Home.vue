<template>
  <div class="home">
    <top-header></top-header>
    <header>Sattva</header>
    <p>You must be authenticated to see this</p>

    <v-container>
      <v-row>
        <v-col v-for="n in 3" :key="n" cols="12" sm="4">
        <home-card></home-card>

        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import TopHeader from "../components/TopHeader.vue";
import HomeCard from "../components/HomeCard.vue";

export default {
  name: "home",
  components: {
    TopHeader,
    HomeCard
  },
};
</script>

<style></style>