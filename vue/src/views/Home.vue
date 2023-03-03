<template>
  <v-container>
    <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="700px">
      <v-img height="350px" src="@/assets/app-bar-photo.png"></v-img>
      <v-card-title><strong>Welcome</strong></v-card-title>
      <v-card-text>
        <v-row align="center" class="mx-0">
          Sattva has been serving our Dearborn, Michigan community since January 2007. The central focus of our classes is the breath. Our aim is to facilitate a healthy body and a supple mind.
        </v-row>
      </v-card-text>
      <v-divider class="mx-4"></v-divider>
      <v-card-subtitle style="font-size: medium">upcoming classes</v-card-subtitle>

      <v-card-text>
        <v-chip-group
            v-model="selection"
            active-class="deep-orange accent-4 white--text"
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
            color="deep-orange lighten-2"
            text
            @click="reserve"
        >
          Reserve
        </v-btn>
      </v-card-actions>
      <v-card-text>Lorem ipsum dolor sit amet consectetur adipisicing elit. Quis nemo repellat facilis adipisci quod praesentium saepe illo ipsam earum veritatis natus non blanditiis debitis, quos assumenda iure vel tempora id.</v-card-text>

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
