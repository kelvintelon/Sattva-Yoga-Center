<template>
  <div class="client">
    <top-header></top-header>

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
import TopHeader from "../components/TopHeader.vue";
import ClientForm from "../components/ClientForm.vue";

export default {
  name: "client-registration",
  components: {
    TopHeader,
    ClientForm,
  },
};
</script>

<style>
</style>