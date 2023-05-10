<template>
   <v-card class="mx-auto my-2 rounded-xl" max-width="900px" min-width="200px">
      <v-img height="350px" src="@/assets/out-door-yoga-practice.jpg" alt="A group of people doing a yoga position together out on the grass"></v-img>
      <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>Welcome</strong></v-card-title>
      <v-card-text>
        <v-row align="center" class="mx-0">
          Sattva has been serving our Dearborn, Michigan community since January 2007. The central focus of our classes is
          the breath. Our aim is to facilitate a healthy body and a supple mind.
        </v-row>
      </v-card-text>
      <v-divider class="mx-4"></v-divider>
      <!-- <v-row><v-col><v-card-subtitle style="font-size: medium">upcoming classes</v-card-subtitle>

          <v-card-text>
            <v-chip-group v-model="selection" active-class="deep-orange accent-4 white--text" column
              class="d-flex align-start flex-column">
              <v-chip>5:30PM</v-chip>

              <v-chip>7:30PM</v-chip>

              <v-chip>8:00PM</v-chip>

              <v-chip>9:00PM</v-chip>
            </v-chip-group>
          </v-card-text>
        </v-col>
        <v-col align="right"><v-card-subtitle style="font-size: medium" align="right">available packages</v-card-subtitle>

          <v-card-text>
            <v-chip-group v-model="packageSelection" active-class="deep-orange accent-4 white--text" column
              class="d-flex align-end flex-column">
              <v-chip>First Class $10</v-chip>

              <v-chip>First Month $40</v-chip>

              <v-chip>Gift</v-chip>

              <v-chip>More</v-chip>

            </v-chip-group>
          </v-card-text>
        </v-col>
      </v-row> -->
      <!-- <v-divider class="mx-4"></v-divider>
      <v-card-text>In Sanskrit, the ancient language of Yoga, "Sattva" has multiple meanings, some of them being essence, purity, luminosity, and equanimity.</v-card-text> -->
      <v-divider class="mx-4"></v-divider>
      <!-- <v-card-text>Yoga is a state where all movement of thought is suspended. It is manifested through one-pointed focus.
        In this state, one is able to experience clarity and peace.</v-card-text> -->
        <v-card-text><v-btn color="rgba(255, 104, 71, 0.85)" rounded block @click="pushToPackageManagement()"><v-icon>mdi-cart-variant</v-icon>Purchase A Package</v-btn></v-card-text>
    </v-card>  
</template>

<script>
export default {
  name: "welcome-card",
  components: {},
  data() {
    return {
      selection: 0,
    packageSelection: 3,
    };
  },
  methods: {
    pushToPackageManagement() {
      if (this.$store.state.token == "") { 
        this.$router.push({ name: "login" });
      } else {
        if (this.$store.state.user.username == "admin") {
          this.$router.push({ name: "package-management" });
        } else {
          this.$router.push({ name: "client-package-management" });
        }
      }
    },
  },
  created() {},
};
</script>

<style></style>
