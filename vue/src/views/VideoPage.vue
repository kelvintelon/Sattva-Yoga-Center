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
          <td>{{ item }}</td>
          <td> <v-btn fab elevation="3" color="orange" @click="chosenVideo(item)"><v-icon>mdi-play-circle</v-icon></v-btn></td>
        </tr>
      </tbody>
    </template>
    
  </v-simple-table>
    


  </v-container>
</template>

<script>

import VideoComponent from "../components/VideoComponent.vue"
// import VideoService from "../services/VideoService";

export default {
    
name:"video-page",
components: {
    VideoComponent
},
data() {
  return {
    listOfFileNames: [],
    showVideo: true,
    chosenFileName: "",
  }
},
created() {
  if (this.$store.state.token == '') {
    this.$router.push({ name: "login" });
      }
      if (this.$store.state.user.username != "admin" && !this.$store.state.clientDetails.is_allowed_video) {
        alert("Please contact the owner to get permissions for videos")
        this.$router.push({ name: "home" });
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