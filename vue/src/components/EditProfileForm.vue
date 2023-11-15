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
      align="center">
      <v-col sm="10" md="12" lg="12" justify="center"
      align="center">
      <h1 style="color: rgba(245, 104, 71, 0.95)">Edit Profile</h1>
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
        
        label="Street Address"
      ></v-text-field>

      <v-text-field
        v-model="clientDetails.city"
        :counter="10"
    
        label="City"
      ></v-text-field>

      <v-select
        v-model="clientDetails.state_abbreviation"
        :items="items"
        label="State"
      ></v-select>

      <v-text-field
        v-model="clientDetails.zip_code"
        :counter="10"
    
        label="ZIP"
      ></v-text-field>
      <v-select
        v-model="clientDetails.country"
        :items="countryItems"
        label="Country"
      ></v-select>
      <v-text-field
        v-model="clientDetails.phone_number"
        :counter="15"
        
        label="Phone Number"
      ></v-text-field>

      <v-text-field
        v-model="clientDetails.email"
        :rules="emailRules"
        label="E-mail"
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
      v-if="$store.state.user.username == 'admin'"
        v-model="clientDetails.is_new_client"
        label="Is New Client?"
      ></v-checkbox>
      <v-checkbox
      v-if="$store.state.user.username == 'admin'"
        v-model="clientDetails.has_record_of_liability"
        label="Record of Liability?"
      ></v-checkbox>
      <v-checkbox
      v-if="$store.state.user.username == 'admin'"
        v-model="clientDetails.is_client_active"
        label="Is Client Active?"
      ></v-checkbox>
      <v-checkbox
        v-model="clientDetails.is_on_email_list"
        label="Stay on Email List?"
      ></v-checkbox>
      <v-checkbox
      v-if="$store.state.user.username == 'admin'"
        v-model="clientDetails.is_allowed_video"
        label="Allow Video?"
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
      country: "",
      phone_number: "",
      is_on_email_list: false,
      is_allowed_video: false,
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
    cityRules: [
      (v) => (v && v.length <= 30) || "City must be less than 30 characters",
    ],
    zipRules:[
      (v) => (v && v.length <= 10) || "Zip must be less than 10 characters",
    ],
    addressRules: [
      (v) => (v && v.length <= 30) || "Street must be less than 40 characters",
    ],
    email: "",
    emailRules: [
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
      clientDetailService.getClientDetailsByClientId(parseInt(this.$route.params.clientId)).then((response) => {
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