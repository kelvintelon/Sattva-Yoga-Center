<template>
  <div class="first-container">
    <div class="second-container">
      <header-logo></header-logo>
      <h1 class="header-title">Sattva Yoga Center</h1>
    </div>

    <login-button v-if="$store.state.token == ''"></login-button>
    <div v-else>
      <logout-button></logout-button>

      <my-account-button></my-account-button>
    </div>
  </div>
</template>

<script>
import HeaderLogo from "./HeaderLogo.vue";
import LoginButton from "./LoginButton.vue";
import MyAccountButton from "./MyAccountButton.vue";
import LogoutButton from "./LogoutButton.vue";

export default {
  name: "top-header",
  components: {
    HeaderLogo,
    LoginButton,
    MyAccountButton,
    LogoutButton,
  },
  data() {
    return {
      expand: false,
    };
  },
  created() {},
};
</script>

<style scoped>
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