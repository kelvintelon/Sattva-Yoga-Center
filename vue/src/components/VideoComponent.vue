<template>
  <!-- https://videojs.com/guides/vue/ -->
  <v-container>
    <div>
         <video-player :options="videoOptions" />
      <!-- <video-player  >
        <source src=earthLocation type="video/mp4"/>
      </video-player> -->
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
            // '/get-file', 
            // VideoService.getVideoFile().then(),
            'http://localhost:9000/get-file',
            // this.getVideo(),
            // require("@/assets/videos/earth.mp4"), 
            // require(this.getVideo()),
            // require(this.videoObject),
              type: 'video/mp4'
              // type: 'bytes'
          }
        ]
      },
      videoObject: {},
      earthLocation: require("@/assets/videos/earth.mp4")
    };
  },
  // created() {
  //   VideoService.getVideoFile().then((response) => {

     
    
  //   this.videoObject = response.data;

  //   })
  // },
  methods: {
    getVideo() {
    
     VideoService.getVideoFile().then((response) => {
  
     return   response.data

    })
    
  },
  }
};
</script>

<style>
</style>