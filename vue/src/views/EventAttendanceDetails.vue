<template>
  <div>
    <top-header></top-header>
    <event-details></event-details>
  </div>
</template>

<script>
import EventDetails from "../components/EventDetails.vue";
import TopHeader from "../components/TopHeader.vue";

export default {
  name: "event-attendance-details",
  components: {
    TopHeader,
    EventDetails,
    
  },
};
</script>

<style>
</style>