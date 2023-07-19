<template>
  <v-footer padless light min-height="220px" color="rgba(252, 142, 1, 0.22)">
    <v-row justify="center" align="center">

      <v-col justify="center" align="center">
        <h5>Find Us</h5>
        <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2950.62814395723!2d-83.24889262415292!3d42.30779937119786!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x883b3587b1d77a51%3A0x6df4f82a074dd6ae!2sSattva%20Yoga%20Center!5e0!3m2!1sen!2sus!4v1689775920839!5m2!1sen!2sus" width="400" height="200" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
      </v-col>
    </v-row>
    <v-row justify="center" align="center">
      <v-col justify="center" align="center">
        <h5> Contact Us</h5>
        <div class="contactUs" style="font-size: 18px ">
          <p>835 Mason St, Suite B120 Dearborn, MI 48124, USA (Parking lot entrance on Garrison Street between Mason and
            Monroe Streets)</p>



          <p class="emailLink" @click="openEmailAddress">info@sattva-yoga-center.com</p>


          (313) 274-3995 (touch number to dial)
          <v-col justify="center" align="center">
            <div style="font-size: 16px">©2023 by Sattva Yoga Center</div>

          </v-col>
        </div>
      </v-col>
    </v-row>
  </v-footer>
</template>

<script>
// import awsService from "../services/AWSService"

export default {
  name: "AppFooter",
  data() {
    return {
      map: null,
      mapCenter: { lat: 42.307550, lng: -83.245910 },

    }
  },
  mounted() {
  
    // API key does not exist in local storage, make API call to retrieve it
    // awsService.getMap().then((response) => {
    //   const retrievedApiKey = response.data.VUE_APP_MAPS_KEY;

    //   const script = document.createElement('script');
    //   script.src = `https://maps.googleapis.com/maps/api/js?key=${retrievedApiKey}`;
    //   script.onload = () => {
    //     this.initMap();
    //   };
      
    //   document.body.appendChild(script);
    // });
  
},
  methods: {
    initMap() {

      this.map = new window.google.maps.Map(document.getElementById("map"), {
        center: this.mapCenter,
        zoom: 14,
        maxZoom: 20,
        minZoom: 3,
        mapTypeControl: true,
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

      const markerObj = { coord: { lat: 42.307550, lng: -83.245910 }, name: "Sattva Yoga Center" };

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
  display: block;
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
