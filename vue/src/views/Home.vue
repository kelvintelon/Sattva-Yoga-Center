<template>
  <v-container>
    <!-- ANIMATION IS THIS LOTTIE COMPONENT -->
    <v-container v-if="animationLoader" >
      <lottie :options="defaultOptions" :width="500" :height="500" />
    </v-container>
    <!-- Welcome Card -->
    <welcome-card v-if="!animationLoader"></welcome-card>
    <!-- Carousel Card -->
    <br v-if="!animationLoader && !expandCarousel">
    <br v-if="!animationLoader && !expandCarousel">
    <br v-if="!animationLoader">
    <div
  v-intersect.once='{
    handler: onIntersect,
    options: {
      threshold: [1.0]
    }
  }'
  class='invisible' v-if="!animationLoader && !expandCarousel">
  
  <br>
  <br>
  </div> 
  
  <v-fab-transition appear>
    <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px" v-if="!animationLoader && expandCarousel" v-model="expandCarousel">
      <v-carousel cycle height="400" hide-delimiter-background show-arrows="false"  interval="3000" eager>
        <v-carousel-item v-for="(item, i) in items" :key="i" :src="item.src" transition="fade-transition"    reverse-transition="fade-transition">
        </v-carousel-item>
      </v-carousel>
    </v-card>
  </v-fab-transition>
  <!-- Calendar Card -->
  </v-container>
</template>

<script>
import * as animationData from "@/assets/animations/yog2loader.json";
import WelcomeCard from "../components/WelcomeCard.vue"


export default {
  name: "home",
  components: { WelcomeCard },
  data: () => ({
    loading: false,
    animationLoader: true,
    selection: 0,
    packageSelection: 3,
    defaultOptions: {
      animationData: animationData.default
    },
    items: [
    { src: require('@/assets/sattvaWebsite/SattvaYoga1.jpeg') },
      { src: require('@/assets/sattvaWebsite/SattvaYoga2.jpeg') },
      { src: require('@/assets/sattvaWebsite/SattvaYoga3.jpeg') },
      { src: require('@/assets/sattvaWebsite/SattvaYoga4.jpeg') },
      { src: require('@/assets/sattvaWebsite/leaf.jpg') },
      
    ],
    expandCarousel: false,
    intersectCount: 0,
  }),

  methods: {
    onIntersect(isIntersecting) {
      this.intersectCount++;
      if (this.intersectCount == 1) {
     
      this.expandCarousel = isIntersecting;
      }
    },
    reserve() {
      this.loading = true

      setTimeout(() => (this.loading = false), 2000)
    },
  },
  created() {
    setTimeout(() => (this.animationLoader = false), 3000);
    
  },
  unmounted() {
    this.expandCarousel = false;
  }
};
</script>

<style>
.invisible {
      width: 100%;
      height: 500px;
      
     bottom: 200px;
   }
   </style>
