<template>
  <v-container>
    <v-simple-table height="400px" v-if="!showVideo">
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
    
     <v-card v-else>
    <v-row>
      <v-spacer></v-spacer>
      <v-col>
    <video-component :chosenFileName="chosenFileName"></video-component></v-col>
    <v-spacer></v-spacer>
  </v-row>
  <v-row>
    <v-spacer></v-spacer>
    <v-col>
      <v-btn @click="showVideo = false">CLOSE OUT PLAYER</v-btn>
    </v-col>
    <v-spacer></v-spacer>
  </v-row>
</v-card>

  </v-container>
</template>

<script>

import VideoComponent from "../components/VideoComponent.vue"
import VideoService from "../services/VideoService";

export default {
    
name:"video-page",
components: {
    VideoComponent
},
data() {
  return {
    listOfFileNames: [],
    showVideo: false,
    chosenFileName: "",
  }
},
created() {
  VideoService.getVideoFilenames().then((response) => {
    if (response.status == 200) {
      this.listOfFileNames = response.data;
    }
  })
},
methods: {
  chosenVideo(fileName){
    this.showVideo = true;
    this.chosenFileName = fileName;
  }
},  
}

</script>

<style>

</style>