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
      oneMonthPackage: {
        "price": 95,
        "package_id": 6,
        "productName": "One Month Package",
        "quantity": 1,
        "client_id" : this.$store.state.clientDetails.client_id,
        "total_amount_paid": 95,
      },
      sixMonthPackage: {
        "price": 540,
        "package_id": 7,
        "productName": "Six Month Package",
        "quantity": 1,
        "client_id" : this.$store.state.clientDetails.client_id,
        "total_amount_paid": 540,
      }
    }
  },
  created() {
    this.getFromLocalStorage();
  },
  methods: {
    getFromLocalStorage() {
      let checkout1Month = localStorage.getItem('checkout1Month');
      let checkout6Month = localStorage.getItem('checkout6Month');
      if (checkout1Month) {
        this.oneMonthPackage.paymentId = localStorage.getItem('paymentId');
        this.updateOneMonthDb(this.oneMonthPackage);
      }
      if (checkout6Month) {
        this.sixMonthPackage.paymentId = localStorage.getItem('paymentId');
        this.updateSixMonthDb(this.sixMonthPackage);
      }
    },
    updateOneMonthDb(oneMonthPackage){
      StripeService.updateOneMonthDb(oneMonthPackage).then((response)=>{
        if(response.status == 201){
          setTimeout(() => {
            localStorage.removeItem('checkout1Month');
            localStorage.removeItem('paymentId');
            this.$router.push({ name: 'client-package-management' })
            }, 3000);
        }
      })
    },
    updateSixMonthDb(sixMonthPackage){
      StripeService.updateSixMonthDb(sixMonthPackage).then((response)=>{
        if(response.status == 201){
          setTimeout(() => {
            localStorage.removeItem('checkout6Month');
            localStorage.removeItem('paymentId');
            this.$router.push({ name: 'client-package-management' })
            }, 3000);
        }
      })
    },
  }
}
</script>

<style></style>