<template>
  <v-container>
    <h2 style="color: rgba(245, 104, 71, 0.95)">Payment Success! Do not refresh this page or switch tabs! Redirecting . . .</h2>
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
    
    this.getFromLocalStorage();
  },
  methods: {
    getFromLocalStorage() {
      const serializedData = localStorage.getItem('lineItems');
      if (serializedData) {
        this.listOfPurchased = JSON.parse(serializedData);
      }
      for (let index = 0; index < this.listOfPurchased.length; index++) {
        this.listOfPurchased[index].paymentId = localStorage.getItem('paymentId');
      }
     
      this.purchaseLocalStorage(this.listOfPurchased);
    },
    purchaseLocalStorage(listOfPurchased){
      StripeService.purchaseLocalStorageItems(listOfPurchased).then((response)=>{
        if(response.status == 201){
          setTimeout(() => {
            localStorage.removeItem('lineItems');
            localStorage.removeItem('paymentId');
            this.$router.push({ name: 'client-package-management' })
            }, 3000);
        }
      })
    }
  }
}
</script>

<style></style>