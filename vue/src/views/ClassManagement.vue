<template>
  <div class="class">
    <top-header></top-header>

    <v-container
      :key="align"
      class="grey lighten-5 mb-6"
    >
      <v-row>
        <v-col cols="12"> <class-form></class-form></v-col>
      </v-row>
    </v-container>
  </div>
</template>


<script>
import TopHeader from "../components/TopHeader.vue";
import ClassForm from "../components/CreateClassForm.vue";

export default {
  name: "class-management",
  components: {
    TopHeader,
    ClassForm,
  },
};
</script>

<style>
</style>