<template>
  <div>
    <h1>Class Registration</h1>
    <h2>New Client</h2>
    <h2>First class $10</h2>
    <v-btn elevation="5" v-on:click="buyFirstClass()">
      <v-icon> mdi-cart </v-icon> Buy Class
    </v-btn>
  </div>
</template>

<script>
import classRegistrationService from "../services/ClassRegistrationService";

export default {
  name: "class-registration-select",
  data() {
    return {
      classAttendance: {
        class_id: 1,
        client_id: 1,
        package_id: 1,
        class_purchase_id: 3,
        is_new_client: true,
        is_drop_in: true,
        drop_in_fee: 0,
        mat_use_fee: 0,
        is_guest: false,
        attendance_count: 1,
      },
    };
  },
  methods: {
    buyFirstClass() {


      classRegistrationService
        .registerClass(this.classAttendance)
        .then((response) => {
          if (response.status == 201) {
            alert("You have registered your class!");
            this.$router.push("/");
          }
        })
        .catch((error) => {
          const response = error.response;
          this.registrationErrors = true;
          if (response.status === 400) {
            this.registrationErrorMsg = "Bad Request: Validation Errors";
          }
        });
      // this.$router.push({ name: "checkout" });
    },
  },
  created() {},
};
</script>

<style>
</style>