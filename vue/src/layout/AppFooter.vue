<template>
  <v-footer app
            padless
            class="d-none d-sm-flex"
            min-height="100px"
            color="amber"
  >
    <v-col cols="12" class="">
      {{ new Date().getFullYear() }} <strong>Sattva Yoga Center</strong>
    </v-col>

  </v-footer>
</template>

<script>
export default {
  name: "AppFooter"
}
</script>

<style scoped>

</style>
