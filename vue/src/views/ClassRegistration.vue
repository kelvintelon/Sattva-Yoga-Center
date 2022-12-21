<template>
  <div class="home">
    <top-header></top-header>
    <class-registration-select></class-registration-select>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ClassRegistrationSelect from "../components/ClassRegistrationSelect.vue";

export default {
  name: "home",
  components: {
    TopHeader,
    ClassRegistrationSelect,
  },
};
</script>

<style>
</style>