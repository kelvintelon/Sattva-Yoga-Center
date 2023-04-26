<template>
  <div class="home">
    <shopping-cart></shopping-cart>
  </div>
</template>


<script>
import ShoppingCart from "../components/ShoppingCart.vue";

export default {
  name: "checkout",
  components: {
    ShoppingCart,
  },
};
</script>

<style>
</style>