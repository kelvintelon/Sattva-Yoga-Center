<template>
  <div class="client-form-template">
    
    <v-form
      ref="form"
      v-model="valid"
      lazy-validation
      class="client-form"
      @submit.prevent="save"
      justify="center" align="center"
    >
    <h1>Edit Your Profile</h1>
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
        :counter="10"
        :rules="nameRules"
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
        label="Stay on Email List?"
        required
      ></v-checkbox>

      

      <v-btn class="mr-4" type="submit" :disabled="invalid"> Save Edits </v-btn>
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
    select: null,
    items: ['AK', 'AL', 'AR', 'AZ', 'CA', 'CO', 'CT', 'DC', 'DE', 'FL', 'GA',
    'HI', 'IA', 'ID', 'IL', 'IN', 'KS', 'KY', 'LA', 'MA', 'MD', 'ME','MI', 'MN', 
    'MO', 'MS', 'MT', 'NC', 'ND', 'NE', 'NH', 'NJ', 'NM','NV', 'NY', 'OH', 'OK', 
    'OR', 'PA', 'RI', 'SC', 'SD', 'TN', 'TX','UT', 'VA', 'VT', 'WA', 'WI', 'WV', 'WY'],
  }),

  methods: {
    save() {

      clientDetailService.updateClientDetailsOfLoggedInUser(this.clientDetails).then(
        (response) => {
          this.$store.commit("SET_CLIENT_DETAILS", response.data);
          this.clientDetails = response.data
        }
      );

      // clientDetailService
      //   .registerClient(this.clientDetails)
      //   .then((response) => {
      //     if (response.status == 201) {
      //       alert ("You have been registered as a client!");
            
      //       // this.$store.commit("SET_CLIENT_ID", response.data.client_id);
      //       this.$store.commit("SET_CLIENT_DETAILS", response.data.clientDetails);
      //       this.reset();
      //       this.$router.push("/registerForClass");
      //     }
      //   })
      //   .catch((error) => {
      //       const response = error.response;
      //       this.registrationErrors = true;
      //       if (response.status === 400) {
      //         this.registrationErrorMsg = "Bad Request: Validation Errors";
      //       }
      //     })
      //   ;

    },
  },
  created() {
      this.clientDetails = this.$store.state.clientDetails
  },
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