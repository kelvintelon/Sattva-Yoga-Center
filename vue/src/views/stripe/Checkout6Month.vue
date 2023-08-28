<template>
  <div>
    <h1>Shopping Cart</h1>
    <h3>6-month Unlimited Package</h3>
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
    this.publishableKey = "pk_test_51N6PQhHIFPdFs4yBA8bxtYohRmL0sEaDlFLODDgcsMwbwWhUikMipVQACyesgumrDmCZ9a8vNadiYt62bZsgp4wE00TzGiZvSt";
    return {
      loading: false,
      lineItems: [
        {
          price: 'price_1N6eNWHIFPdFs4yBQwUDbWZw', // 6 month package
          quantity: 1,
        },
      ],
      successURL: 'http://localhost:8080/payment/subscriptionSuccess',
      cancelURL: 'http://localhost:8080/clientPackageManagement',
    };
  },
  methods: {
    submit () {
      localStorage.setItem('checkout6Month', true);
      // You will be redirected to Stripe's secure checkout page
      this.$refs.checkoutRef.redirectToCheckout();
    },
  },
};
</script>