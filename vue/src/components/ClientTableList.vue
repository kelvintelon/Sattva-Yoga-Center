<template>
  <v-card>
    <v-card-title>
      Client List
       <v-divider class="mx-4" inset vertical></v-divider>
          <v-btn
            color="primary"
            dark
            class="mb-2"
            v-bind="attrs"
            v-on="on"
            @click.prevent="emailRecipients"
            title="Email Selected Client(s)"
          >
            <v-icon>mdi-order-bool-ascending-variant</v-icon>
            <v-icon>mdi-email</v-icon>
          </v-btn>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-btn
            color="#9948B6ED"
            dark
            class="mb-2"
            v-bind="attrs"
            v-on="on"
            @click.prevent="emailRecipientsFromEmailList"
            title="Email List"
          >
            <v-icon>mdi-email-plus</v-icon>
          </v-btn>
      <v-spacer></v-spacer>

      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
        @keyup.enter="getSearchedClientTablePaginated"
      ></v-text-field>
      <v-spacer></v-spacer>

      <v-dialog v-model="mergeDialog" max-width="500px">
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on" @click.prevent="retrieveDuplicateClients">
            Merge
          </v-btn>
        </template>
        <v-card>
          <!-- Merging Clients starts here -->
          <v-card-title>
            <span class="text-h5">Merge Clients</span>
            <v-spacer></v-spacer>
          </v-card-title>

          <v-card-text>
            <v-container>
              <v-row>
                <v-col>
                <!-- Full list of clients autocomplete here -->
                  
                  <v-autocomplete
              v-model="selectedClients"
              :disabled="isUpdating"
              :items="listOfDuplicateClients"
              filled
              chips
              color="blue-grey lighten-2"
              label="Possible Duplicates"
              item-text="quick_details"
              return-object
              multiple
            >
              <template v-slot:selection="data">
                <v-chip
                color="red lighten-3"
                  v-bind="data.attrs"
                  :input-value="data.selected"
                  close
                  @click="data.select"
                  @click:close="remove(data.item)"
                >
                  {{ data.item.quick_details }}
                </v-chip>
              </template>
              <template v-slot:item="data">
                <template v-if="typeof data.item !== 'object'">
                  <v-list-item-content v-text="data.item"></v-list-item-content>
                </template>
                <template v-else>
                 
                  <v-list-item-content>
                    <v-list-item-title v-text="data.item.quick_details"></v-list-item-title>
                    <!-- <v-list-item-subtitle v-html="data.item.group"></v-list-item-subtitle> -->
                  </v-list-item-content>
                </template>
              </template>
            </v-autocomplete>
                  <v-autocomplete
              v-model="selectedClients"
              :disabled="isUpdating"
              :items="clientList"
              filled
              chips
              color="blue-grey lighten-2"
              label="Clients to Remove"
              item-text="quick_details"
              return-object
              multiple
            >
              <template v-slot:selection="data">
                <v-chip
                color="red lighten-3"
                  v-bind="data.attrs"
                  :input-value="data.selected"
                  close
                  @click="data.select"
                  @click:close="remove(data.item)"
                >
                  {{ data.item.quick_details }}
                </v-chip>
              </template>
              <template v-slot:item="data">
                <template v-if="typeof data.item !== 'object'">
                  <v-list-item-content v-text="data.item"></v-list-item-content>
                </template>
                <template v-else>
                 
                  <v-list-item-content>
                    <v-list-item-title v-text="data.item.quick_details"></v-list-item-title>
                    <!-- <v-list-item-subtitle v-html="data.item.group"></v-list-item-subtitle> -->
                  </v-list-item-content>
                </template>
              </template>
            </v-autocomplete>
            <!-- Client to Keep starts here -->
            <v-autocomplete
              v-model="clientToMergeInto"
              :disabled="isUpdating"
              :items="clientList"
              filled
              chips
              color="blue-grey lighten-2"
              label="Clients to Keep"
              item-text="quick_details"
              return-object
              
            >
              <template v-slot:selection="data">
                <v-chip
                color="green lighten-3"
                  v-bind="data.attrs"
                  :input-value="data.selected"
                  close
                  @click="data.select"
                  @click:close="removeClientToKeep()"
                >
                  {{ data.item.quick_details }}
                </v-chip>
              </template>
              <template v-slot:item="data">
                <template v-if="typeof data.item !== 'object'">
                  <v-list-item-content v-text="data.item"></v-list-item-content>
                </template>
                <template v-else>
                 
                  <v-list-item-content>
                    <v-list-item-title v-text="data.item.quick_details"></v-list-item-title>
                    <!-- <v-list-item-subtitle v-html="data.item.group"></v-list-item-subtitle> -->
                  </v-list-item-content>
                </template>
              </template>
            </v-autocomplete>
                </v-col>
              </v-row>
            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="closeMergeAccountsDialog"> Cancel </v-btn>
            <v-btn
              color="blue darken-1"
              text
              @click="confirmMerge"
            >
              Start Merge</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-dialog>
      <!-- Carousel forms starts here -->
      <v-dialog v-model="profileChoiceDialog" max-width="500px">
              <v-card justify="center">
                <v-card-title>
                  <span class="text-h5">Store Client Info</span>
                </v-card-title>
                <v-carousel hide-delimiters height="900"><v-carousel-item 
                  v-for="(item,i) in listOfClientForms" :key="i">
                  <v-row justify="center" >
                    <v-col cols="6">
                      <v-form
                        ref="clientForm"
                        height="200"
                        width="500"
                        v-model="valid"
                        lazy-validation
                        class="class-form mx-auto white"
                        @submit.prevent="keepClientInfo(item)"
                        justify="center"
                        align="center"
                      >
                       <v-text-field
                        v-model="item.first_name"        
                        :counter="20"
                        :rules="nameRules"
                        label="First Name"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="item.last_name"
                        :counter="20"
                        :rules="nameRules"
                        label="Last Name"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="item.street_address"
                        :counter="30"
                        :rules="addressRules"
                        label="Street Address"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="item.city"
                        :counter="10"
                        :rules="nameRules"
                        label="City"
                        required
                      ></v-text-field>

                      <v-select
                        v-model="item.state_abbreviation"
                        :items="states"
                        :rules="[(v) => !!v || 'Item is required']"
                        label="State"
                        required
                      ></v-select>

                      <v-text-field
                        v-model="item.zip_code"
                        :counter="10"
                        :rules="nameRules"
                        label="ZIP"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="item.phone_number"
                        :counter="15"
                        :rules="nameRules"
                        label="Phone Number"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-model="item.email"
                        :rules="emailRules"
                        label="E-mail"
                        required
                      ></v-text-field>
                      <v-checkbox
                        v-model="item.is_new_client"
                        label="Is New Client?"
                        required
                      ></v-checkbox>
                      <v-checkbox
                        v-model="item.has_record_of_liability"
                        label="Record of Liability"
                        required
                      ></v-checkbox>
                      <v-checkbox
                        v-model="item.is_client_active"
                        label="Is Client Active?"
                        required
                      ></v-checkbox>

                      <v-checkbox
                        v-model="item.is_on_email_list"
                        label="Stay on Email List?"
                        required
                      ></v-checkbox>
                      
                        <v-row justify="center" align="center"
                          ><v-col>
                            <v-btn
                            color="green lighten-3"
                              class="mr-4"
                              type="submit"
                              :disabled="invalid"
                            >
                              Keep
                            </v-btn></v-col
                          >
                          </v-row
                        >
                      </v-form>
                    </v-col>
                  </v-row>
                
              </v-carousel-item></v-carousel>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="closeProfileChoice">
                    Cancel
                  </v-btn>
                  <!-- <v-btn color="blue darken-1" text @click="save"> Save </v-btn> -->
                </v-card-actions>
              </v-card>
            </v-dialog>
      <v-spacer></v-spacer>
        <!-- Add a new client -->
        <v-dialog v-model="newClientDialog" max-width="500px">
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark fab class="mx-2" v-bind="attrs" v-on="on">
                <v-icon large>mdi-new-box</v-icon>
              </v-btn>
            </template>
            <v-card>
              <!-- Add a Client Form starts here -->
              <v-card-title>
                <span class="text-h5">Add Client To Roster</span>
    
              </v-card-title>

              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col>
                      
                      <v-text-field
                       
                        v-model="newClientDetails.first_name"
                        :counter="20"
                        :rules="nameRules"
                        label="First Name"
                        required
                      ></v-text-field>

                      <v-text-field
                       
                        v-model="newClientDetails.last_name"
                        :counter="20"
                        :rules="nameRules"
                        label="Last Name"
                        required
                      ></v-text-field>
                      <v-text-field
                       
                        v-model="newClientDetails.email"
                        :counter="30"
                
                        label="Email (Optional)"
                        
                      ></v-text-field>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                  <v-btn color="blue darken-1" text @click="saveNewClientRegistration" > Save New Client</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
          <v-spacer></v-spacer>
      <v-dialog v-model="dialog2" max-width="500px">
        <template v-slot:activator="{ on, attrs }">
          <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
            Add to Shared Group
          </v-btn>
        </template>
        <v-card>
          <!-- Add a Client Form starts here -->
          <v-card-title>
            <span class="text-h5">Add Client To Shared Group</span>
            <v-spacer></v-spacer>
            <v-btn
              class="mx-2"
              fab
              dark
              color="primary"
              @click="showNewClientForm = !showNewClientForm"
              v-if="!showNewClientForm"
              ><v-icon large>mdi-new-box</v-icon></v-btn
            >
            <v-btn
              class="mx-2"
              fab
              dark
              color="primary"
              @click="showNewClientForm = !showNewClientForm"
              v-if="showNewClientForm"
              ><v-icon large>mdi-account-multiple-plus</v-icon></v-btn
            >
          </v-card-title>

          <v-card-text>
            <v-container>
              <v-row>
                <v-col>
                  <v-select
                    v-if="!showNewClientForm"
                    label="Choose one or multiple"
                    :items="familyList"
                    v-model="selectedFamily"
                    item-text="quick_details"
                    return-object
                  ></v-select>
                  <v-text-field
                    v-if="showNewClientForm"
                    v-model="newFamily"
                    :counter="30"
                    :rules="nameRules"
                    label="New Family Name"
                    required
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-container>
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="close1"> Cancel </v-btn>
            <v-btn
              color="blue darken-1"
              text
              @click="save"
              v-if="!showNewClientForm"
            >
              Add to selected family</v-btn
            >
            <v-btn
              color="blue darken-1"
              text
              @click="saveNewClient"
              v-if="showNewClientForm"
            >
              Create New Family</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="clientList"
      item-key="client_id"
      v-model="selectedClients"
      :options.sync="options"
      :server-items-length="totalClients"
      :loading="loading"
      loading-text="Loading... Please wait"
      :search="search"
      show-select
      dense
      class="elevation-5"
      hide-default-footer
    >
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
          <v-dialog v-model="dialogDelete" max-width="600px">
            <v-card>
              <v-card-title class="text-h5"
                >Are you sure you want to delete this client?</v-card-title
              >
              <v-card-title class="text-h6" style="color: red"
                >This will delete all traces</v-card-title
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
        <v-icon
          small
          class="mr-2"
          @click.prevent="sendToUserPageAdminView(item)"
        >
          mdi-account-search
        </v-icon>
        <v-icon small class="mr-2" @click="editItem(item)"> mdi-pencil </v-icon>
        <v-icon small @click="deleteItem(item)"> mdi-delete </v-icon>
      </template>
      <template v-slot:no-data>
        <v-btn color="primary" @click="initialize"> Reset </v-btn>
      </template>
    </v-data-table>
    <v-row>
      <v-col cols="11">
        <v-pagination
          v-model="page"
          :length="Math.ceil(totalClients / pageSize)"
          @input="temporaryPageMethod"
          total-visible="8"
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
    <v-overlay :value="overlay">
      <v-progress-circular
        indeterminate
        size="70"
      ></v-progress-circular>
    </v-overlay>
  </v-card>
</template>

<script>
import clientDetailService from "../services/ClientDetailService";
import familyService from "../services/FamilyService";

export default {
  data() {
    return {
      search: "",
      page: 1,
      pageSize: 20,
      headers: [
        {text:"ID",
        sortable: true,
      value:"client_id"},
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
        loading: true,
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
      // add family
      selectedClients: [],
      showNewClientForm: false,
      dialog2: false,
      mergeDialog: false,
      familyList: [],
      newFamily: "",
      selectedFamily: {},
      emailLink: "https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=",
      isUpdating: false,
      autoUpdate: true,
      clientToMergeInto: {},
      listOfDuplicateClients: [],
      newClientDetails: {
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
        loading: true,
        date_of_entry: "",
        user_id: 0,
      },
      listOfClientForms: [],
      profileChoiceDialog: false,
      overlay: false,
      newClientDialog: false,
    };
  },
  created() {
    this.getClientTable();
    this.getFamilyList();
  },
  watch: {
      isUpdating (val) {
        if (val) {
          setTimeout(() => (this.isUpdating = false), 3000)
        }
      },
    },
  methods: {
    keepClientInfo(item) {
      this.overlay = !this.overlay;
            this.clientToMergeInto.email = item.email
            this.clientToMergeInto.phone_number = item.phone_number
            this.clientToMergeInto.street_address = item.street_address
            this.clientToMergeInto.state_abbreviation = item.state_abbreviation
            this.clientToMergeInto.city = item.city
            this.clientToMergeInto.zip_code = item.zip_code
            this.clientToMergeInto.is_new_client = item.is_new_client
            this.clientToMergeInto.has_record_of_liability = item.has_record_of_liability
            this.clientToMergeInto.is_client_active = item.is_client_active
            this.clientToMergeInto.is_on_email_list = item.is_on_email_list

            // TODO: Call method from server here immediately
        // Method that takes in a list of selected Clients 
        //and adds the clientToMerge at the zero index
        this.selectedClients.unshift(this.clientToMergeInto);

        clientDetailService.mergeClients(this.selectedClients)
        .then((response) => {
          if (response.status === 200) {
            this.overlay = false;
            this.closeMergeAccountsDialog();
            this.profileChoiceDialog = false;
            this.getClientTable();
          }
        })
        .catch((error) => {
              const response = error.response;
              if (response.status === 400) {
                alert("Email already in use, or different error.")
              }
            });
    },
    confirmMerge(){
      
      if (Object.keys(this.clientToMergeInto).length == 0 || this.selectedClients.length == 0) {
        alert("Please Choose Clients to Remove and Keep")
      } else {
        this.listOfClientForms = [];
        
        this.listOfClientForms.push(this.clientToMergeInto)
        
        
        for (let index = 0; index < this.selectedClients.length; index++) {
          
          if (this.selectedClients[index].email  ||
            this.selectedClients[index].phone_number  |
            this.selectedClients[index].street_address  ||
            this.selectedClients[index].state_abbreviation  ||
            this.selectedClients[index].city  || 
            this.selectedClients[index].zip_code  ) {
              this.selectedClients[index].first_name = this.clientToMergeInto.first_name
              this.selectedClients[index].last_name = this.clientToMergeInto.last_name;
              this.listOfClientForms.push(this.selectedClients[index])
            }
        }
        
        this.mergeDialog = false;
        this.profileChoiceDialog = true;
        
      
    }

    },
    
    closeProfileChoice() {
      this.profileChoiceDialog = false;
      this.listOfClientForms = [];
      
    },
    remove (item) {
      let itemIndex = 0;
      for (let index = 0; index < this.selectedClients.length; index++) {
        if (this.selectedClients[index.client_id] == item.client_id) {
          itemIndex = index;
        }
        
      }
        if (itemIndex >= 0) this.selectedClients.splice(itemIndex, 1)
      },
      removeClientToKeep () {
      this.clientToMergeInto = [];
      },
    sendToUserPageAdminView(object) {
      this.$store.commit("SET_CLIENT_DETAILS", object);
      this.$router.push({
        name: "client-details-admin-view",
        params: { clientId: object.client_id },
      });
    },
    emailRecipients() {
      if (this.selectedClients.length > 0) {
      this.selectedClients.forEach((item) => {
        this.emailLink = this.emailLink + item.email + ";";
      });
      // window.location.href = this.emailLink;
      window.open(this.emailLink, '_blank').focus();
      this.emailLink = "https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=";
      } else {
        alert("Please select at least one user to email")
      }
    },
    emailRecipientsFromEmailList() {
      this.clientList.forEach((item) => {
        if (item.is_on_email_list) {
          this.emailLink = this.emailLink + item.email + ";";
        }
      });
      // window.location.href = this.emailLink;
      window.open(this.emailLink, '_blank').focus();
      this.emailLink = "https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=";
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
    getSearchedClientTablePaginated() {
      this.loading = true;
      this.page = 1;
      clientDetailService
        .getPaginatedClients(this.page, this.pageSize, this.search)
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
        });
    },
    getClientTable() {
      this.loading = true;
      this.overlay = !this.overlay;
      clientDetailService .getPaginatedClients(this.page, this.pageSize).then((response) => {
        if (response.status == 200) {
          this.loading = false;
          this.overlay = false;
          this.paginatedObject = response.data;
            this.clientList = this.paginatedObject.listOfClients;
            this.totalClients = this.paginatedObject.totalRows;
           this.clientList.forEach((item) => {
             if (item.full_address.includes("null")) {
               item.full_address = item.full_address.replaceAll("null", " ")
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
            this.$router.push('/login')
          }
        });
      
    },
    temporaryPageMethod() {
      
      this.getPaginatedClientTable();
    },
    temporaryPageSizeMethod() {
      this.page = 1;
      this.getPaginatedClientTable();
    },
    retrieveDuplicateClients() {
      this.loading = true;
      this.overlay = !this.overlay;
      clientDetailService.getDuplicateClients().then((response) => {
        if (response.status == 200) {
          this.loading = false;
          this.overlay = false;
          this.listOfDuplicateClients = response.data;
        }
      })
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
    resetByIndex(index) {
      this.$refs.clientForm.reset();
      alert(index)
      this.listOfClientForms[index] = {}
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

    //family stuff
    getFamilyList() {
      familyService.getFamilyList().then((response) => {
        if (response.status == 200) {
          this.familyList = response.data;
        }
      });
    },
    save() {

      if(this.selectedClients.length == 0){
        alert("You need to select at least one client.");
        this.close1();
      }else{
      // check for length > 0 here if bug
      for (let index = 0; index < this.selectedClients.length; index++) {
        // this.allowSignUp = false;

        this.selectedClients[index].family_id = this.selectedFamily.family_id;

        // this.individualClientFromLoop = this.selectedClients[index];

        // END OF LOOP BLOCK
      }

      familyService
        .addMultipleClientsForFamily(this.selectedClients)
        .then((response) => {
          if (response.status == 201) {
            alert("Successfully added clients to selected family");
            this.getClientTable();
            // this.getEventDetailsCall();
            this.selectedClients = [];
            this.selectedFamily = {};
          } else {
            alert("Error adding clients to roster");
          }
        });

      this.close1();
      }
    },

    saveNewClient() {
      for (let index = 0; index < this.selectedClients.length; index++) {
        this.selectedClients[index].family_name = this.newFamily;
      }
      familyService
        .addMultipleClientsToNewFamily(this.selectedClients)
        .then((response) => {
          if (response.status == 201) {
            alert("Successfully added clients to selected family");
            this.getFamilyList();
            // this.getEventDetailsCall();
            this.selectedClients = [];
            this.newFamily = "";
          } else {
            alert("Error adding clients to roster");
          }
        });

      this.close1();
    },
    saveNewClientRegistration() {
    
      if (this.newClientDetails.first_name != "" && this.newClientDetails.last_name != "") {

      
      clientDetailService
        .registerNewClient(this.newClientDetails)
        .then((response) => {
          if (response.status == 200) {
            alert("Successfully added client");
            this.getClientTable();
          } else {
            alert("Error adding clients to roster");
          }
        });

      this.newClientDialog = false;
      } else {
        alert("Please Fill Out Both First and Last Name")
      }
    },
    close1() {
      this.dialog2 = false;
      this.newFamily = "";
      this.selectedFamily = {};
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },
    closeMergeAccountsDialog() {
      this.mergeDialog = false;
      this.selectedClients = [];
      this.listOfClientForms = [];
      // this.$nextTick(() => {
      //   this.editedItem = Object.assign({}, this.defaultItem);
      //   this.editedIndex = -1;
      // });
    },
  },
};
</script>

<style>
</style>