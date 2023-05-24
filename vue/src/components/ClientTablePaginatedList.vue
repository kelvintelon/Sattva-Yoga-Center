<template>
  <v-card>
    <v-card-title>
      Client List
      <v-divider class="mx-4" inset vertical></v-divider>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="clientList"
      :options.sync="options"
      :server-items-length="totalClients"
      :loading="loading"
      class="elevation-1"
      hide-default-footer
    ></v-data-table>
    <v-row>
      <v-col cols="11">
        <v-pagination
          v-model="page"
          :length="totalClients / pageSize"
          @input="temporaryPageMethod"
        ></v-pagination>
      </v-col>
      <v-col col="1">
        <v-select
          
          v-model="pageSize"
          :items="[10, 20, 30, 40, 50]"
          outlined
          filled
          @change="temporaryPageSizeMethod"
        >
        </v-select>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
  </v-card>
</template>
  
  <script>
import clientDetailService from "../services/ClientDetailService";

export default {
  data() {
    return {
      search: "",
      page: 1,
      pageSize: 20,
      headers: [
        { text: "ID", sortable: true, value: "client_id" },
        {
          text: "First Name",
          align: "start",
          sortable: true,
          value: "first_name",
        },
        { text: "Last Name", value: "last_name", sortable: true },
        { text: "Active", value: "is_client_active" },
        { text: "New Client", value: "is_new_client" },
        { text: "Address", value: "full_address", sortable: false },
        { text: "Phone Number", value: "phone_number", sortable: false },
        { text: "Email List", value: "is_on_email_list", sortable: true },
        { text: "Email", value: "email", sortable: false },
        { text: "Shared Group", value: "family_name", sortable: true },
        {
          text: "Record of Liability",
          value: "has_record_of_liability",
          sortable: true,
        },
        { text: "Date of Entry", value: "date_of_entry", sortable: true },
        { text: "Actions", value: "actions", sortable: false },
      ],
      clientList: [],
      totalClients: 0,
      paginatedObject: {},
      loading: false,
    };
  },
  created() {
    this.getPaginatedClientTable();
  },
  watch: {
    options: {
      handler() {
        this.getPaginatedClientTable();
      },
      deep: true,
    },
  },
  methods: {
    getPaginatedClientTable() {
      this.loading = true;
      clientDetailService
        .getPaginatedClients(this.page,this.pageSize)
        .then((response) => {
          if (response.status == 200) {
            this.loading = false;
            this.paginatedObject = response.data;
            this.clientList = this.paginatedObject.listOfClients;
            this.totalClients = this.paginatedObject.totalRows;
            this.clientList.forEach((item) => {
              if (item.full_address.includes("null")) {
                item.full_address = item.full_address.replaceAll("null", " ");
              }
              item.date_of_entry = new Date(item.date_of_entry);
            });
            this.$store.commit("SET_CLIENT_LIST", response.data);
          } else {
            alert("Error retrieving client information");
          }
        })
        .catch((error) => {
          const response = error.response;
          if (response.status === 401) {
            this.$router.push("/login");
          }
        });
    },
    temporaryPageMethod() {
      alert(this.page);
      this.getPaginatedClientTable();
    },
    temporaryPageSizeMethod() {
        this.page = 1;

      alert(this.pageSize);
      this.getPaginatedClientTable()
    },
  },
};
</script>
  
  <style>
</style>