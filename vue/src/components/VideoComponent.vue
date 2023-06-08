<template>
  <v-container>
    <div>
      <video-player :options="videoOptions" />
    </div>
  </v-container>
</template>

<script>
import VideoPlayer from '@/components/VideoPlayer.vue';
import VideoService from "../services/VideoService";


export default {
    name: 'VideoComponent',
  components: {
    VideoPlayer
  },
  data() {
    return {
      videoOptions: {
        autoplay: true,
        controls: true,
        sources: [
          {
            src:
              require("@/assets/videos/earth.mp4"),
              type: 'video/mp4'
          }
        ]
      },
      videoObject: {},
    };
  },
  created() {
    VideoService.getVideoFile().then((response) => {
        this.videoObject = response.data;
    })
  }

};
</script>

<style>
</style>