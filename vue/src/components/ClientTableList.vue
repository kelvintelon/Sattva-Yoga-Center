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
    <v-data-table :headers="headers" :items="clientList" :search="search">
      <template v-slot:top>
        <v-toolbar flat>
          <!-- START OF EDIT CLIENT FORM -->
          <v-dialog v-model="dialog" max-width="500px">
            <v-card justify="center" align="center">
              <v-card-title justify="center" align="center">
                <span class="text-h5" align="center"> Edit Client </span>
              </v-card-title>
              
              <v-container>
                <v-row justify="center" style="min-height: 160px">
                  <v-col cols="11">
                    <v-form
                      ref="form"
                      height="100"
                      width="500"
                      v-model="valid"
                      lazy-validation
                      class="class-form mx-auto white"
                      @submit.prevent="update"
                      justify="center"
                      align="center"
                    >
                      <v-text-field
                        v-model="editedItem.first_name"
                        :counter="20"
                        :rules="nameRules"
                        label="First Name"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="editedItem.last_name"
                        :counter="20"
                        :rules="nameRules"
                        label="Last Name"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="editedItem.street_address"
                        :counter="30"
                        :rules="addressRules"
                        label="Street Address"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="editedItem.city"
                        :counter="10"
                        :rules="nameRules"
                        label="City"
                        required
                      ></v-text-field>

                      <v-select
                        v-model="editedItem.state_abbreviation"
                        :items="states"
                        :rules="[(v) => !!v || 'Item is required']"
                        label="State"
                        required
                      ></v-select>

                      <v-text-field
                        v-model="editedItem.zip_code"
                        :counter="10"
                        :rules="nameRules"
                        label="ZIP"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="editedItem.phone_number"
                        :counter="15"
                        :rules="nameRules"
                        label="Phone Number"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="editedItem.email"
                        :rules="emailRules"
                        label="E-mail"
                        required
                      ></v-text-field>
                      <v-checkbox
                        v-model="editedItem.is_new_client"
                        label="Is New Client?"
                        required
                      ></v-checkbox>
                      <v-checkbox
                        v-model="editedItem.has_record_of_liability"
                        label="Record of Liability"
                        required
                      ></v-checkbox>
                      <v-checkbox
                        v-model="editedItem.is_client_active"
                        label="Is Client Active?"
                        required
                      ></v-checkbox>

                      <v-checkbox
                        v-model="editedItem.is_on_email_list"
                        label="Stay on Email List?"
                        required
                      ></v-checkbox>

                      <v-row justify="center" align="center"
                        >
                        <v-col justify="center" align="center">
                          <div
            class="alert alert-danger"
            role="alert"
            v-if="emailRegistrationErrors"
            style="color: red"
          >
            {{ emailRegistrationErrorMsg }}
          </div>
                          <v-btn class="mr-4" type="submit" :disabled="invalid">
                            update
                          </v-btn>
                          </v-col
                        >
                        <v-col cols="10" justify="center" align="center">
                          <v-btn color="error" class="mr-4" @click="reset">
                            Reset Form
                          </v-btn>
                        </v-col></v-row
                      >
                    </v-form>
                  </v-col>
                </v-row>
              </v-container>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <!-- <v-btn color="blue darken-1" text @click="save"> Save </v-btn> -->
              </v-card-actions>
            </v-card>
          </v-dialog>
          <!-- END OF EDIT CLIENT FORM -->
          <v-dialog v-model="dialogDelete" max-width="500px">
            <v-card>
              <v-card-title class="text-h5"
                >Are you sure you want to delete this client?</v-card-title
              >
              <v-card-title class="text-h6"
                >This will delete the client everywhere else too</v-card-title
              >
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="closeDelete"
                  >Cancel</v-btn
                >
                <v-btn color="blue darken-1" text @click="deleteItemConfirm"
                  >OK</v-btn
                >
                <v-spacer></v-spacer>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
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
        ></v-simple-checkbox> </template
      ><template v-slot:[`item.actions`]="{ item }">
        <v-icon small class="mr-2" @click.prevent="sendToUserPageAdminView(item)"> mdi-account-search </v-icon>
        <v-icon small class="mr-2" @click="editItem(item)"> mdi-pencil </v-icon>
        <v-icon small @click="deleteItem(item)"> mdi-delete </v-icon>
      </template>
      <template v-slot:no-data>
        <v-btn color="primary" @click="initialize"> Reset </v-btn>
      </template>
    </v-data-table>
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
        { text: "Last Name", value: "last_name", sortable: true },
        { text: "Active", value: "is_client_active" },
        { text: "New Client", value: "is_new_client" },
        { text: "Address", value: "full_address", sortable: false },
        { text: "Phone Number", value: "phone_number", sortable: false },
        { text: "Email List", value: "is_on_email_list", sortable: true },
        { text: "Email", value: "email", sortable: false },
        {
          text: "Record of Liability",
          value: "has_record_of_liability",
          sortable: true,
        },
        { text: "Date of Entry", value: "date_of_entry", sortable: true },
        { text: "Actions", value: "actions", sortable: false },
      ],
      clientList: [],
      dialogDelete: false,
      dialog: false,
      editedIndex: -1,
      editedItem: {
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
      states: [
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
      editFormIncomplete: true,
      nameRules: [
        (v) => !!v || "Name is required",
        (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
      ],

      addressRules: [
        (v) => !!v || "Name is required",
        (v) =>
          (v && v.length <= 30) || "Street must be less than 40 characters",
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
      
    };
  },
  created() {
    this.getClientTable();
  },
  methods: {
    sendToUserPageAdminView(object) {
      this.$store.commit("SET_CLIENT_DETAILS", object);
      this.$router.push({name: "client-details-admin-view", params: {clientId: object.client_id}})
    },
    update() {
      this.checkEditForm();

      if (this.editFormIncomplete == false) {
        clientDetailService
          .updateClientDetails(this.editedItem)
          .then((response) => {
            response;
            this.getClientTable();
            alert("Edit client succesful!");
            this.clearErrors();
            this.close();
          })
          .catch((error) => {
              const response = error.response;
              this.emailRegistrationErrors = true;
              if (response.status === 400) {
                this.emailRegistrationErrorMsg = "Could not update. Email is already being used by an account";
              }
            });
      }
    },
    clearErrors() {
      this.emailRegistrationErrors = false;
      this.emailRegistrationErrorMsg = 'There were problems updating this email.';
    },
    getClientTable() {
      clientDetailService.getClientList().then((response) => {
        if (response.status == 200) {
          this.clientList = response.data;
           this.clientList.forEach((item) => {
             if (item.full_address.includes("null")) {
               item.full_address = item.full_address.replaceAll("null", " ")
             }
            item.date_of_entry = new Date(item.date_of_entry);
          })
          this.$store.commit("SET_CLIENT_LIST", response.data);
          
        } else {
          alert("Error retrieving client information");
        }
      });
    },
    editItem(item) {
      this.editedIndex = this.clientList.indexOf(item);

      this.editedItem = Object.assign({}, item);
      this.dialog = true;
    },

    deleteItem(item) {
      this.editedIndex = this.clientList.indexOf(item);

      this.editedItem = Object.assign({}, item);
      this.dialogDelete = true;
    },
    deleteItemConfirm() {
      clientDetailService
        .removeClient(this.editedItem.client_id)
        .then((response) => {
          if (response.status == 200) {
            this.clientList.splice(this.editedIndex, 1);
            alert("Client successfully removed!");
          } else {
            alert("Error removing client!");
          }
        });
      this.closeDelete();
    },
    closeDelete() {
      this.dialogDelete = false;
    },
    close() {
      this.dialog = false;
      this.reset();
      this.clearErrors();
    },
    reset() {
      this.$refs.form.reset();
      this.clearErrors();
    },
    checkEditForm() {
      if (
        this.editedItem.last_name == "" ||
        this.editedItem.first_name == ""
      ) {
        alert("Please fill out your form");
      } else {
        this.editFormIncomplete = false;
      }
    },
  },
};
</script>

<style>
</style>