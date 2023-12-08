<template>
  <v-container>
    <v-card v-if="showVideo" class="mb-7">
    <v-row>
      <v-spacer></v-spacer>
      <v-col>
    <video-component :chosenFileName="chosenFileName"></video-component></v-col>
    <v-spacer></v-spacer>
  </v-row>
  <v-row align="center" justify="center">
    <v-spacer></v-spacer>
    <v-col align="center" justify="center">
      <v-btn @click="showVideo = false">CLOSE OUT PLAYER</v-btn>
    </v-col>
    <v-spacer></v-spacer>
  </v-row>
</v-card>

    <v-simple-table height="400px">
    <template v-slot:default>
      <thead>
        <tr>
          <th class="text-left">
            Name
          </th>
          <th class="text-left">
            Watch
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in listOfFileNames"
          :key="item"
        >
          <td>{{ item.name }}</td>
          <td> <v-btn fab elevation="3" color="orange" @click="chosenVideo(item.videoLinks)"><v-icon>mdi-play-circle</v-icon></v-btn></td>
        </tr>
      </tbody>
    </template>
    
  </v-simple-table>
    


  </v-container>
</template>

<script>

import VideoComponent from "../components/VideoComponent.vue"
import clientDetailService from "../services/ClientDetailService";
// import VideoService from "../services/VideoService";

export default {
    
name:"video-page",
components: {
    VideoComponent
},
data() {
  return {
    listOfFileNames: [{
    videoLinks: "https://storage.cloud.google.com/sattva_yoga_center_videos/09.01.20%20-%20Tuesday-All%20Levels.mp4",
    name: "09.01.20 - Tuesday - All Levels Class"
  },
  {
    videoLinks: "https://storage.cloud.google.com/sattva_yoga_center_videos/09.02.20%20-%20Wednesday-All%20Levels.mp4",
    name: "09.02.20 - Wednesday - All Levels Class"
  },
  {
    videoLinks: "https://storage.cloud.google.com/sattva_yoga_center_videos/09.03.20%20-%20Thursday-All%20Levels.mp4",
    name: "09.03.20 - Thursday - All Levels Class"
  },
  {
    videoLinks: "https://storage.cloud.google.com/sattva_yoga_center_videos/09.04.20%20-%20Friday-All%20Levels.mp4",
    name: "09.04.20 - Friday - All Levels Class"
  },
  {
    videoLinks: "https://storage.cloud.google.com/sattva_yoga_center_videos/09.05.20%20-%20Saturday-All%20Levels.mp4",
    name: "09.05.20 - Saturday - All Levels Class"
  },
  {
    videoLinks: "https://storage.cloud.google.com/sattva_yoga_center_videos/09.06.20%20-%20Sunday-All%20Levels.mp4",
    name: "09.06.20 - Sunday - All Levels Class"
  },
  {
    videoLinks: "https://storage.cloud.google.com/sattva_yoga_center_videos/09.07.20%20-%20Monday-All%20Levels.mp4",
    name: "09.07.20 - Monday - All Levels Class"
  }],
    showVideo: false,
    chosenFileName: "",
  }
},
created() {
  if (this.$store.state.token == '') {
    this.$router.push({ name: "login" });
  }
      if (this.$store.state.user.username != "admin") {

        clientDetailService
            .getClientDetailsOfLoggedInUser()
            .then((response) => {
              if (response.data.client_id != 0) {
                this.$store.commit("SET_CLIENT_DETAILS", response.data);
                if (response.data.is_allowed_video == false) {
                  this.$store.state.clientDetails.is_allowed_video

                  alert("Please contact the owner to get permissions for videos")
                  this.$router.push({ name: "home" });
                }
              }});
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
                this.$router.push({name: "logout"});
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
  chosenVideo(fileName){
    if (this.showVideo) {
    this.showVideo = false;
    }
    setTimeout(() => {
      this.chosenFileName = fileName;
    
    this.showVideo = true;
    }, 250)
    
  }
},  
}

</script>

<style>

</style>