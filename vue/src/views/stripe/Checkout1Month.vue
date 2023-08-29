<template>
  <div>
    <h1>Shopping Cart</h1>
    <h3>1-month Unlimited Package</h3>
    <stripe-checkout
      ref="checkoutRef"
      mode="subscription"
      :pk="publishableKey"
      :line-items="lineItems"
      :success-url="successURL"
      :cancel-url="cancelURL"
      @loading="v => loading = v"
    />
    <v-btn @click="submit">Subscribe!</v-btn>
  </div>
</template>

<script>
import { StripeCheckout } from '@vue-stripe/vue-stripe';
export default {
  components: {
    StripeCheckout,
  },
  data () {
    // this.publishableKey = process.env.STRIPE_PUBLISHABLE_KEY;
    this.publishableKey = "pk_test_51NEabUBV0tnIJdW6JIy49Ky1uilERTHoouGeS6ySxpMsLiSwuehx2qo04plqxcFuVk7M5DYIJXXZ532bONj0iXbI00qVJtVHbn";
    return {
      loading: false,
      lineItems: [
        {
          price: 'price_1NieCWBV0tnIJdW6WqIm2dti', // The id of the recurring price you created in your Stripe dashboard
          quantity: 1,
        },
      ],
      successURL: 'http://localhost:8080/payment/subscriptionSuccess',
      cancelURL: 'http://localhost:8080/clientPackageManagement',
    };
  },
  methods: {
    submit () {
      // so users don't abuse the success page
      localStorage.setItem('checkout1Month', true);
      // You will be redirected to Stripe's secure checkout page
      this.$refs.checkoutRef.redirectToCheckout();
    },
  },
  computed:{
    successful1(){
      return 'http://localhost:8080/payment/subscriptionSuccess';
    }
  }
};
</script>