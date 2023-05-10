<template>
  <v-container>
    <!-- ANIMATION IS THIS LOTTIE COMPONENT -->
    <v-container v-if="animationLoader" alt="Yoga lotus flower by Rebecca Southall">
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
    }' class='largeInvisible' v-if="!animationLoader && !expandTable">

      <br>
      <br>
    </div>
    <v-expand-x-transition appear>
      <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px" v-if="!animationLoader && expandTable"
        v-model="expandTable">
        <v-data-table :headers="tableHeaders" :items="events" class="elevation-1">
          <template v-slot:top>
            <v-toolbar flat>
              <v-toolbar-title style="color: rgba(245, 104, 71, 0.95)"><strong>Next Available
                  Classes</strong></v-toolbar-title>
              <v-spacer></v-spacer>
            </v-toolbar>
          </template>
          <template v-slot:[`item.actions`]="{ item }">
            <v-icon class="mr-2" @click="SignUp(item)" style="color: rgba(245, 104, 71, 0.95)">
              mdi-account-plus
            </v-icon>
          </template>
        </v-data-table>
      </v-card>
    </v-expand-x-transition>
    <!--Studio Guidelines Card and About Sattva  -->
    <br v-if="!animationLoader && !expandStudioGuidelines">
    <br v-if="!animationLoader && !expandStudioGuidelines">
    <br v-if="!animationLoader">
    <div v-intersect.once='{
      handler: onStudioGuidelinesIntersect,
      options: {
        threshold: [1.0]
      }
    }' class='largeInvisible' v-if="!animationLoader && !expandStudioGuidelines">

      <br>
      <br>
    </div>
    <v-fab-transition appear origin="bottom right">
      <v-card class="mx-auto my-2 rounded-xl cardBorder" max-width="900px" min-width="200px"
        v-if="!animationLoader && expandStudioGuidelines" ripple v-model="expandStudioGuidelines">
        <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>Studio Guidelines</strong></v-card-title>
        <v-card-subtitle>Prior to attending a class at Sattva Yoga Center, please review the following guidelines. We
          believe they will help you get the most out of your practice.</v-card-subtitle>
        <v-divider class="mx-4" color="red"></v-divider>
        <v-card-text>
          <v-row align="center" class="mx-0">
            <div>
              - Do not eat anything for at least 2 hours before class.
            </div>
            <div>
              - Please arrive at least 10 minutes early before scheduled start time.
            </div>
            <div>
              - Wear comfortable clothing. Do not wear perfumes or scented lotions.
            </div>
            <div>
              - Please remove footwear (shoes, filp flops) in the lobby.
            </div>
            <div>
              - Bring a mat and a hand towel. Due to COVID we do not have rental mats at the studio.
            </div>
            <div>
              - If you have any restrictions, please notify the instructor before class.
            </div>
            <div>
              - Please help us in keeping the practice space neat and orderly.
            </div>
            <div>
              - Keep your attention to yourself during the practice.
            </div>
            <div>
              - Please be mindful and quiet in the yoga room.</div>
          </v-row>
        </v-card-text>
        <div v-intersect.once='{
          handler: onNewClientIntersect,
          options: {
            threshold: [1.0]
          }
        }' class='smallInvisible' v-if="!animationLoader && !expandNewClient" />
        <v-slide-y-reverse-transition appear v-if="!animationLoader && expandNewClient">
          <v-card v-if="!animationLoader && expandNewClient" v-model="expandNewClient">
            <v-divider class="mx-4" color="red"></v-divider>
            <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>New Client</strong></v-card-title>
            <v-card-subtitle>
              <v-card-text>
                <v-row align="center" justify="center">
                  <v-col cols="2">
                    <v-img src="@/assets/new.png" contain></v-img>
                  </v-col>
                  <v-col cols="10">
                    <div>If you're new to yoga or the studio, please come to a Beginner/ Basic Class or an All Levels
                      Class.
                    </div>
                  </v-col>
                </v-row>
              </v-card-text></v-card-subtitle>
          </v-card>
        </v-slide-y-reverse-transition>
        <div v-intersect.once='{
          handler: onPurposeMissionIntersect,
          options: {
            threshold: [1.0]
          }
        }' class='smallInvisible' v-if="!animationLoader && !expandPurposeMission" />
        <v-slide-y-reverse-transition appear v-if="!animationLoader && expandPurposeMission">
          <v-card v-if="!animationLoader && expandPurposeMission" v-model="expandPurposeMission">
            <v-divider class="mx-4" color="red"></v-divider>
            <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>Purpose/Mission</strong></v-card-title>
            <v-card-subtitle>
              <v-card-text>
                <v-row align="center" justify="center">
                  <v-col cols="2">
                    <v-img src="@/assets/pigeon-bird-icon.png" contain height="100"></v-img>
                  </v-col>
                  <v-col cols="10">
                    <div>Our mission is to serve our clients on their path towards peace. We believe that a breath-focused
                      yoga practice can guide everyone towards peace.
                    </div>
                  </v-col>
                </v-row>
              </v-card-text></v-card-subtitle>
          </v-card>
        </v-slide-y-reverse-transition>
        <div v-intersect.once='{
          handler: onSattvaIntersect,
          options: {
            threshold: [1.0]
          }
        }' class='smallInvisible' v-if="!animationLoader && !expandSattva" />
        <v-slide-y-reverse-transition appear v-if="!animationLoader && expandSattva">
          <v-card v-if="!animationLoader && expandSattva" v-model="expandSattva">
            <v-divider class="mx-4" color="red"></v-divider>
            <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>Sattva</strong></v-card-title>
            <v-card-subtitle>
              <v-card-text>
                <v-row align="center" justify="center">
                  <v-col cols="2">
                    <v-img src="@/assets/lotus-icon.png" contain></v-img>
                  </v-col>
                  <v-col cols="10">
                    <div>In Sanskrit, the ancient language of Yoga, "Sattva" has multiple meanings, some of them being
                      essence, purity, luminosity, and equanimity.
                    </div>
                  </v-col>
                </v-row>
              </v-card-text></v-card-subtitle>
          </v-card>
        </v-slide-y-reverse-transition>
        <div v-intersect.once='{
          handler: onYogaIntersect,
          options: {
            threshold: [1.0]
          }
        }' class='smallInvisible' v-if="!animationLoader && !expandYoga" />
        <v-slide-y-reverse-transition appear v-if="!animationLoader && expandYoga">
          <v-card v-if="!animationLoader && expandYoga" v-model="expandYoga">
            <v-divider class="mx-4" color="red"></v-divider>
            <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>Yoga</strong></v-card-title>
            <v-card-subtitle>
              <v-card-text>
                <v-row align="center" justify="center">
                  <v-col cols="2">
                    <v-img src="@/assets/meditation-yoga-icon.png" contain height="100"></v-img>
                  </v-col>
                  <v-col cols="10">
                    <div>Yoga is a state where all movement of thought is suspended. It is manifested through one-pointed
                      focus. In this state, one is able to experience clarity and peace.
                    </div>
                  </v-col>
                </v-row>
              </v-card-text></v-card-subtitle>
          </v-card>
        </v-slide-y-reverse-transition>
        <div v-intersect.once='{
          handler: onAcknowledgementsIntersect,
          options: {
            threshold: [1.0]
          }
        }' class='smallInvisible' v-if="!animationLoader && !expandAcknowledgements" />
        <v-slide-y-reverse-transition appear v-if="!animationLoader && expandAcknowledgements">
          <v-card v-if="!animationLoader && expandAcknowledgements" v-model="expandAcknowledgements">
            <v-divider class="mx-4" color="red"></v-divider>
            <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>Acknowledgements</strong></v-card-title>
            <v-card-subtitle>
              <v-card-text>
                <v-row align="center" justify="center">
                  <v-col cols="2">
                    <v-img src="@/assets/handshake-icon.png" contain height="100"></v-img>
                  </v-col>
                  <v-col cols="10">
                    <div>We are grateful to all our teachers for guiding us on the path of Yoga. We hope to stay true to
                      the
                      path that our teachers have shown us. We are also grateful to all of the practitioners at our studio
                      from whom we learn daily.
                    </div>
                  </v-col>
                </v-row>
              </v-card-text></v-card-subtitle>
          </v-card>
        </v-slide-y-reverse-transition>
      </v-card>
    </v-fab-transition>
    <!--Bhagavad Gita Card  -->
    <br v-if="!animationLoader && !expandBhagavadGita">
    <br v-if="!animationLoader && !expandBhagavadGita">
    <br v-if="!animationLoader">
    <div v-intersect.once='{
      handler: onBhagavadGitaIntersect,
      options: {
        threshold: [1.0]
      }
    }' class='smallInvisible' v-if="!animationLoader && !expandBhagavadGita">

      <br>
      <br>
    </div>
    <v-slide-x-transition appear>
      <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px"
        v-if="!animationLoader && expandBhagavadGita" v-model="expandBhagavadGita">
        <v-card-text>
          <v-row align="center" justify="center">
            <v-col cols="6">
              <v-img src="@/assets/thiruman.jpg" contain></v-img>
            </v-col>
            <v-spacer></v-spacer>
          </v-row>
        </v-card-text>
        <v-card-text>
          <v-row>
            <v-spacer></v-spacer>
            <v-col>
              <div><em>
                  "When the Light of Knowledge shines through all the gates of this body, then it should be known that
                  Sattva is dominant"</em><br>
                The Bhagavad Gita, 14:11</div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-slide-x-transition>
    <!-- Youtube Videos -->
    <br v-if="!animationLoader && !expandYoutubeVideos">
    <br v-if="!animationLoader && !expandYoutubeVideos">
    <br v-if="!animationLoader">
    <div v-intersect.once='{
      handler: onYoutubeVideosIntersect,
      options: {
        threshold: [1.0]
      }
    }' class='smallInvisible' v-if="!animationLoader && !expandYoutubeVideos">

      <br>
      <br>
    </div>
    <v-fab-transition appear>
    <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px" 
    v-if="!animationLoader && expandYoutubeVideos" v-model="expandYoutubeVideos">
      <v-card-title style="color: rgba(245, 104, 71, 0.95)" class="hidden-sm-and-up"><strong>Videos</strong></v-card-title>
      <v-card-text align="center" justify="center" class="hidden-sm-and-down">
        <v-btn
        color="rgba(245, 104, 71, 0.95)"
              fab
              x-large
              dark
              class="mx-2"
              outlined
              @click="firstYoutubeVideo = !firstYoutubeVideo"
            >
              <v-icon  color="rgba(245, 104, 71, 0.95)">mdi-chevron-left</v-icon>
            </v-btn>
        <iframe v-if="!firstYoutubeVideo" width="560" height="315" src="https://www.youtube.com/embed/QpZqLIIJQ2Q" title="YouTube video player"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
          allowfullscreen></iframe>
          <iframe v-if="firstYoutubeVideo" width="560" height="315" src="https://www.youtube.com/embed/yf-tnhoHZE0" title="YouTube video player"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
          allowfullscreen></iframe>
          <v-btn
            color="rgba(245, 104, 71, 0.95)"
              fab
              x-large
              dark
              class="mx-2"
              outlined
              @click="firstYoutubeVideo = !firstYoutubeVideo"
            >
              <v-icon >mdi-chevron-right</v-icon>
            </v-btn>
      </v-card-text>
      <v-card-text align="center" justify="center" class="hidden-sm-and-up">
        <div><a href="https://www.youtube.com/embed/QpZqLIIJQ2Q">Going to Sattva Yoga Center</a></div>
        <div><a href="https://www.youtube.com/embed/yf-tnhoHZE0">Inside of Sattva Yoga Center</a></div>  
      </v-card-text>
    </v-card>
  </v-fab-transition>
    <!--Instructor Training  -->
    <br v-if="!animationLoader && !expandInstructorTraining">
    <br v-if="!animationLoader && !expandInstructorTraining">
    <br v-if="!animationLoader">
    <div v-intersect.once='{
      handler: onInstructorTrainingIntersect,
      options: {
        threshold: [1.0]
      }
    }' class='largeInvisible' v-if="!animationLoader && !expandInstructorTraining">
      <br>
      <br>
    </div>
    <v-expand-x-transition appear>
      <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px"
        v-if="!animationLoader && expandInstructorTraining" v-model="expandInstructorTraining">
        <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>Instructor Training</strong></v-card-title>
        <v-card-subtitle>September 2022 Update: Training is on hold due to the pandemic.</v-card-subtitle>
        <v-divider class="mx-4" color="red"></v-divider>
        <v-card-title style="color: rgba(245, 104, 71, 0.75)">Training Objectives</v-card-title>
        <v-card-text>
          <v-row align="center" class="mx-0">
            <div>- To establish/enhance your personal practice and understanding of Yoga</div>
            <div>- Building upon the personal practice and learning the skills needed to serve as an instructor of Hatha
              Yoga</div>
          </v-row>
        </v-card-text>
        <v-divider class="mx-4" color="red"></v-divider>
        <v-card-title style="color: rgba(245, 104, 71, 0.75)">Certification</v-card-title>
        <v-card-text>
          <v-row align="center" class="mx-0">
            <div> Sattva Yoga Center is a Registered Yoga School with the Yoga Alliance. The length of the training
              program is 250 hours. This meets and exceeds the RYT 200 standards set by the Yoga Alliance. Upon successful
              completion of the training (including the successful completion of a comprehensive final exam), a
              certificate will be issued</div>
            <div><br>Please call us to learn more about topics covered, cost, and dates.
              <br>(313) 274-3995
            </div>
          </v-row>
        </v-card-text>
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
    }' class='smallInvisible' v-if="!animationLoader && !expandCarousel">

      <br>
      <br>
    </div>

    <v-fab-transition appear>
      <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px"
        v-if="!animationLoader && expandCarousel" v-model="expandCarousel">
        <v-carousel cycle :interval="3000" height="400" hide-delimiter eager hide-delimiters>
          <v-carousel-item v-for="(item, i) in items" :key="i" :src="item.src" fade reverse-transition="fade-transition">
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
    expandStudioGuidelines: false,
    studioGuidelinesIntersectCount: 0,
    expandNewClient: false,
    newClientIntersectCount: 0,
    expandPurposeMission: false,
    purposeMissionIntersectCount: 0,
    expandSattva: false,
    sattvaIntersectCount: 0,
    expandYoga: false,
    yogaIntersectCount: 0,
    expandAcknowledgements: false,
    acknowledgementsIntersectCount: 0,
    expandInstructorTraining: false,
    instructorTrainingIntersectCount: 0,
    expandBhagavadGita: false,
    bhagavadGitaIntersectCount: 0,
    expandYoutubeVideos: false,
    youtubeVideosIntersectCount: 0,
    tableHeaders: [
      {
        text: "Class Description",
        value: "quick_details",
        align: "start",
      },
      { text: "Sign Up", value: "actions", sortable: false },
    ],
    firstYoutubeVideo: false,
    events: [],
  }),
  computed: {
  mobile() {
    return this.$vuetify.breakpoint.sm
  },
},
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
    onStudioGuidelinesIntersect(isIntersecting) {
      this.studioGuidelinesIntersectCount++;
      if (this.studioGuidelinesIntersectCount == 1) {

        this.expandStudioGuidelines = isIntersecting;
      }
    },
    onNewClientIntersect(isIntersecting) {
      this.newClientIntersectCount++;
      if (this.newClientIntersectCount == 1) {

        this.expandNewClient = isIntersecting;
      }
    },
    onPurposeMissionIntersect(isIntersecting) {
      this.purposeMissionIntersectCount++;
      if (this.purposeMissionIntersectCount == 1) {

        this.expandPurposeMission = isIntersecting;
      }
    },
    onSattvaIntersect(isIntersecting) {
      this.sattvaIntersectCount++;
      if (this.sattvaIntersectCount == 1) {

        this.expandSattva = isIntersecting;
      }
    },
    onYogaIntersect(isIntersecting) {
      this.yogaIntersectCount++;
      if (this.yogaIntersectCount == 1) {

        this.expandYoga = isIntersecting;
      }
    },
    onAcknowledgementsIntersect(isIntersecting) {
      this.acknowledgementsIntersectCount++;
      if (this.acknowledgementsIntersectCount == 1) {

        this.expandAcknowledgements = isIntersecting;
      }
    },
    onInstructorTrainingIntersect(isIntersecting) {
      this.instructorTrainingIntersectCount++;
      if (this.instructorTrainingIntersectCount == 1) {

        this.expandInstructorTraining = isIntersecting;
      }
    },
    onBhagavadGitaIntersect(isIntersecting) {
      this.bhagavadGitaIntersectCount++;
      if (this.bhagavadGitaIntersectCount == 1) {

        this.expandBhagavadGita = isIntersecting;
      }
    },
    onYoutubeVideosIntersect(isIntersecting) {
      this.youtubeVideosIntersectCount++;
      if (this.youtubeVideosIntersectCount == 1) {

        this.expandYoutubeVideos = isIntersecting;
      }
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
.largeInvisible {
  width: 100%;
  height: 450px;

  bottom: 200px;
}

.smallInvisible {
  width: 100%;
  height: 250px;
  bottom: 200px;
}

.parallax {
  z-index: 99;
}</style>
