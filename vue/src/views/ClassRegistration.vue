<template>
  <div class="home">
    <top-header></top-header>
    <!-- <class-registration-select></class-registration-select> -->
    <class-registration-client></class-registration-client>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
// import ClassRegistrationSelect from "../components/ClassRegistrationSelect.vue";
import ClassRegistrationClient from "../components/ClassRegistrationClient.vue"

export default {
  name: "class-registration",
  components: {
    TopHeader,
    // ClassRegistrationSelect,
    ClassRegistrationClient
  },
};
</script>

<style>
</style>