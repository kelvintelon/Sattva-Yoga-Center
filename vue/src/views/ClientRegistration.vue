<template>
  <div class="client">
    <top-header></top-header>

    <v-container v-for="align in alignments" :key="align" class="grey lighten-5 mb-6">
      <v-row>
       <client-form></client-form>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ClientForm from "../components/ClientForm.vue";

export default {
  name: "client",
  components: {
    TopHeader,
    ClientForm,
  },
};
</script>

<style>
</style>