<template>
  <div>
    <img class="logo" src="@/assets/sattva-logo.jpeg" />
    <div class="inner_header">
      <router-link
        style="text-decoration: none; color: inherit"
        v-bind:to="{ name: 'home' }"
      >
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "header-logo",
  data() {
    return {};
  },
  created() {},
};
</script>

<style>
/* header {
  display: flex;
  justify-content: left;
} */

.inner_header {
  margin-left: 40px;
}

.title {
  font-size: 50px;
}

.logo {
  height: 140px;
}
</style>