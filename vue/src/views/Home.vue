<template>
  <div class="home">
    <top-header></top-header>
    <header>Sattva</header>
    <p>You must be authenticated to see this</p>
    

    
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue"

export default {
  name: "home",
  components: {
    TopHeader
  },
};
</script>

<style>

</style>