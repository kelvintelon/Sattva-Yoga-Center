<template>
  <div class="home">
    <top-header></top-header>
    <class-registration-client></class-registration-client>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ClassRegistrationClient from "../components/ClassRegistrationClient.vue"

export default {
  name: "class-registration",
  components: {
    TopHeader,
    ClassRegistrationClient
  },
};
</script>

<style>
</style>