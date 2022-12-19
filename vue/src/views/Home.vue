<template>
  <div class="home">
    <header-logo></header-logo>
    <h1>Home</h1>
    <p>You must be authenticated to see this</p>
  </div>
</template>


<script>
import HeaderLogo from "../components/HeaderLogo.vue";

export default {
  name: "home",
  components: {
    HeaderLogo,
  },
};
</script>
