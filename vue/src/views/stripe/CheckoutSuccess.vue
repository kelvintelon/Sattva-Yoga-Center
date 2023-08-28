<template>
  <v-container>
    <h2>Payment Success! Do not refresh this page! Redirecting . . .</h2>
    <!-- <v-row><br /></v-row>
    <v-row>
      <router-link v-bind:to="{ name: 'client-package-management' }">
        <v-btn color="orange">Buy Packages</v-btn>
      </router-link>
      <v-spacer></v-spacer>
      <router-link v-bind:to="{ name: 'class-registration' }">
        <v-btn color="green">Sign up for class</v-btn>
      </router-link>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
    </v-row> -->
  </v-container>
</template>

<script>
// import axios from 'axios';
import StripeService from '../../services/StripeService'
export default {
  data() {
    return {
      sessionId: '',
      listOfPurchased: []
    }
  },
  created() {
    // this.updateDb();
    this.getFromLocalStorage();
  },
  methods: {
    // updateDb() {
    //   this.sessionId = localStorage.getItem('sessionId');
    //   axios
    //     .get(`/stripe/updateDbAfterPurchase/${this.sessionId}`)
    //     .then((response)=>{
    //       if(response.status== 201){
    //         alert("Payment Success")
    //       }
    //     })
    // },
    getFromLocalStorage() {
      const serializedData = localStorage.getItem('lineItems');
      if (serializedData) {
        this.listOfPurchased = JSON.parse(serializedData);
      }
      this.purchaseLocalStorage(this.listOfPurchased);
    },
    purchaseLocalStorage(listOfPurchased){
      StripeService.purchaseLocalStorageItems(listOfPurchased).then((response)=>{
        if(response.status == 201){
          setTimeout(() => {
            localStorage.removeItem('lineItems');
            this.$router.push({ name: 'client-package-management' })
            }, 3000);
        }
      })
    }
  }
}
</script>

<style></style>