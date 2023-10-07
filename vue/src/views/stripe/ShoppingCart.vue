<template>
  <v-container>
    <v-data-table
      :headers="headers"
      :items="$store.state.lineItems"
      class="elevation-5"
      dense
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-container><v-row>
          <router-link to="/clientPackageManagement">
            <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
              <v-icon> mdi-keyboard-return</v-icon> Return
            </v-btn>
          </router-link>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-toolbar-title v-if="$vuetify.breakpoint.mdAndUp" style="color: rgba(245, 104, 71, 0.95)"
            >Shopping Cart</v-toolbar-title
          >
          <v-toolbar-title v-if="$vuetify.breakpoint.mdAndDown" style="color: rgba(245, 104, 71, 0.95)"
            >Cart</v-toolbar-title
          >
          <v-spacer></v-spacer>
            <stripe-checkout
      ref="checkoutRef"
      :pk="publishableKey"
      :session-id="sessionId"
    />
    <v-btn color="orange" @click="submit">Checkout To See Total!</v-btn>
          </v-row></v-container>
        </v-toolbar>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <!-- <v-btn v-if="subscribeBtn(item)" color="green" small class="mr-2" @click="redirectSubscription(item)">
          Subscribe!
        </v-btn> -->
        <v-btn @click="remove(item)"> REMOVE </v-btn>
        <!-- <v-icon v-else  class="mr-2" @click="purchasePackage(item)">
          mdi-cart-plus
        </v-icon> -->
      </template>
    </v-data-table>
    <!-- <v-simple-table dense>
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
  </v-simple-table> -->
    
    
  </v-container>
</template>

<script>
import { StripeCheckout } from "@vue-stripe/vue-stripe";
import axios from "axios";
export default {
  components: {
    StripeCheckout,
  },
  data() {
    this.publishableKey =
      "pk_test_51NEabUBV0tnIJdW6JIy49Ky1uilERTHoouGeS6ySxpMsLiSwuehx2qo04plqxcFuVk7M5DYIJXXZ532bONj0iXbI00qVJtVHbn";
    return {
      headers: [
        {
          text: "Package Description",
          align: "start",
          value: "productName",
        },
        { text: "Cost $", value: "price", sortable: true },
        { text: "Remove", value: "actions", sortable: false },
      ],
      loading: false,
      sessionId: "", // session id from backend
      localStorageData: [],
      clone: [],
    };
  },
  created() {
    if (this.$store.state.lineItems.length > 0) {
      this.localStorageData = JSON.stringify(this.$store.state.lineItems);
      localStorage.setItem("lineItems", this.localStorageData);
    } else {
      const serializedData = localStorage.getItem('lineItems');
      if (serializedData) {
        this.localStorageData = JSON.parse(serializedData);
        this.$store.commit("SET_STRIPE_LINE_ITEMS",this.localStorageData);
      }
    }
    
  },
  methods: {
    submit() {
      // You will be redirected to Stripe's secure checkout page
      // console.log('lineItems: ', this.$store.state.lineItems);
      if (this.$store.state.lineItems.length > 0) {
        
        // check that only one subscription is allowed
       
        let continueWithPayment = true;
        for (let index = 0; index < this.$store.state.lineItems.length; index++) {
          let element = this.$store.state.lineItems[index];
          if (element.is_monthly_renew && this.$store.state.lineItems.length > 1) {
            continueWithPayment = false;
            alert("Please choose between one subscription or one-time purchase packages")
            break;
          }
        }

        if (continueWithPayment) {

        
        // check that if they have a subscription, then that it's standalone
        axios
          .post(`/stripe/create-checkout-session`, this.$store.state.lineItems)
          .then((response) => {
            this.sessionId = response.data.sessionId;
            //localStorage.setItem("sessionId", response.data.sessionId);
            localStorage.setItem("paymentId", response.data.paymentId);
            // console.log('session', response.data);
            this.$refs.checkoutRef.redirectToCheckout({
              sessionId: response.data.sessionId,
            });
            // this.stripe.redirectToCheckout({
            //   sessionId: response.data.sessionId,
            // });
          })
          .catch(alert("Attempting checkout"));

        }
      } else {
        alert("Cart is empty")
      }
    },

    remove(item) {
      let index = this.$store.state.lineItems.indexOf(item);
      this.$store.state.lineItems.splice(index, 1);
      this.$store.commit("SET_STRIPE_LINE_ITEMS", this.$store.state.lineItems);
      localStorage.setItem("lineItems", JSON.stringify(this.$store.state.lineItems));
    },
  },
};
</script>