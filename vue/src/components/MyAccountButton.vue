<template>
  <div class="my-account-button">
    <v-row>
      <v-col class="shrink">
        <v-btn
          elevation="5"
          v-on:click="goToPage()"
          @mouseover="expandMore"
          @mouseleave="expandLess"
        >
          <v-icon> mdi-yoga </v-icon> my account
        </v-btn>

        <!-- <v-expand-x-transition>
          <v-btn v-show="expand" justify="center">Log Out</v-btn>
        </v-expand-x-transition> -->
      </v-col>
    </v-row>
  </div>
</template>

<script>
export default {
  name: "my-account-button",
  data() {
    return {
      expand: false,
    };
  },
  methods: {
    goToPage() {
      this.$router.push({ name: "profile-page" });
    },
    expandMore() {
      this.expand = true;
    },
    expandLess() {
      this.expand = false;
    },
  },
  created() {},
};
</script>

<style>
</style>