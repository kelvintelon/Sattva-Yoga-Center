<template>
  <div>
    <h1>Class Registration</h1>
    <h2>New Client</h2>
    <h2>First class $10</h2>
    <v-btn elevation="5" v-on:click="buyFirstClass()">
      <v-icon> mdi-cart </v-icon> Buy Class
    </v-btn>
  </div>
</template>

<script>
export default {
  name: "class-registration-select",
  data() {
    return {};
  },
  methods: {
    buyFirstClass() {
      this.$router.push({ name: "checkout" });
    },
  },
  created() {},
};
</script>

<style>
</style>