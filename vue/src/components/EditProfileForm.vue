<template>
<v-container>
  <v-row>
    <v-col sm="12">
    <v-form
      ref="form"
      v-model="valid"
      lazy-validation
      class="client-form"
      @submit.prevent="save()"
      

    >
    <v-container>
       <v-row justify="center"
      align="center"><v-col justify="center"
      align="center">
      <h1 style="color: rgba(245, 104, 71, 0.95)">Edit Profile</h1>
      </v-col>
      <!-- // TODO: Fix the code here -->
      <v-col cols="12" md="12" justify="center"
      align="center">

      <v-text-field
        v-model="clientDetails.first_name"
        :counter="20"
        :rules="nameRules"
        label="First Name"
        required
      ></v-text-field>
</v-col></v-row>
      <v-text-field
        v-model="clientDetails.last_name"
        :counter="20"
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
        :rules="phoneeRules"
        label="Phone Number"
        required
      ></v-text-field>

      <v-text-field
        v-model="clientDetails.email"
        :rules="emailRules"
        label="E-mail"
        required
      ></v-text-field>
      <div
            class="alert alert-danger"
            role="alert"
            v-if="emailRegistrationErrors"
            style="color: red"
          >
            {{ emailRegistrationErrorMsg }}
          </div>
      <v-checkbox
        v-model="clientDetails.is_on_email_list"
        label="Stay on Email List?"
        required
      ></v-checkbox>

      <v-alert type="success" v-show="showAlert">
        You edited your profile.
        <v-btn color="warning" @click="clickOkay()"> Okay </v-btn>
      </v-alert>

      <v-btn
        class="mr-4"
        type="submit"
        color="amber lighten-4"
        :disabled="invalid"
      >
        Save Edits
      </v-btn>
      </v-container>
    </v-form>
    </v-col>
  </v-row>
    </v-container>
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
     emailRegistrationErrors: false,
      emailRegistrationErrorMsg: 'There were problems registering with this email.',
    showAlert: false,
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
  }),

  methods: {
    displayAlert() {
      this.showAlert = true;
    },
    getImageUral(img) {
      return require(`@/assets/${img}`)
    },
    clickOkay() {
      
        this.showAlert = false;
        this.clearErrors();
      
    },
    save() {
      // I believe it still works without the following line?
        // this.clientDetails = this.$store.state.clientDetails;
      clientDetailService
        .updateClientDetails(this.clientDetails)
        .then((response) => {          
          response
          this.$store.commit("SET_CLIENT_DETAILS", this.clientDetails);
          this.emailRegistrationErrors = false;
          // TODO: replace this v-alert with a v-snackbar
          this.displayAlert();

        })
        .catch((error) => {
              const response = error.response;
              this.emailRegistrationErrors = true;
              if (response.status === 400) {
                this.emailRegistrationErrorMsg = "Could not update. Email is already being used by an account";
              }
            });

    },
    clearErrors() {
      this.emailRegistrationErrors = false;
      this.emailRegistrationErrorMsg = 'There were problems updating this email.';
    },
  },
  created() {
    if (this.$store.state.user.username == "admin") {
      clientDetailService.getClientDetailsByClientId(this.$route.params.clientId).then((response) => {
         if (response.status == 200) {
           this.clientDetails = response.data;
         } else {
           alert("Error retrieving client information")
         }
      })
    } else {
      this.clientDetails = this.$store.state.clientDetails;
    }
    
  },
};
</script>


<style scoped>



</style>