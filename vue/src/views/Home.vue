<template>
  <v-container>
    <v-card class="mx-auto my-2" max-width="700px" min-width="700px">
      <v-img height="350px" src="@/assets/IMG_5742.jpeg"></v-img>
      <v-card-title><strong>namaste.</strong></v-card-title>
      <v-card-text>
        <v-row align="center" class="mx-0">
          Namas ipsum te dolor sit amet, consectetur adipisicing elit. Accusantium cupiditate debitis delectus dolorum
          eaque harum ipsa, molestiae molestias mollitia, nesciunt officia perferendis quaerat quam reiciendis, sequi
          totam vel voluptas voluptates!
        </v-row>
      </v-card-text>
      <v-divider class="mx-4"></v-divider>
      <v-card-subtitle style="font-size: medium">upcoming classes</v-card-subtitle>

      <v-card-text>
        <v-chip-group
            v-model="selection"
            active-class="deep-purple accent-4 white--text"
            column
        >
          <v-chip>5:30PM</v-chip>

          <v-chip>7:30PM</v-chip>

          <v-chip>8:00PM</v-chip>

          <v-chip>9:00PM</v-chip>
        </v-chip-group>
      </v-card-text>

      <v-card-actions>
        <v-btn
            color="deep-purple lighten-2"
            text
            @click="reserve"
        >
          Reserve
        </v-btn>
      </v-card-actions>

    </v-card>


  </v-container>
</template>

<script>


export default {
  name: "home",
  components: {},
  data: () => ({
    loading: false,
    selection: 1,
  }),

  methods: {
    reserve() {
      this.loading = true

      setTimeout(() => (this.loading = false), 2000)
    },
  },
};
</script>

<style></style>
