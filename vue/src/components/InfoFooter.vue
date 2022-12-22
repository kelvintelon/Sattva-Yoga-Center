<template>
  <v-card>
    <v-footer
        color="red accent-2 mt-5"


    >
      <v-row
          justify="center"

      >
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
        <v-col
            class=" py-4 text-center white--text"
            cols="12"
        >
          {{ new Date().getFullYear() }} - <strong>Sattva Yoga Center</strong>
        </v-col>
      </v-row>
    </v-footer>
  </v-card>
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