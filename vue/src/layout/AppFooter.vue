<template>
  <v-footer
    
    padless
    light
    min-height="250px"
    color="rgba(252, 142, 1, 0.22)"
  >
 
  <v-row justify="center" align="center">
  
   <v-col justify="center" align="center">
    <h5>Find Us</h5>
     <div id="map"></div>
   </v-col>
  <v-col justify="center" align="center">
    <h5> Contact Us</h5>
    <div class="contactUs" style="font-size: 18px ">
     <p>835 Mason St, Suite B120 Dearborn, MI 48124, USA (Parking lot entrance on Garrison Street between Mason and Monroe Streets)</p>



<p class="emailLink" @click="openEmailAddress">info@sattva-yoga-center.com</p>


(313) 274-3995 (touch number to dial)

    </div>
  </v-col>
   </v-row>
   <v-row justify="center" align="center">
    
    <v-col justify="center" align="center">
      <div style="font-size: 16px" >©2023 by Sattva Yoga Center</div>
      
    </v-col>
   
   </v-row>
  </v-footer>
</template>

<script>
export default {
  name: "AppFooter",
  data() {
    return {
      map: null,
      mapCenter: { lat: 42.307550, lng: -83.245910 },

    }
  },
  mounted() {
    this.initMap();
  },
  methods: {
    initMap() {
     
     this.map = new window.google.maps.Map(document.getElementById("map"), {
      center: this.mapCenter,
        zoom: 14,
        maxZoom: 20,
        minZoom: 3,
        mapTypeControl:true,
        zoomControl: true,
      });
      // let noPOIStyle = [
      //   {
      //     featureType: "poi",
      //     elementType: "labels",
      //     stylers: [{ visibility: "off" }],
      //   },
      // ];

      // this.map.setOptions({ styles: noPOIStyle });

      const markerObj = { coord: { lat: 42.307550, lng: -83.245910  }, name: "Sattva Yoga Center" };
      
      new window.google.maps.Marker({
        position: markerObj.coord,
        map: this.map,
        label: {
          text: markerObj.name,
          color: "blue"
        },
      });
    },
    openEmailAddress() {

      window.open('https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=info@sattva-yoga-center.com', '_blank').focus();
    },
  }
};
</script>

<style scoped>
.contactUs {
  display:block;
}
#map {
  height: 20vh;
  width: 40vh;

}

.emailLink:hover {
  text-decoration: underline;
  color: blue;
}

</style>
