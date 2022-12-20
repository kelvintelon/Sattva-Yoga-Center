<template>
  <div>
    <div class="inner_header">
      <router-link
        style="text-decoration: none; color: inherit"
        v-bind:to="{ name: 'home' }"
      ><img class="logo" src="@/assets/sattva-logo.jpeg" />
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

.inner_header {
  padding: 10px;
}

.title {
  font-size: 50px;
}

.logo {
  height: 130px;
}
</style>