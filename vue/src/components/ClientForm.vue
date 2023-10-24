<template>
  <v-card class="mx-auto my-12 pb-12">
    <v-row justify="center" align="center">
      <v-spacer></v-spacer>
      <v-col lg="4" sm="10" justify="center" align="center">
        <v-form
          ref="form"
          v-model="valid"
          lazy-validation
          class="client-form-template"
          @submit.prevent="submit"
        >
          <h1 style="color: rgba(245, 104, 71, 0.95)">Set Up Your Profile</h1>
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
          ></v-text-field>

          <v-text-field
            v-model="clientDetails.city"
            :counter="10"
            :rules="cityRules"
            label="City"
          ></v-text-field>

          <v-select
            v-model="clientDetails.state_abbreviation"
            :items="items"
            :rules="[(v) => !!v || 'Item is required']"
            label="State"
          ></v-select>

          <v-text-field
            v-model="clientDetails.zip_code"
            :counter="10"
            :rules="zipRules"
            label="ZIP"
          ></v-text-field>
          <v-select
            v-model="clientDetails.country"
            :items="countryItems"
            :rules="[(v) => !!v || 'Item is required']"
            label="Country"
          ></v-select>
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
          ></v-checkbox>
          <v-checkbox
            v-model="clientDetails.has_record_of_liability"
            label="Record of Liability?"
          ></v-checkbox>
          <v-btn
            v-if="!readLiabilityRelease"
            @click="readLiabilityRelease = true"
          >
            Read and Accept Studio Liability Release
          </v-btn>
          <div v-else>
            Studio Liability Release: I understand that yoga can be physically
            intensive and I voluntarily assume the risk inherent in my
            participation in classes provided by Sattva Yoga Center, including
            the risk of injury, accident, death, loss, cost or damage to my
            person, my family members or to my guests, and I release and
            indemnify Sattva Yoga Center from and against any and all such
            claims and liabilities, including attorneys’ fees. I further attest
            that I am in sufficient physical health, and/or that I have
            consulted with a physician and I am able to undertake and engage in
            the physical movements and exercises in classes that I have chosen
            to take provided by Sattva Yoga Center. I assume responsibility to
            update Sattva Yoga Center of any changes in my medical condition
            that might affect my safety or participation in classes at Sattva
            Yoga Center.
          </div>
          <v-checkbox
            v-model="acceptTerms"
            label="I Understand."
            :rules="[(v) => !!v || 'You must agree to continue!']"
            required
          ></v-checkbox>
          <v-btn color="error" class="mr-4 my-4" @click="reset">
            Reset Form
          </v-btn>

          <v-btn class="mr-4" type="submit" :disabled="invalid"> submit </v-btn>
          <div
            class="alert alert-danger"
            role="alert"
            style="color: red"
            v-if="emailRegistrationErrors"
          >
            {{ emailRegistrationErrorMsg }}
          </div>
        </v-form>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
  </v-card>
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
      is_client_active: false,
      is_new_client: true,
      street_address: "",
      city: "",
      state_abbreviation: "",
      zip_code: "",
      country: "",
      phone_number: "",
      is_on_email_list: false,
      email: "",
      has_record_of_liability: false,
      date_of_entry: "",
      user_id: 0,
      is_allowed_video: false,
    },
    emailRegistrationErrors: false,
    emailRegistrationErrorMsg:
      "There were problems registering with this email.",
    nameRules: [
      (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
    ],
    cityRules: [
      (v) => (v && v.length <= 30) || "City must be less than 30 characters",
    ],
    zipRules: [
      (v) => (v && v.length <= 10) || "Zip must be less than 10 characters",
    ],
    addressRules: [
      (v) => (v && v.length <= 30) || "Street must be less than 40 characters",
    ],
    email: "",
    emailRules: [(v) => /.+@.+\..+/.test(v) || "E-mail must be valid"],
    phoneRules: [
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
    countryItems: [
    "United States",
    "Afghanistan",
  "Albania",
  "Algeria",
  "Andorra",
  "Angola",
  "Antigua and Barbuda",
  "Argentina",
  "Armenia",
  "Australia",
  "Austria",
  "Azerbaijan",
  "Bahamas",
  "Bahrain",
  "Bangladesh",
  "Barbados",
  "Belarus",
  "Belgium",
  "Belize",
  "Benin",
  "Bhutan",
  "Bolivia",
  "Bosnia and Herzegovina",
  "Botswana",
  "Brazil",
  "Brunei",
  "Bulgaria",
  "Burkina Faso",
  "Burundi",
  "Cabo Verde",
  "Cambodia",
  "Cameroon",
  "Canada",
  "Central African Republic",
  "Chad",
  "Chile",
  "China",
  "Colombia",
  "Comoros",
  "Congo",
  "Costa Rica",
  "Croatia",
  "Cuba",
  "Cyprus",
  "Czechia",
  "Democratic Republic of the Congo",
  "Denmark",
  "Djibouti",
  "Dominica",
  "Dominican Republic",
  "East Timor",
  "Ecuador",
  "Egypt",
  "El Salvador",
  "Equatorial Guinea",
  "Eritrea",
  "Estonia",
  "Eswatini",
  "Ethiopia",
  "Fiji",
  "Finland",
  "France",
  "Gabon",
  "Gambia",
  "Georgia",
  "Germany",
  "Ghana",
  "Greece",
  "Grenada",
  "Guatemala",
  "Guinea",
  "Guinea-Bissau",
  "Guyana",
  "Haiti",
  "Honduras",
  "Hungary",
  "Iceland",
  "India",
  "Indonesia",
  "Iran",
  "Iraq",
  "Ireland",
  "Israel",
  "Italy",
  "Ivory Coast",
  "Jamaica",
  "Japan",
  "Jordan",
  "Kazakhstan",
  "Kenya",
  "Kiribati",
  "Kosovo",
  "Kuwait",
  "Kyrgyzstan",
  "Laos",
  "Latvia",
  "Lebanon",
  "Lesotho",
  "Liberia",
  "Libya",
  "Liechtenstein",
  "Lithuania",
  "Luxembourg",
  "Madagascar",
  "Malawi",
  "Malaysia",
  "Maldives",
  "Mali",
  "Malta",
  "Marshall Islands",
  "Mauritania",
  "Mauritius",
  "Mexico",
  "Micronesia",
  "Moldova",
  "Monaco",
  "Mongolia",
  "Montenegro",
  "Morocco",
  "Mozambique",
  "Myanmar",
  "Namibia",
  "Nauru",
  "Nepal",
  "Netherlands",
  "New Zealand",
  "Nicaragua",
  "Niger",
  "Nigeria",
  "North Korea",
  "North Macedonia",
  "Norway",
  "Oman",
  "Pakistan",
  "Palau",
  "Palestine",
  "Panama",
  "Papua New Guinea",
  "Paraguay",
  "Peru",
  "Philippines",
  "Poland",
  "Portugal",
  "Qatar",
  "Romania",
  "Russia",
  "Rwanda",
  "Saint Kitts and Nevis",
  "Saint Lucia",
  "Saint Vincent and the Grenadines",
  "Samoa",
  "San Marino",
  "Sao Tome and Principe",
  "Saudi Arabia",
  "Senegal",
  "Serbia",
  "Seychelles",
  "Sierra Leone",
  "Singapore",
  "Slovakia",
  "Slovenia",
  "Solomon Islands",
  "Somalia",
  "South Africa",
  "South Korea",
  "South Sudan",
  "Spain",
  "Sri Lanka",
  "Sudan",
  "Suriname",
  "Sweden",
  "Switzerland",
  "Syria",
  "Taiwan",
  "Tajikistan",
  "Tanzania",
  "Thailand",
  "Togo",
  "Tonga",
  "Trinidad and Tobago",
  "Tunisia",
  "Turkey",
  "Turkmenistan",
  "Tuvalu",
  "Uganda",
  "Ukraine",
  "United Arab Emirates",
  "United Kingdom",
  "Uruguay",
  "Uzbekistan",
  "Vanuatu",
  "Vatican City",
  "Venezuela",
  "Vietnam",
  "Yemen",
  "Zambia",
  "Zimbabwe"
    ],
    formComplete: false,
    acceptTerms: false,
    readLiabilityRelease: false,
  }),

  methods: {
    reset() {
      this.$refs.form.reset();
    },
    checkForm() {
      if (
        this.clientDetails.last_name == "" ||
        this.clientDetails.first_name == ""
      ) {
        alert("Please fill out the form with your name");
      } else if (this.clientDetails.email == "") {
        alert("Please fill out the form with your email");
      } else if (this.clientDetails.phone_number == "") {
        alert("Please fill out the form with your phone number");
      } else if (this.acceptTerms == false) {
        alert("To complete the form, please read and accept the terms");
      } else {
        this.formComplete = true;
      }
    },
    submit() {
      this.checkForm();
      if (this.formComplete) {
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
              this.emailRegistrationErrors = true;
              if (response.status === 400) {
                this.emailRegistrationErrorMsg =
                  "There were problems registering this email.";
              }
            });
        } else {
          alert("You are already registered as a client!");
          this.$router.push({ name: "profile-page" });
        }
      }
    },
    clearErrors() {
      this.emailRegistrationErrors = false;
      this.emailRegistrationErrorMsg =
        "There were problems registering this email.";
    },
  },
  created() {
    if (this.$store.state.clientDetails.client_id > 0) {
      this.$router.push({ name: "home" });
    }
  },
};
</script>


<style scoped>
.client-form-template {
  display: flex;
  flex-direction: column;
}

.client-form {
  width: 40%;
}
</style>