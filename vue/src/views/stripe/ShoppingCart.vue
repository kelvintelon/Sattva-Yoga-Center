<template>
  <div>
    <v-simple-table dense>
    <template v-slot:default>
      <thead>
        <tr>
          <th class="text-left">
            Description
          </th>
          <th class="text-left">
            Price
          </th>
          <th class="text-left">
            Action
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in $store.state.lineItems"
          :key="item.productName"
        >
          <td>{{ item.productName }}</td>
          <td>${{ item.price }}</td>
          <td> 
            <v-btn v-on:click="remove(item)">remove</v-btn> 
          </td>
        </tr>
      </tbody>
    </template>
  </v-simple-table>
    <stripe-checkout
      ref="checkoutRef"
      :pk="publishableKey"
      :session-id="sessionId"
    />
    <v-btn color="orange" @click="submit">Checkout To See Total!</v-btn>
  </div>
</template>

<script>
import { StripeCheckout } from '@vue-stripe/vue-stripe';
import axios from 'axios';
export default {
  components: {
    StripeCheckout,
  },
  data () {
    this.publishableKey = "pk_test_51NEabUBV0tnIJdW6JIy49Ky1uilERTHoouGeS6ySxpMsLiSwuehx2qo04plqxcFuVk7M5DYIJXXZ532bONj0iXbI00qVJtVHbn";
    return {
      loading: false,
      sessionId: '', // session id from backend
      localStorageData: [],
      clone: []
    };
  },
  methods: {
    submit () {
      // You will be redirected to Stripe's secure checkout page
      // console.log('lineItems: ', this.$store.state.lineItems);
      this.localStorageData = JSON.stringify(this.$store.state.lineItems)
      localStorage.setItem('lineItems',this.localStorageData);
      axios
        .post(
          `/stripe/create-checkout-session`,
          this.$store.state.lineItems
        )
        .then((response) => {
          this.sessionId= response.data.sessionId;
          localStorage.setItem('sessionId', response.data.sessionId);
          localStorage.setItem('paymentId', response.data.paymentId);
          // console.log('session', response.data);
          this.$refs.checkoutRef.redirectToCheckout({
            sessionId: response.data.sessionId,
          });
          // this.stripe.redirectToCheckout({
          //   sessionId: response.data.sessionId,
          // });
        })
        .catch((err) => console.log(err));
    },

    remove(item){
      let index = this.$store.state.lineItems.indexOf(item);
      this.$store.state.lineItems.splice(index,1);
      this.$store.commit("SET_STRIPE_LINE_ITEMS",this.$store.state.lineItems);
    },
  },
};
</script>