<template>

    <v-footer class="pa-0"
    >
      <v-card
          flat
          tile
          width="100%"
          class="orange lighten-1git s text-center"
      >
        <v-card-text>
          <v-btn
              v-for="icon in icons"
              :key="icon"
              class="mx-4"
              icon
          >
            <v-icon size="24px">
              {{ icon }}
            </v-icon>
          </v-btn>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-text class="white--text">
          {{ new Date().getFullYear() }} — <strong>Sattva Yoga Center</strong>
        </v-card-text>
      </v-card>
    </v-footer>

</template>
<script>
export default {
  data: () => ({
    icons: [
      'mdi-home',
      'mdi-email',
      'mdi-calendar',
    ],

  }),

}
</script>

<style scoped>

</style>