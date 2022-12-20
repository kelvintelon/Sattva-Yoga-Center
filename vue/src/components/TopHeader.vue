<template>
  <div class="first-container">
    <div class="second-container">
      <header-logo></header-logo>
      <h1 class="header-title">Sattva Yoga Center</h1>
    </div>
    
      <login-button></login-button>
    
  </div>
</template>

<script>
import HeaderLogo from "./HeaderLogo.vue";
import LoginButton from "./LoginButton.vue";

export default {
  name: "top-header",
  components: {
    HeaderLogo,
    LoginButton,
  },
  data() {
    return {};
  },
  created() {},
};
</script>

<style>
.first-container {
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.second-container {
  display: flex;
  align-items: center;
  flex-basis: 70%;
  padding: 20px;
}

.header-title {
  padding: 20px;
}
</style>