<template>
  <div class="home">
    <top-header></top-header>
    <header>Sattva</header>
    <p>You must be authenticated to see this</p>
    <client-form></client-form>

    
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ClientForm from "../components/ClientForm.vue";

export default {
  name: "home",
  components: {
    TopHeader,
    ClientForm
  },
};
</script>

<style>

</style>