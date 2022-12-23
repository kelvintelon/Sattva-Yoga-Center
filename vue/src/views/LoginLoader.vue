<template>
  <v-container>
    <v-row>
      <v-col cols="12" md="4">
        <v-skeleton-loader
          type="card-avatar, article, actions"
        ></v-skeleton-loader>

        <v-skeleton-loader type="date-picker"></v-skeleton-loader>
      </v-col>

      <v-col cols="12" md="4">
        <v-skeleton-loader type="article, actions"></v-skeleton-loader>

        <v-skeleton-loader
          type="table-heading, list-item-two-line, image, table-tfoot"
        ></v-skeleton-loader>
      </v-col>

      <v-col cols="12" md="4">
        <v-skeleton-loader
          type="list-item-avatar, divider, list-item-three-line, card-heading, image, actions"
        ></v-skeleton-loader>

        <v-skeleton-loader
          type="list-item-avatar-three-line, image, article"
        ></v-skeleton-loader>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import clientDetailService from "../services/ClientDetailService";

export default {
  name: "login-loader",
  clientProfile: {
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
  created() {
    clientDetailService.getClientDetailsOfLoggedInUser().then((response) => {
        if (response.data.client_id != 0) {
            this.clientProfile = response.data;
            this.$store.commit("SET_CLIENT_DETAILS", response.data);
            this.$router.push("/");
        } else {
            this.$router.push("/clientRegistration");
        }
    });
  },
  methods: {
  },
};
</script>

<style>
</style>