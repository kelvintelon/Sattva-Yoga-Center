<template>
  <v-container>
    <v-card v-if="showVideo" class="mb-7">
      <v-row>
        <v-spacer></v-spacer>
        <v-col>
          <video-component :chosenFileName="chosenFileName"></video-component
        ></v-col>
        <v-spacer></v-spacer>
      </v-row>
      <v-row align="center" justify="center">
        <v-spacer></v-spacer>
        <v-col align="center" justify="center">
          <v-btn @click="closeOutPlayer">CLOSE OUT PLAYER</v-btn>
        </v-col>
        <v-spacer></v-spacer>
      </v-row>
    </v-card>

    <v-simple-table height="900px">
      <template v-slot:default>
        <thead>
          <tr>
            <th class="text-left">Name</th>
            <th class="text-left">Watch</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in listOfFileNames" :key="item">
            <td>{{ item.name }}</td>
             <td v-if="matchPlayerToThumbnail(item)"><div @click="playVideo(item)"><video-component  :chosenFileName="item.videoLink" ></video-component></div></td>

            <td v-else>
              <v-btn
                fab
                elevation="3"
                color="orange"
                
                @click="chosenVideo(item.videoLink)"
                ><v-icon>mdi-play-circle</v-icon></v-btn
              >
            </td>
          </tr>
        </tbody>
      </template>
    </v-simple-table>
  </v-container>
</template>

<script>
import VideoComponent from "../components/VideoComponent.vue";
import clientDetailService from "../services/ClientDetailService";
// import VideoService from "../services/VideoService";

export default {
  name: "video-page",
  components: {
    VideoComponent,
  },
  data() {
    return {
      listOfFileNames: [
        
      ],
      showVideo: false,
      hideThumbnail: false,
      chosenFileName: "",
      
    };
  },
  created() {
    if (this.$store.state.token == "") {
      this.$router.push({ name: "login" });
    }
    if (this.$store.state.user.username != "admin") {
      clientDetailService.getClientDetailsOfLoggedInUser().then((response) => {
        if (response.data.client_id != 0) {
          this.$store.commit("SET_CLIENT_DETAILS", response.data);
          if (response.data.is_allowed_video == false) {
            this.$store.state.clientDetails.is_allowed_video;

            alert("Please contact the owner to get permissions for videos");
            this.$router.push({ name: "home" });
          }
        }
      });
    } else {
      clientDetailService
        .getClientDetailsOfAdminUser()
        .then((response) => {
          response;
          this.checkLinks();
        })
        .catch((error) => {
          const response = error.response;
          if (response.status === 401) {
            this.$store.state.token = "";
            this.links = [];
            this.links.push(
              { text: "Login", route: "/login" },
              { text: "Register", route: "/register" }
            );
            if (
              this.$router.currentRoute.name != "home" &&
              this.$router.currentRoute.name != "login" &&
              this.$router.currentRoute.name != "register"
            ) {
              this.$router.push({ name: "login" });
            }
            this.checkLinks();
          }
          if (response.status == 403) {
            this.$router.push({ name: "logout" });
          }
        });
    }
    // VideoService.getVideoFilenames().then((response) => {
    //   if (response.status == 200) {
    //     this.listOfFileNames = response.data;
    //   }
    // })
  },
  methods: {
    matchPlayerToThumbnail(item) {
      if (this.chosenFileName != item.videoLink) {
        return true;
      } else {
        return false;
      }
    },
    closeOutPlayer() {
      this.showVideo = false;
      this.chosenFileName = "";
      this.hideThumbnail = false;
    },
    playVideo(item) {
      this.$vuetify.goTo(0);
      this.chosenVideo(item.videoLink)
      this.hideThumbnail = true;
    },
    chosenVideo(fileName) {
      if (this.showVideo) {
        this.showVideo = false;
      }
      setTimeout(() => {
        this.chosenFileName = fileName;

        this.showVideo = true;
      }, 250);
    },
  },
};
</script>

<style>
</style>