<template>

    <div class="client-form-template">
      <v-form ref="form" v-model="valid" lazy-validation>
        <v-text-field
          v-model="firstName"
          :counter="10"
          :rules="nameRules"
          label="First Name"
          required
        ></v-text-field>

        <v-text-field
          v-model="lastName"
          :counter="10"
          :rules="nameRules"
          label="Last Name"
          required
        ></v-text-field>

        <v-text-field
          v-model="streetAddress"
          :counter="10"
          :rules="addressRules"
          label="Street Address"
          required
        ></v-text-field>

        <v-text-field
          v-model="city"
          :counter="10"
          :rules="nameRules"
          label="City"
          required
        ></v-text-field>

        <v-select
          v-model="select"
          :items="items"
          :rules="[(v) => !!v || 'Item is required']"
          label="State"
          required
        ></v-select>

        <v-text-field
          v-model="zipCode"
          :counter="10"
          :rules="nameRules"
          label="ZIP"
          required
        ></v-text-field>

        <v-text-field
          v-model="phoneNumber"
          :counter="10"
          :rules="nameRules"
          label="Phone Number"
          required
        ></v-text-field>

        <v-text-field
          v-model="email"
          :rules="emailRules"
          label="E-mail"
          required
        ></v-text-field>

        <v-select
          v-model="select"
          :items="items"
          :rules="[(v) => !!v || 'Item is required']"
          label="Item"
          required
        ></v-select>

        <v-btn color="error" class="mr-4" @click="reset"> Reset Form </v-btn>

        <v-btn class="mr-4" type="submit" :disabled="invalid"> submit </v-btn>
      </v-form>
    </div>

</template>

<script>
export default {
  data: () => ({
    valid: true,
    firstName: "",

    nameRules: [
      (v) => !!v || "Name is required",
      (v) => (v && v.length <= 10) || "Name must be less than 10 characters",
    ],

    addressRules: [
      (v) => !!v || "Name is required",
      (v) => (v && v.length <= 10) || "Name must be less than 10 characters",
    ],
    email: "",
    emailRules: [
      (v) => !!v || "E-mail is required",
      (v) => /.+@.+\..+/.test(v) || "E-mail must be valid",
    ],
    select: null,
    items: [ 'AL', 'AK', 'AS', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'DC', 'FM', 'FL', 'GA', 'GU', 'HI', 'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MH', 'MD', 'MA', 'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH', 'NJ', 'NM', 'NY', 'NC', 'ND', 'MP', 'OH', 'OK', 'OR', 'PW', 'PA', 'PR', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VT', 'VI', 'VA', 'WA', 'WV', 'WI', 'WY' ],
  }),

  methods: {
    reset() {
      this.$refs.form.reset();
    },
  },
};
</script>


<style>
.client-form-template {
  display: flex;
  justify-content: center;
}
</style>