<template>

  <v-footer
      color="red accent-2"

      min-height="100vh"

  >
    <v-row>
      <v-col>

        <v-btn
            v-for="link in links"
            :key="link"
            color="amber lighten-5"
            text
            rounded
            class="ma-2"
        >
          {{ link }}
        </v-btn>
        <v-col class=" py-4 text-center white--text"
        >
          {{ new Date().getFullYear() }} <strong>Sattva Yoga Center</strong>
        </v-col>

      </v-col>
    </v-row>
  </v-footer>


</template>

<script>
export default {
  name: "info-footer",
  data() {
    return {
      links: [
        'home',
        'contact',
        'follow us'
      ]
    }
  }
}
</script>

<style scoped>

</style>