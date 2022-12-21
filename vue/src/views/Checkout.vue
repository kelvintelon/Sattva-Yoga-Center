<template>
  <div class="home">
    <top-header></top-header>
    <shopping-cart></shopping-cart>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ShoppingCart from "../components/ShoppingCart.vue";

export default {
  name: "home",
  components: {
    TopHeader,
    ShoppingCart,
  },
};
</script>

<style>
</style>