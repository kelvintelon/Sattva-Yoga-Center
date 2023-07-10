<template>
  <v-container>
        <v-card
        >
          <edit-profile-form></edit-profile-form>
        </v-card>
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