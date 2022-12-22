<template>
  <!-- m is margin; x is left & right spacing -->
  <v-card class="mx-auto my-12"
    color="deep-orange lighten-5" 
  >

    <v-img src="https://images.pexels.com/photos/507935/pexels-photo-507935.jpeg"></v-img>

    <v-card-title class="font-weight-bold">upcoming events</v-card-title>

    

    <v-card-text>
      <v-row align="center" class="mx-0"> </v-row>

      <div class="black--text mx-auto mb-0 mt-5">
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Natus harum
        error expedita cumque architecto! Dolore, ducimus pariatur.
        Reprehenderit adipisci quas dolores, deleniti veniam incidunt unde
        magnam, facere fuga, itaque similique!
      </div>

    </v-card-text>

    <v-divider ></v-divider>

    <v-card-title class="font-weight-bold">Class times</v-card-title>

    <v-card-text>
      <v-chip-group
        v-model="selection"
        active-class="amber accent-4 white--text"
        column
      >
        <v-chip>4:00 pm</v-chip>
        <v-chip>5:00 pm</v-chip>
        <v-chip>8:00 pm</v-chip>
      </v-chip-group>
    </v-card-text>

    <v-card-actions>
      <v-btn color="black" text @click="signup">sign up̵</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
export default {
  name: "home-card",
  components: {},
  data() {
    return {};
  },
  created() {},
};
</script>

<style></style>
