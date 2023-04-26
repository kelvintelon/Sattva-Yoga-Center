<template>
  <div class="client">

    <v-container
      :key="align"
      class="grey lighten-5 mb-6"
    >
      <v-row>
        <v-col cols="12"> <client-form></client-form></v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import ClientForm from "../components/ClientForm.vue";

export default {
  name: "client-registration",
  components: {
    ClientForm,
  },
};
</script>

<style>
</style>