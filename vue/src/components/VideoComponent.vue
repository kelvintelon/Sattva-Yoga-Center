<template>
  <!-- https://videojs.com/guides/vue/ -->
  <v-container>
    <div>
         <video-player id="vid1" :options="videoOptions" />
    </div>
  </v-container>
</template>

<script>
import VideoPlayer from '@/components/VideoPlayer.vue';

export default {
    name: 'VideoComponent',
  components: {
    VideoPlayer
  },
  props: ["chosenFileName"],
  data() {
    return {
      
      localUrl: 'http://localhost:9000/get-file?fileName=',
      // localUrl: 'https://sattva-yoga-app.herokuapp.com/get-file?fileName=',
      exactFileLocation: 'http://localhost:9000/get-file?fileName=09.18.20 - Friday_.mp4',
      fileName: '09.18.20 - Friday_.mp4',

    };
  },
  computed: {
    videoOptions() {
      return {
        autoplay: false,
        controls: true,
        sources: [
          {
            src:
            this.localUrl+this.chosenFileName,
              type: 'video/mp4'
             
          }
        ]
      }
    }
  },
  created() {
    
  },
  methods: {
 
  }
};
</script>

<style>
</style>