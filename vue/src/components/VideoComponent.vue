<template>
  <!-- https://videojs.com/guides/vue/ -->
  <v-container>
    <div>
         <video-player id="vid1" :options="videoOptions" />
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
      
      localUrl: 'http://localhost:9000/get-file?fileName=',
      exactFileLocation: 'http://localhost:9000/get-file?fileName=09.18.20 - Friday_',
      fileName: '09.18.20 - Friday_',

    };
  },
  computed: {
    videoOptions() {
      return {
        autoplay: true,
        controls: true,
        sources: [
          {
            src:
            this.localUrl+this.fileName,
              type: 'video/mp4'
              // type: 'bytes'
          }
        ]
      }
    }
  },
  created() {
    // this.videoOptions.src='http://localhost:9000/get-file?fileName='+ this.file;
    // var video = this.videojs("vid1");
    // video.src(this.fileName);
  },
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