<template>
  <div class="home">
    <header-logo></header-logo>
    <h1>Test</h1>
    <!-- <img alt="Vue logo" src="../assets/logo.png"> -->
    <!-- <HelloWorld msg="Welcome to Your Vue.js App"/> -->
  </div>
</template>

<script>
// @ is an alias to /src
import HeaderLogo from "../components/HeaderLogo.vue";

export default {
  name: 'Home',
  components: {
    HeaderLogo,
  }
}
</script>
