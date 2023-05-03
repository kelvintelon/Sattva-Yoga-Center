<template>
  <v-container>
    <!-- ANIMATION IS THIS LOTTIE COMPONENT -->
    <v-card v-if="animationLoader">
      <lottie :options="defaultOptions" :width="500" :height="500"/>
    </v-card>
    <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px" v-if="!animationLoader">
      <v-img height="350px" src="@/assets/out-door-yoga-practice.jpg"></v-img>
      <v-card-title><strong>Welcome</strong></v-card-title>
      <v-card-text>
        <v-row align="center" class="mx-0">
          Sattva has been serving our Dearborn, Michigan community since January 2007. The central focus of our classes is the breath. Our aim is to facilitate a healthy body and a supple mind.
        </v-row>
      </v-card-text>
      <v-divider class="mx-4"></v-divider>
      <v-row><v-col><v-card-subtitle style="font-size: medium">upcoming classes</v-card-subtitle>
      
      <v-card-text>
        <v-chip-group
            v-model="selection"
            active-class="deep-orange accent-4 white--text"
            column
            class="d-flex align-start flex-column"
        >
          <v-chip>5:30PM</v-chip>

          <v-chip>7:30PM</v-chip>

          <v-chip>8:00PM</v-chip>

          <v-chip>9:00PM</v-chip>
        </v-chip-group>
      </v-card-text>
    </v-col>
    <v-col align="right"><v-card-subtitle style="font-size: medium" align="right">available packages</v-card-subtitle>
      
      <v-card-text>
        <v-chip-group
            v-model="packageSelection"
            active-class="deep-orange accent-4 white--text"
            column
            class="d-flex align-end flex-column"
        >
          <v-chip>First Class $10</v-chip>

          <v-chip>First Month  $40</v-chip>

          <v-chip>Gift</v-chip>

          <v-chip>More</v-chip>
          
        </v-chip-group>
      </v-card-text>
    </v-col>
    </v-row>
      <!-- <v-divider class="mx-4"></v-divider>
      <v-card-text>In Sanskrit, the ancient language of Yoga, "Sattva" has multiple meanings, some of them being essence, purity, luminosity, and equanimity.</v-card-text> -->
      <v-divider class="mx-4"></v-divider>
      <v-card-text>Yoga is a state where all movement of thought is suspended. It is manifested through one-pointed focus. In this state, one is able to experience clarity and peace.</v-card-text>
    </v-card>


  </v-container>
</template>

<script>
 import * as animationData from "@/assets/animations/yog2loader.json";

export default {
  name: "home",
 
  data: () => ({
    loading: false,
    animationLoader: true,
    selection: 0,
    packageSelection: 3,
    defaultOptions: {
      animationData: animationData.default
    }
  }),

  methods: {
    reserve() {
      this.loading = true

      setTimeout(() => (this.loading = false), 2000)
    },
  },
  created() {
    setTimeout(() => (this.animationLoader = false), 3000);
  }
};
</script>

<style></style>
