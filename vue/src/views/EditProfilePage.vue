<template>
  <v-container class="edit-profile" fill-height fluid>
    <v-spacer></v-spacer>
    <top-header></top-header>
    <v-spacer></v-spacer>
    <v-row justify="center" align="center">
      <v-spacer></v-spacer>
      <v-col cols="4" justify="center" align="center">
        <v-card
          class="mx-auto my-12 rounded-xl pb-12 pt-12"
          color="gray lighten-4"
          min-height="100%"
        >
          <header-logo></header-logo>
          <edit-profile-form></edit-profile-form>
        </v-card>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
  </v-container>
</template>

<script>
import EditProfileForm from "../components/EditProfileForm.vue";
import TopHeader from "../components/TopHeader.vue";

export default {
  name: "edit-profile-page",
  components: { EditProfileForm, TopHeader },
};
</script>

<style>
</style>