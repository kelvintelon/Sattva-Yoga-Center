<template>
  <!-- m is margin; x is left & right spacing -->
  <v-card class="mx-auto my-12"
          color="amber lighten-5"
          min-height="100%"
  >

    <v-img src="https://static.wixstatic.com/media/78a355_0dceef78b31e4be8a9368faf0905cac6~mv2.jpg/v1/fit/w_2074,h_1046,q_90/78a355_0dceef78b31e4be8a9368faf0905cac6~mv2.webp"></v-img>

    <v-card-title class="font-weight-bold">upcoming events</v-card-title>


    <v-card-text>
      <v-row align="center" class="mx-0"></v-row>

      <div class="black--text mx-auto mb-0 mt-5">
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Natus harum
        error expedita cumque architecto! Dolore, ducimus pariatur.
        Reprehenderit adipisci quas dolores, deleniti veniam incidunt unde
        magnam, facere fuga, itaque similique!
      </div>

    </v-card-text>

    <v-divider></v-divider>

    <v-card-title class="font-weight-bold">Class times</v-card-title>

    <v-card-text>
      <v-chip-group
          v-model="selection"
          class="mx-4"
          active-class="amber accent-4 white--text"
          column
      >
        <v-chip>4:00 pm</v-chip>
        <v-chip>5:00 pm</v-chip>
        <v-chip>8:00 pm</v-chip>
      </v-chip-group>
    </v-card-text>

    <v-card-actions>
      <v-btn class="mx-6">sign up</v-btn>
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
  created() {
  },
  method: {
    signup() {
       this.$router.push("/registerForClass");
    }
  }
};
</script>

<style></style>
