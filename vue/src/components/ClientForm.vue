<template>
  <div class="client-form-template">
    <v-form
      ref="form"
      v-model="valid"
      lazy-validation
      class="client-form"
      @submit.prevent="submit"
      justify="center"
      align="center"
    >
      <h1>Set Up Your Profile</h1>
      <v-text-field
        v-model="clientDetails.first_name"
        :counter="10"
        :rules="nameRules"
        label="First Name"
        required
      ></v-text-field>

      <v-text-field
        v-model="clientDetails.last_name"
        :counter="10"
        :rules="nameRules"
        label="Last Name"
        required
      ></v-text-field>

      <v-text-field
        v-model="clientDetails.street_address"
        :counter="30"
        :rules="addressRules"
        label="Street Address"
        required
      ></v-text-field>

      <v-text-field
        v-model="clientDetails.city"
        :counter="10"
        :rules="nameRules"
        label="City"
        required
      ></v-text-field>

      <v-select
        v-model="clientDetails.state_abbreviation"
        :items="items"
        :rules="[(v) => !!v || 'Item is required']"
        label="State"
        required
      ></v-select>

      <v-text-field
        v-model="clientDetails.zip_code"
        :counter="10"
        :rules="nameRules"
        label="ZIP"
        required
      ></v-text-field>

      <v-text-field
        v-model="clientDetails.phone_number"
        :counter="15"
        :rules="phoneRules"
        label="Phone Number"
        required
      ></v-text-field>

      <v-text-field
        v-model="clientDetails.email"
        :rules="emailRules"
        label="E-mail"
        required
      ></v-text-field>

      <v-checkbox
        v-model="clientDetails.is_on_email_list"
        label="Join Email List?"
        required
      ></v-checkbox>

      <v-btn color="error" class="mr-4" @click="reset"> Reset Form </v-btn>

      <v-btn class="mr-4" type="submit" :disabled="invalid"> submit </v-btn>
    </v-form>
  </div>
</template>

<script>
import clientDetailService from "../services/ClientDetailService";

export default {
  data: () => ({
    valid: true,
    firstName: "",
    clientDetails: {
      last_name: "",
      first_name: "",
      is_client_active: true,
      is_new_client: true,
      street_address: "",
      city: "",
      state_abbreviation: "",
      zip_code: "",
      phone_number: "",
      is_on_email_list: false,
      email: "",
      has_record_of_liability: false,
      date_of_entry: "",
      user_id: 0,
    },
    nameRules: [
      (v) => !!v || "Name is required",
      (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
    ],

    addressRules: [
      (v) => !!v || "Name is required",
      (v) => (v && v.length <= 30) || "Street must be less than 40 characters",
    ],
    email: "",
    emailRules: [
      (v) => !!v || "E-mail is required",
      (v) => /.+@.+\..+/.test(v) || "E-mail must be valid",
    ],
    phoneRules: [
      (v) => !!v || "Phone is required",
      (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
    ],
    select: null,
    items: [
      "AK",
      "AL",
      "AR",
      "AZ",
      "CA",
      "CO",
      "CT",
      "DC",
      "DE",
      "FL",
      "GA",
      "HI",
      "IA",
      "ID",
      "IL",
      "IN",
      "KS",
      "KY",
      "LA",
      "MA",
      "MD",
      "ME",
      "MI",
      "MN",
      "MO",
      "MS",
      "MT",
      "NC",
      "ND",
      "NE",
      "NH",
      "NJ",
      "NM",
      "NV",
      "NY",
      "OH",
      "OK",
      "OR",
      "PA",
      "RI",
      "SC",
      "SD",
      "TN",
      "TX",
      "UT",
      "VA",
      "VT",
      "WA",
      "WI",
      "WV",
      "WY",
    ],
    formIncomplete: true,
  }),

  methods: {
    reset() {
      this.$refs.form.reset();
    },
    checkForm() {
      if (
        this.clientDetails.last_name == "" ||
        this.clientDetails.first_name == "" ||
        this.clientDetails.street_address == "" ||
        this.clientDetails.city == "" ||
        this.clientDetails.state_abbreviation == "" ||
        this.clientDetails.zip_code == "" ||
        this.clientDetails.phone_number == "" ||
        this.clientDetails.email == ""
      ) {
        alert("Please fill out your form");
      } else {
        this.formIncomplete = false;
      }
    },
    submit() {
      this.checkForm();
      if (this.formIncomplete == false) {
        this.clientDetails.user_id = this.$store.state.user.id;
        this.clientDetails.date_of_entry = new Date().toISOString();

        // if the client isn't registered then do the following:
        if (
          this.$store.state.clientDetails.client_id == 0 ||
          Object.keys(this.$store.state.clientDetails).length === 0
        ) {
          clientDetailService
            .registerClient(this.clientDetails)
            .then((response) => {
              if (response.status == 201) {
                alert("You have been registered as a client!");
                this.$store.commit(
                  "SET_CLIENT_DETAILS",
                  response.data.clientDetails
                );
                this.reset();
                this.$router.push("/registerForClass");
              }
            })
            .catch((error) => {
              const response = error.response;
              this.registrationErrors = true;
              if (response.status === 400) {
                this.registrationErrorMsg = "Bad Request: Validation Errors";
              }
            });
        } else {
          alert("You are already registered as a client!");
          this.$router.push({ name: "profile-page" });
        }
      }
    },
  },
  created() {},
};
</script>


<style scoped>
.client-form-template {
  display: flex;
  justify-content: center;
}

.client-form {
  width: 40%;
}
</style>