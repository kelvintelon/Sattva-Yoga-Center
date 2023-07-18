<template>
    <v-container>
    <h2>Shopping Cart</h2>
    <br />
    <v-spacer></v-spacer>
    <v-simple-table dense>
    <template v-slot:default>
      <thead>
        <tr>
          <th class="text-left">
            Description
          </th>
          <th class="text-left">
            Quantity
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in clone"
          :key="item.description"
        >
          <td>{{ item.description }}</td>
          <td> 
            <button v-on:click="decrease(item)">-</button> 
            {{ item.quantity }} 
            <button v-on:click="increase(item)">+</button> 
          </td>
        </tr>
      </tbody>
    </template>
  </v-simple-table>
    <!-- <p>10-class Package</p> -->
    <stripe-checkout
      ref="checkoutRef"
      mode="payment"
      :pk="publishableKey"
      :line-items="lineItems"
      :success-url="successURL"
      :cancel-url="cancelURL"
      @loading="v => loading = v"
    />
    <v-btn elevation="5" v-on:click="submit">
      <v-icon> mdi-cart </v-icon> Pay Now!
    </v-btn>
    <v-spacer></v-spacer>

  </v-container>
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
      lineItems: [],
      successURL: 'http://localhost:8080/success',
      cancelURL: 'http://localhost:8080/clientPackageManagement',
      addedPackages:[],
      clone: [],
      descriptionPackages:[
        "New Client First Class",
        "New Client First Class",
        "10 Class Package",
        "20 Class Package",
        "One Month Package",
        "Six Month Package",
        "Gift"
      ],
      priceIdPackages:[
        "asdf",
        "sadg",
        "price_1N6c2YHIFPdFs4yB3HUeqY5I",
        "price_1N6cHnHIFPdFs4yBxuAIkPU7",
        "price_1N6c0gHIFPdFs4yBwtPzfq9K",
        "price_1N6eNWHIFPdFs4yBQwUDbWZw",
        "sadfasd"
      ],
      quantityTracker:[
        0,
        0,
        0,
        0,
        0,
        0,
        0
      ]
    };
  },
  created(){
    this.setup();
  },
  methods: {
    submit () {
      // You will be redirected to Stripe's secure checkout page
      this.$refs.checkoutRef.redirectToCheckout();
    },
    setup(){
      this.addedPackages = this.$store.state.lineItems;
    for(let i= 0 ; i< this.addedPackages.length; i++){
      let index = this.descriptionPackages.indexOf(this.addedPackages[i].description);
      this.quantityTracker[index]++;
    }
    for(let i= 0 ; i< this.quantityTracker.length; i++){
      if(this.quantityTracker[i]>0){
        let obj = new Object();
        obj.price = this.priceIdPackages[i];
        obj.quantity = this.quantityTracker[i];
        this.lineItems.push(obj);
        let obj1 = new Object();
        obj1.price = this.priceIdPackages[i];
        obj1.quantity = this.quantityTracker[i];
        obj1.description = this.descriptionPackages[i];
        this.clone.push(obj1);
      }
    }
    this.$store.commit("SET_STRIPE_LINE_ITEMS",this.lineItems);
    },
    decrease(item){
      if(item.quantity>0){
        item.quantity = item.quantity -1;
      }
      let index = this.clone.indexOf(item);
      if(item.quantity <= 0){
        this.clone.splice(index,1);
      }
      this.lineItems = this.clone.map(({price,quantity})=>{return {price,quantity};});
      this.$store.commit("SET_STRIPE_LINE_ITEMS",this.lineItems);
    },
    increase(item){
      if(item.quantity>0){
        item.quantity = item.quantity +1;
      }
      this.lineItems = this.clone.map(({price,quantity})=>{return {price,quantity};});
      this.$store.commit("SET_STRIPE_LINE_ITEMS",this.lineItems);
    },
  },
};
</script>