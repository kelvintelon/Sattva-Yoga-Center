<template>
  <v-footer
    app
    padless
    class="d-none d-sm-flex"
    min-height="200px"
    color="#EFEBE9"
  >
    <v-col class="">
      {{ new Date().getFullYear() }} <strong>Sattva Yoga Center</strong>
    </v-col>
  </v-footer>
</template>

<script>
export default {
  name: "AppFooter",
};
</script>

<style scoped></style>
