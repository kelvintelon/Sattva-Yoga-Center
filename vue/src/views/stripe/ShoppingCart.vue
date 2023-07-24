<template>
  <div>
    <stripe-checkout
      ref="checkoutRef"
      :pk="publishableKey"
      :session-id="sessionId"
    />
    <button @click="submit">Checkout!</button>
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
    this.publishableKey = "pk_test_51N6PQhHIFPdFs4yBA8bxtYohRmL0sEaDlFLODDgcsMwbwWhUikMipVQACyesgumrDmCZ9a8vNadiYt62bZsgp4wE00TzGiZvSt";
    return {
      loading: false,
      sessionId: '', // session id from backend
    };
  },
  methods: {
    submit () {
      // You will be redirected to Stripe's secure checkout page
      // console.log('lineItems: ', this.$store.state.lineItems);
      axios
        .post(
          `/stripe/create-checkout-session`,
          this.$store.state.lineItems
        )
        .then((response) => {
          this.sessionId= response.data.sessionId;
          localStorage.setItem('sessionId', response.data.sessionId);
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
  },
};
</script>