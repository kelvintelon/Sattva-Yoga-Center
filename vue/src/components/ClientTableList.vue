<template>
  <v-card>
    <v-card-title>
      Client List
      <v-spacer></v-spacer>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="clientList"
      :search="search"
    >
    <template v-slot:[`item.is_client_active`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_client_active"
            disabled
          ></v-simple-checkbox>
        </template>
        <template v-slot:[`item.is_new_client`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_new_client"
            disabled
          ></v-simple-checkbox>
        </template>
        <template v-slot:[`item.is_on_email_list`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_on_email_list"
            disabled
          ></v-simple-checkbox>
        </template>
        <template v-slot:[`item.has_record_of_liability`]="{ item }">
          <v-simple-checkbox
            v-model="item.has_record_of_liability"
            disabled
          ></v-simple-checkbox>
        </template></v-data-table>
  </v-card>
</template>

<script>

import clientDetailService from "../services/ClientDetailService";


export default {
  data() {
    return {
      search: "",
      headers: [
        {
          text: "First Name",
          align: "start",
          sortable: true,
          value: "first_name",
        },
        { text: "Last Name", value: "last_name", sortable: true},
        { text: "Active", value: "is_client_active" },
        { text: "New Client", value: "is_new_client" },
        { text: "Address", value: "full_address", sortable: false},
        { text: "Phone Number", value: "phone_number", sortable: false},
        { text: "Email List", value: "is_on_email_list", sortable: true},
        { text: "Email", value: "email", sortable: false},
        { text: "Record of Liability", value: "has_record_of_liability", sortable: true},
        { text: "Date of Entry", value: "date_of_entry", sortable: true},
      ],
      clientList: [],
    };
  },
  created() {
      this.getClientTable();
  },
 methods: {
  getClientTable() {
      clientDetailService.getClientList().then((response) => {
          if(response.status == 200) {
              this.$store.commit("SET_CLIENT_LIST", response.data);
              this.clientList = response.data;
          } else {
              alert("Error retrieving client information")
          }
      });
  }
 },
  
};
</script>

<style>
</style>