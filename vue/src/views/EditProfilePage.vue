<template>
  <v-container fill-height fluid>

    <v-row justify="center" align="center">
      <v-spacer></v-spacer>
      <v-col >
        <v-card
          class="mx-auto my-12 rounded-xl pb-12 pt-12"
          color="gray lighten-4"
          min-height="100%"
        >
        <v-spacer></v-spacer>
          <edit-profile-form></edit-profile-form>
        </v-card>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
  </v-container>
</template>

<script>
import EditProfileForm from "../components/EditProfileForm.vue";


export default {
  name: "edit-profile-page",
  components: { EditProfileForm },
};
</script>

<style>
</style>