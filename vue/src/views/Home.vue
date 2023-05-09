<template>
  <v-container>
    <!-- ANIMATION IS THIS LOTTIE COMPONENT -->
    <v-container v-if="animationLoader">
      <lottie :options="defaultOptions" :width="500" :height="500" />
    </v-container>
    <!-- Welcome Card -->
    <welcome-card v-if="!animationLoader"></welcome-card>
    <!-- Table Card -->
    <br v-if="!animationLoader && !expandTable">
    <br v-if="!animationLoader && !expandTable">
    <br v-if="!animationLoader">
    <div v-intersect.once='{
      handler: onTableIntersect,
      options: {
        threshold: [1.0]
      }
    }' class='invisible' v-if="!animationLoader && !expandTable">

      <br>
      <br>
    </div>
    <v-expand-x-transition appear>
    <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px"
    v-if="!animationLoader && expandTable" v-model="expandTable">
        <v-data-table
        :headers="tableHeaders"
        :items="events"
        class="elevation-1"
        
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title style="color: rgba(245, 104, 71, 0.95)">Next Available Classes</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
          </v-toolbar>
        </template>
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon small class="mr-2" @click="SignUp(item)">
            mdi-account-plus
          </v-icon>
        </template>
      </v-data-table>
      </v-card>
    </v-expand-x-transition>
    <!-- Carousel Card -->
    <br v-if="!animationLoader && !expandCarousel">
    <br v-if="!animationLoader && !expandCarousel">
    <br v-if="!animationLoader">
    <div v-intersect.once='{
      handler: onCarouselIntersect,
      options: {
        threshold: [1.0]
      }
    }' class='invisible' v-if="!animationLoader && !expandCarousel">

      <br>
      <br>
    </div>

    <v-fab-transition appear>
      <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px"
        v-if="!animationLoader && expandCarousel" v-model="expandCarousel">
        <v-carousel cycle :interval="3000" height="400" hide-delimiter  eager   hide-delimiters>
          <v-carousel-item v-for="(item, i) in items" :key="i" :src="item.src" transition="fade-transition"
            reverse-transition="fade-transition">
          </v-carousel-item>
        </v-carousel>
      </v-card>
    </v-fab-transition>
    <br>
    
    
  </v-container>
</template>

<script>
import * as animationData from "@/assets/animations/yog2loader.json";
import WelcomeCard from "../components/WelcomeCard.vue";
import eventService from "../services/EventService";


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
      { src: require('@/assets/sattvaWebsite/SattvaYoga6.jpeg') },
      { src: require('@/assets/sattvaWebsite/SattvaYoga3.jpeg') },
      { src: require('@/assets/sattvaWebsite/SattvaYoga7.jpeg') },
      { src: require('@/assets/sattvaWebsite/SattvaYoga4.jpeg') },
      { src: require('@/assets/sattvaWebsite/SattvaYoga8.jpeg') },
      { src: require('@/assets/sattvaWebsite/leaf.jpg') },
      { src: require('@/assets/sattvaWebsite/SattvaYoga9.jpeg') },
    ],
    expandCarousel: false,
    carouselIntersectCount: 0,
    expandTable: false,
    tableIntersectCount: 0,
    tableHeaders: [
        {
          text: "Class Description",
          value: "quick_details",
          align: "start",
        },
        { text: "Sign Up", value: "actions", sortable: false },
      ],
      events: [],
  }),

  methods: {
    onCarouselIntersect(isIntersecting) {
      this.carouselIntersectCount++;
      if (this.carouselIntersectCount == 1) {

        this.expandCarousel = isIntersecting;
      }
    },
    onTableIntersect(isIntersecting) {
      this.tableIntersectCount++;
      if (this.tableIntersectCount == 1) {

        this.expandTable = isIntersecting;
      }
    },
    reserve() {
      this.loading = true

      setTimeout(() => (this.loading = false), 2000)
    },
    getEventTable() {
      eventService
        .get100Events()
        .then((response) => {
          if (response.status == 200) {
            this.$store.commit("SET_EVENT_LIST", response.data);
            this.events = response.data;
          } else {
            alert("Error retrieving class information");
          }
        });
    },
    SignUp() {
      if (this.$store.state.token == "") { 
        this.$router.push({ name: "login" });
      } else {
        if (this.$store.state.user.username == "admin") {
          this.$router.push({ name: "class-management" });
        } else {
          this.$router.push({ name: "class-registration" });
        }
      }
    },
  },
  created() {
    this.getEventTable();
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
  height: 450px;

  bottom: 200px;
}
</style>
