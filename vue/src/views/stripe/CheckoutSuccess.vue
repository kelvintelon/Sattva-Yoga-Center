<template>
  <v-container>
    <h2>Payment Success!</h2>
    <v-row><br /></v-row>
    <v-row>
      <router-link v-bind:to="{ name: 'client-package-management' }">
        <v-btn color="orange">Buy Packages</v-btn>
      </router-link>
      <v-spacer></v-spacer>
      <router-link v-bind:to="{ name: 'class-registration' }">
        <v-btn color="green">Sign up for class</v-btn>
      </router-link>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
      <v-spacer></v-spacer>
    </v-row>
  </v-container>
</template>

<script>
export default {
}
</script>

<style></style>