<template>
  <v-navigation-drawer
    v-model="drawer"
    :rail="rail"
    permanent
    @click="rail = false"
  >
    <v-list>
      <v-list-item
        prepend-icon="mid-home"
        title="Home"
        value="home"
      ></v-list-item>
      <v-list-item
        prepend-icon="mdi-apps"
      ></v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
export default {
  name: "NavDrawer",
  data() {
    return {
      drawer: true,
      items: [{ title: "Home", icon: "mid-home" }],
      rail: true,
    };
  },
};
</script>

<style scoped></style>
