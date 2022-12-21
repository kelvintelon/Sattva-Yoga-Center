<template>
  <!-- m is margin; x is left & right spacing -->
  <v-card class="mx-auto my-12">
    <v-card-title>upcoming events</v-card-title>

    

    <v-card-text>
      <v-row align="center" class="mx-auto"> </v-row>
      <div class="grey--text ms-4">
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Natus harum
        error expedita cumque architecto! Dolore, ducimus pariatur.
        Reprehenderit adipisci quas dolores, deleniti veniam incidunt unde
        magnam, facere fuga, itaque similique!
      </div>
    </v-card-text>

    <v-divider class="mx-4"></v-divider>

    <v-card-title>Class times</v-card-title>

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
``

<style></style>
