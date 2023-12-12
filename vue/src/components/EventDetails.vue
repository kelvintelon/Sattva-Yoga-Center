<template>
  <v-container>
    <v-snackbar
      v-model="snackBarNoPurchaseWarning"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      :timeout="timeout"
      shaped
    >
      Roster Warning: Clients Have Been Red Flagged
    </v-snackbar>
    <v-snackbar
      v-model="snackBarDeleteClientConfirmation"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
    >
      Warning: Delete Red Flagged Client?
      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarDeleteClientConfirmation = false"
          left
        >
          Close
        </v-btn>
        <v-btn color="white" text v-bind="attrs" @click="allowClientDelete">
          Continue
        </v-btn>
      </template>
    </v-snackbar>
    <v-data-table
      v-model="selectedClientsFromRoster"
      :headers="headers"
      :items="returnListOfSignedUpClients"
      item-key="client_id"
      sort-by="last_name"
      show-select
      class="elevation-1"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-btn
            color="primary"
            dark
            class="mb-2"
            v-bind="attrs"
            v-on="on"
            @click.prevent="sendToCalendarPage"
          >
            <v-icon> mdi-keyboard-return</v-icon>
          </v-btn>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-toolbar-title>{{ event.event_name }}</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <span
            >{{ new Date(event.start_time).getFullYear() }}-{{
              new Date(event.start_time).getMonth() + 1
            }}-{{ new Date(event.start_time).getDate() }}</span
          >

          <v-divider class="mx-4" inset vertical></v-divider>
          <span>
            {{
              new Date(event.start_time).toLocaleString("en-US", {
                hour: "numeric",
                minute: "numeric",
                hour12: true,
              })
            }}-
          </span>

          <span>
            {{
              new Date(event.end_time).toLocaleString("en-US", {
                hour: "numeric",
                minute: "numeric",
                hour12: true,
              })
            }}</span
          >
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-btn
            color="primary"
            dark
            class="mb-2 mr-3"
            v-bind="attrs"
            v-on="on"
            @click.prevent="emailRecipients"
            title="Email Selected Client(s)"
          >
            <v-icon>mdi-order-bool-ascending-variant</v-icon>
            <v-icon>mdi-email</v-icon>
          </v-btn>
          
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
          <v-divider class="mx-4" inset vertical></v-divider>
          Count: {{ listOfSignedUpClients.length }}
          <v-spacer></v-spacer>
          <v-dialog v-model="dialog" max-width="500px" persistent>
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
                Client Sign Up
              </v-btn>
            </template>
            <v-card>
              <!-- Add a Client Form starts here -->
              <v-card-title>
                <span class="text-h5">Add Client To Roster</span>
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
                      <v-autocomplete
                      v-if="!showNewClientForm"
                    v-model="selectedClients"
                    :disabled="isUpdating"
                    :items="returnCorrectClientListToChoose"
                    :loading="loadingFirstClientList"
                    filled
                    color="blue-grey lighten-2"
                    label="Choose one or more"
                    item-text="quick_details"
                    return-object
                    ref="autocompleteEventDetails"
                    cache-items
                    multiple
                    @keypress="getSearchedFirstClientTableForAutocomplete"         
                  >
                  <template v-slot:append-item>
                      <div v-intersect="endFirstIntersect" />
                    </template>
                  </v-autocomplete>
                      <v-text-field
                        v-if="showNewClientForm"
                        v-model="clientDetails.first_name"
                        :counter="20"
                        :rules="nameRules"
                        label="First Name"
                        required
                      ></v-text-field>

                      <v-text-field
                        v-if="showNewClientForm"
                        v-model="clientDetails.last_name"
                        :counter="20"
                        :rules="nameRules"
                        label="Last Name"
                        required
                      ></v-text-field>
                      <v-text-field
                        v-if="showNewClientForm"
                        v-model="clientDetails.email"
                        :counter="30"
                        
                        label="Email (Optional)"
                     
                      ></v-text-field>
                      <v-checkbox v-if="showNewClientForm" v-model="clientDetails.is_on_email_list" label="Join Email List? (Optional)"></v-checkbox>
                  <v-checkbox v-if="showNewClientForm" v-model="clientDetails.is_allowed_video" label="Allowed Video? (Optional)"></v-checkbox>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <v-btn color="blue darken-1" text @click="save" v-if="!showNewClientForm"> Save Client(s)</v-btn>
                  <v-btn color="blue darken-1" text @click="saveNewClient" v-if="showNewClientForm"> Save New Client</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
          <!-- For Deleting a client from the roster -->
          <v-dialog v-model="dialogDelete" max-width="500px">
            <v-card>
              <v-card-title class="text-h5"
                >Are you sure you want to remove this client?</v-card-title
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
      <template v-slot:[`item.client_id`]="{ item }">
        <v-chip :color="getColor(item)" dark>
          {{ item.client_id }}
        </v-chip>
      </template>
      <template v-slot:[`item.first_name`]="{ item }">
        <v-chip :color="getColor(item)" dark>
          {{ item.first_name }}
        </v-chip>
      </template>
      <template v-slot:[`item.last_name`]="{ item }">
        <v-chip :color="getColor(item)" dark>
          {{ item.last_name }}
        </v-chip>
      </template>
      <template v-slot:[`item.is_on_email_list`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_on_email_list"
          disabled
        ></v-simple-checkbox>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon
          large
          class="mr-2"
          @click.prevent="sendToUserPageAdminView(item)"
        >
          mdi-account-search
        </v-icon>
        <v-icon large @click.prevent="RemoveClassForClient(item)" color="#933">
          mdi-close-thick
        </v-icon>
      </template>
    </v-data-table>
    <v-overlay :value="overlay">
      <v-progress-circular
        indeterminate
        size="70"
      ></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script>
import eventService from "../services/EventService";
import clientDetailService from "../services/ClientDetailService";
import packagePurchaseService from "../services/PackagePurchaseService";

export default {
  name: "event-details",

  data() {
    return {
      dialogDelete: false,
      // Add a client properties
      allClientsList: [],
      selectedClients: [],
      individualClientFromLoop: {},
      allowSignUp: false,
      validSignUp: true,
      snackBarNoPurchaseWarning: false,
      snackBarDeleteClientConfirmation: false,
      timeout: 6000,
      hasSubscriptionPackage: false,
      subscriptionPackages: [],
      subscriptionPackageId: 0,
      quantityPackages: [],
      quantityPackageIdToDecrement: 0,
      eventClientSignUp: {
        event_id: "",
        client_id: "",
      },
      dialog: false,
      packages: [],
      // Edit event properties
      allTimes: [
      "12:00 AM",
      "12:15 AM",
      "12:30 AM",
      "12:45 AM",
      "01:00 AM",
      "01:15 AM",
      "01:30 AM",
      "01:45 AM",
      "02:00 AM",
      "02:15 AM",
      "02:30 AM",
      "02:45 AM",
      "03:00 AM",
      "03:15 AM",
      "03:30 AM",
      "03:45 AM",
      "04:00 AM",
      "04:15 AM",
      "04:30 AM",
      "04:45 AM",
      "05:00 AM",
      "05:15 AM",
      "05:30 AM",
      "05:45 AM",
      "06:00 AM",
      "06:15 AM",
      "06:30 AM",
      "06:45 AM",
      "07:00 AM",
      "07:15 AM",
      "07:30 AM",
      "07:45 AM",
      "08:00 AM",
      "08:15 AM",
      "08:30 AM",
      "08:45 AM",
      "09:00 AM",
      "09:15 AM",
      "09:30 AM",
      "09:45 AM",
      "10:00 AM",
      "10:15 AM",
      "10:30 AM",
      "10:45 AM",
      "11:00 AM",
      "11:15 AM",
      "11:30 AM",
      "11:45 AM",
      "12:00 PM",
      "12:15 PM",
      "12:30 PM",
      "12:45 PM",
      "01:00 PM",
      "01:15 PM",
      "01:30 PM",
      "01:45 PM",
      "02:00 PM",
      "02:15 PM",
      "02:30 PM",
      "02:45 PM",
      "03:00 PM",
      "03:15 PM",
      "03:30 PM",
      "03:45 PM",
      "04:00 PM",
      "04:15 PM",
      "04:30 PM",
      "04:45 PM",
      "05:00 PM",
      "05:15 PM",
      "05:30 PM",
      "05:45 PM",
      "06:00 PM",
      "06:15 PM",
      "06:30 PM",
      "06:45 PM",
      "07:00 PM",
      "07:15 PM",
      "07:30 PM",
      "07:45 PM",
      "08:00 PM",
      "08:15 PM",
      "08:30 PM",
      "08:45 PM",
      "09:00 PM",
      "09:15 PM",
      "09:30 PM",
      "09:45 PM",
      "10:00 PM",
      "10:15 PM",
      "10:30 PM",
      "10:45 PM",
      "11:00 PM",
      "11:15 PM",
      "11:30 PM",
    ],
      editedEvent: {},
      editMenu: false,
      titleRules: [
        (v) => !!v || "Title is required",
        (v) => (v && v.length <= 30) || "Title must be less than 30 characters",
      ],
      timeRules: [(v) => !!v || "Time is required"],
      colorRules: [(v) => !!v || "Color is required"],
      showEditForm: false,
      colorPickerMenu: false,
      event: {},
      date: "",
      // Attendance table properties
      listOfSignedUpClients: [],
      headers: [
        { text: "Client ID", align: "start", value: "client_id" },
        {
          text: "First Name",
          value: "first_name",
        },
        { text: "Last Name", value: "last_name" },
        { text: "Phone Number", value: "phone_number", sortable: false },
        { text: "Email List", value: "is_on_email_list", sortable: true },
        { text: "Email", value: "email", sortable: false },
        { text: "Actions", value: "actions", sortable: false },
      ],
      editedIndex: -1,
      // email properties
      selectedClientsFromRoster: [],
      emailLink: "https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=",
      // Delete client from roster properties
      indexOfClientToBeDeleted: 0,
      allHistoricalPackages: [],
      // Add a new client
      showNewClientForm: false,
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
        is_allowed_video: false,
        email: "",
        has_record_of_liability: false,
        date_of_entry: "",
      },
      nameRules: [
        (v) => !!v || "Name is required",
        (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
      ],
      overlay:false,
      paginatedObject: {},
      autocompleteFirstClientList: [],
      loadingFirstClientList: false,
      firstAutocompletePage: 1,
      pageSize: 20,
      firstAutocompleteSearch: "",
    };
  },
  methods: {
    formatsIncomingEvent() {
      this.editedEvent = Object.assign({}, this.event);

      // finds YYYY-MM-DD format
      const [selectedMonth, selectedDay, selectedYear] = new Date(
        this.editedEvent.start_time
      )
        .toLocaleDateString()
        .split("/");
      this.date = selectedYear + "-" + selectedMonth + "-" + selectedDay;

      // converts Date Object to 12:00 AM/PM string
      this.editedEvent.start_time = new Date(
        this.editedEvent.start_time
      ).toLocaleString("en-US", {
        hour: "numeric",
        minute: "numeric",
        hour12: true,
      });

      this.editedEvent.end_time = new Date(
        this.editedEvent.end_time
      ).toLocaleString("en-US", {
        hour: "numeric",
        minute: "numeric",
        hour12: true,
      });
    },
    getColor(item) {
      if (item.redFlag) {
        return "red";
      } else {
        return "blue";
      }
    },
    showCardEditForm() {
      this.showEditForm = !this.showEditForm;
    },
    // Attendance table methods
    close() {
      this.clientDetails.first_name = "";
      this.clientDetails.last_name = "";
      this.dialog = false;
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },

    closeDelete() {
      this.dialogDelete = false;
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },
    // START OF: REMOVING CLIENT FROM ROSTER
    RemoveClassForClient(client) {
      this.overlay = !this.overlay;
      let foundClientObject = false;
      client.event_id = this.$route.params.eventId;
      // see if the client object is already selected
      for (let j = 0; j < this.selectedClientsFromRoster.length; j++) {
        this.selectedClientsFromRoster[j].event_id = this.$route.params.eventId;
        if (this.selectedClientsFromRoster[j].client_id == client.client_id) {
          foundClientObject = true;
        }
      }
      // if it's not selected already then add the client object to the list of selected clients
      if (!foundClientObject) {
        this.selectedClientsFromRoster.push(client);
      }
      // create a temp array to hold the roster of clients selected, not necessary
      let temporaryList = this.selectedClientsFromRoster;
      // let foundRedFlag = false;
      // for (let index = 0; index < temporaryList.length; index++) {
      //   if (temporaryList[index].redFlag) {
      //       foundRedFlag = true;
      //   }
      // }
      // if (foundRedFlag) {
      //   this.snackBarDeleteClientConfirmation = true;
      // } else {

      // }
      eventService
        .removeEventForSelectedClients(temporaryList)
        .then((response) => {
          if (response.status === 200) {
            this.overlay = false;
            alert("Successfully deleted clients from roster");
            this.getEventDetailsCall();
            this.selectedClientsFromRoster = [];
          } else {
            alert("Error deleting clients from roster");
          }
        }); // END OF REMOVING CLIENT FROM ROSTER
    },
    allowClientDelete() {
      this.overlay = !this.overlay;
      eventService
        .removeEventForSelectedClients(this.selectedClientsFromRoster)
        .then((response) => {
          if (response.status === 200) {
            this.overlay = false;
            alert("Successfully deleted clients from roster");
            this.getEventDetailsCall();
            this.selectedClientsFromRoster = [];
          } else {
            alert("Error deleting clients from roster");
          }
        });
    },
    // START OF ADDING CLIENT TO ROSTER
    save() {
      this.overlay = !this.overlay;
      for (let index = 0; index < this.selectedClients.length; index++) {
        this.allowSignUp = false;

        this.selectedClients[index].event_id = this.$route.params.eventId;

        this.individualClientFromLoop = this.selectedClients[index];

        // END OF LOOP BLOCK
      }
      if (this.selectedClients.length > 0) {
        eventService
          .registerMultipleClientsForEvent(this.selectedClients)
          .then((response) => {
            if (response.status == 201) {
              this.overlay = false;
              this.autocompleteFirstClientList = [];
            this.individualClientFromLoop = {};
            this.firstAutocompletePage = 1;
            this.$refs.autocompleteEventDetails.cachedItems = [];
              alert("Successfully added clients to roster");
              this.getEventDetailsCall();
              this.selectedClients = [];
              this.clientDetails.first_name = "";
              this.clientDetails.last_name = "";
            } else {
              alert("Error adding clients to roster");
            }
          });
          this.close();
      } else {
        alert("Please select at least one client")
      }
    },
    saveNewClient() {
      let newClient = {
        event_id: this.$route.params.eventId,
        first_name: this.clientDetails.first_name,
        last_name: this.clientDetails.last_name,
        email: this.clientDetails.email
      }
      if (this.clientDetails.first_name != "" && this.clientDetails.last_name != "" && newClient.first_name != "" && newClient.last_name != "") {

      
      eventService
        .registerNewClientForEvent(newClient)
        .then((response) => {
          if (response.status == 201) {
            alert("Successfully added clients to roster");
            this.autocompleteFirstClientList = [];
            this.individualClientFromLoop = {};
            this.firstAutocompletePage = 1;
            this.$refs.autocompleteEventDetails.cachedItems = [];
            this.getEventDetailsCall();
            this.selectedClients = [];
            this.clientDetails.first_name = "";
            this.clientDetails.last_name = "";
            this.clientDetails.email = "";
          } else {
            alert("Error adding clients to roster");
          }
        });

      this.close();
      } else {
        alert("Please Fill Out Both First and Last Name")
      }
    },
    findActivePackage(object) {
      packagePurchaseService
        .getUserPurchasedPackagesByUserId(object.user_id)
        .then((response) => {
          if (response.status == 200) {
            // set up your event_client object here to pass through later
            // TODO: Change the following line
            // Add the package_purchase_id to the object
            this.eventClientSignUp.event_id = this.$route.params.eventId;
            this.eventClientSignUp.client_id = object.client_id;
            this.eventClientSignUp.date = object.dateRef;

            // focus on if it's expired or not
            var today = new Date();
            var dd = String(today.getDate()).padStart(2, "0");
            var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
            var yyyy = today.getFullYear();
            today = yyyy + "-" + mm + "-" + dd;

            this.packages = response.data.filter((item) => {
              return (
                item.expiration_date >= today || item.classes_remaining > 0
              );
            });

            this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);

            // format the sign up and pass along the object
            this.formattingSignUp(this.eventClientSignUp);
          } else {
            alert("Error retrieving package information");
          }
          // END OF ACTIVE PACKAGE REQUEST
        });
    },
    formattingSignUp(object) {
      this.overlay = !this.overlay;
      // find out if they have at least one active package that's a subscription or a bundle
      // and active

      if (this.$store.state.activePackageList.length == 0) {
        this.allowSignUp = false;

        this.snackBarNoPurchaseWarning = true;
      } else if (this.$store.state.activePackageList.length > 0) {
        this.$store.state.activePackageList.forEach((item) => {
          // compare todays date and make sure it's less than the expiration date
          const todaysDate = new Date();
          let expirationDate = new Date(item.expiration_date);
          expirationDate.setDate(expirationDate.getDate() + 1);

          // TODO: Handle Gift Card logic here when SQUARE is in place
          if (item.classes_remaining > 0 || todaysDate < expirationDate) {
            this.allowSignUp = true;
            if (item.unlimited) {
              this.hasSubscriptionPackage = true;
              this.subscriptionPackages =
                this.$store.state.activePackageList.filter((item) => {
                  return item.unlimited;
                });
              this.initial = this.subscriptionPackages[0];
              this.subscriptionPackages.forEach((item) => {
                if (item.expiration_date > this.initial.expiration_date) {
                  this.initial = item;
                }
              });
              object.package_purchase_id = this.initial.package_purchase_id;
              this.subscriptionPackageId = this.initial.package_purchase_id;
            } else {
              this.quantityPackages =
                this.$store.state.activePackageList.filter((item) => {
                  return (
                    item.unlimited == false &&
                    (item.expiration_date == null ||
                      todaysDate < item.expiration_date)
                  );
                });
              this.initial = this.quantityPackages[0];
              this.quantityPackages.forEach((item) => {
                if (item.date_purchased < this.initial.date_purchased) {
                  this.initial = item;
                }
                this.quantityPackageIdToDecrement =
                  this.initial.package_purchase_id;
              });
            }
          }
        });

        // this here is if they only have a gift certificate or dont have a bundle/subscription
        if (!this.allowSignUp) {
          this.snackBarNoPurchaseWarning = true;
        }
      }

      // if they have an active package then they are allowed to sign up
      if (this.allowSignUp) {
        

        if (
          this.hasSubscriptionPackage &&
          this.eventClientSignUp.date > this.initial.expiration_date
        ) {
          alert(
            "Error! Unlimited package will be expired by then for: " +
              this.individualClientFromLoop.quick_details
          );
          this.validSignUp = false;
        }

        if (this.hasSubscriptionPackage) {
          object.package_purchase_id = this.subscriptionPackageId;
        } else {
          object.package_purchase_id = this.quantityPackageIdToDecrement;
        }

        if (this.validSignUp == true) {
          eventService.registerForEvent(object).then((response) => {
            if (response.status == 201) {
              this.overlay = false;
              if (this.hasSubscriptionPackage == false) {
                packagePurchaseService.decrementByOne(
                  this.quantityPackageIdToDecrement
                );
              }
              // add them to the listofSignedUpclients
              //  this.listOfSignedUpClients.push(this.individualClientFromLoop)
              this.getEventDetailsCall();
            }
          });
        }
      }
    },
    endFirstIntersect(entries, observer, isIntersecting) {
      if (isIntersecting && this.firstAutocompleteSearch == "") {
        // alert("intersected")

       
        this.firstAutocompletePage++;

        setTimeout(
          () =>
            // alert(this.search)
            ( this.getAutoCompletedFirstClientTable()
            ),
            // this.getPaginatedClientTable(),

            // logic goes in here
          250
        );
        
      }
    },
    getSearchedFirstClientTableForAutocomplete(event){
      this.firstAutocompletePage = 1;
      var charTyped = String.fromCharCode(event.which);
       if (/[a-z\d]/i.test(charTyped)) {
       
       
        setTimeout(
          () =>
           
            ( this.setFirstAutocompleteSearch(event.target.value) 
            ),
            
          250
        );
         
      }
    },
    setFirstAutocompleteSearch(search) {
      if (search != undefined && search.length > 0) {
        this.firstAutocompleteSearch = search;
        this.firstAutocompletePage = 1;
        // alert(this.firstAutocompleteSearch)
      } else {
        this.firstAutocompleteSearch = "";
      }
      this.loadingFirstClientList = true;
      this.getAutoCompletedFirstClientTable();
    },
    getAutoCompletedFirstClientTable() {
      
      clientDetailService
        .getPaginatedClientsForEvent(parseInt(this.$route.params.eventId), this.firstAutocompletePage, this.pageSize, this.firstAutocompleteSearch)
        .then((response) => {
          if (response.status == 200) {
            this.paginatedObject = response.data;

            this.autocompleteFirstClientList = this.paginatedObject.listOfClients;
            this.loadingFirstClientList = false;
            this.autocompleteFirstClientList.forEach((item) => {
              if (item.full_address.includes("null")) {
                item.full_address = item.full_address.replaceAll("null", " ");
              }
              item.date_of_entry = new Date(item.date_of_entry);
            });
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
    getAllClients() {
      this.overlay = !this.overlay;
      this.loading =true;
      clientDetailService.getClientList().then((response) => {
        if (response.status == 200) {
          this.loading = false;
          this.overlay = false;
          this.allClientsList = response.data;

          this.$store.commit("SET_CLIENT_EVENT_LIST", response.data);
        } else {
          alert("Error retrieving client information");
        }
      });
    },
    sendToCalendarPage() {
      this.$router.push("/classManagement");
    },
    sendToUserPageAdminView(object) {
      this.$store.commit("SET_CLIENT_DETAILS", object);
      this.$router.push({
        name: "client-details-admin-view",
        params: { clientId: object.client_id },
      });
    },
    emailRecipients() {
       if (this.selectedClientsFromRoster.length > 0) {
      this.selectedClientsFromRoster.forEach((item) => {
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
      this.listOfSignedUpClients.forEach((item) => {
        if (item.is_on_email_list) {
          this.emailLink = this.emailLink + item.email + ";";
        }
      });
      // window.location.href = this.emailLink;
      window.open(this.emailLink, '_blank').focus();
      this.emailLink = "https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=";
    },
    getEventDetailsCall() {
      eventService
        .getEventDetailsByEventId(this.$route.params.eventId)
        .then((response) => {
          if (response.status == 200) {
            this.event = response.data;
            this.editedEvent = response.data;
            this.formatsIncomingEvent();
            this.listOfSignedUpClients = this.event.attendanceList;
            // formats the date to be readable
            this.getAutoCompletedFirstClientTable()
            this.listOfSignedUpClients.forEach((item) => {
              item.date_of_entry = new Date(item.date_of_entry);
              if (item.redFlag) {
                this.snackBarNoPurchaseWarning = true;
              }
            });
          }
        });
    },
  },
  created() {
    // TODO: Change the following so you can redirect users who stumbled onto this page right here in this moment

    this.getEventDetailsCall();
    this.$root.$refs.E = this;
  },
  computed: {
    returnCorrectEndTime() {
      // method used to make sure end_time list dropdown comes after the start_time list drop down
      if (this.editedEvent.start_time != "") {
        let datesToReturn = [];
        let foundMatch = false;
        for (let index = 0; index < this.allTimes.length - 1; index++) {
          if (this.event.start_time == this.allTimes[index]) {
            foundMatch = true;
          }
          if (foundMatch) {
            datesToReturn.push(this.allTimes[index + 1]);
          }
        }
        return datesToReturn;
      } else {
        return this.allTimes;
      }
    },
    returnCorrectClientListToChoose() {
      // let finalizedList = [];
      // for (let index = 0; index < this.autocompleteFirstClientList.length; index++) {
      //   let foundMatch = false;
      //   this.listOfSignedUpClients.forEach((element) => {
      //     if (element.client_id == this.autocompleteFirstClientList[index].client_id) {
      //       foundMatch = true;
      //     }
      //   });
      //   if (foundMatch == false) {
      //     finalizedList.push(this.autocompleteFirstClientList[index]);
      //   }
      // }
      // // returns a list of users excluding the ones who are already signed up
      // return finalizedList;
      return this.autocompleteFirstClientList
    },
    formTitle() {
      return this.editedIndex === -1 ? "New Item" : "Edit Item";
    },
    returnListOfSignedUpClients() {
      return this.listOfSignedUpClients;
    },
  },
  watch: {
    dialog(val) {
      val || this.close();
    },
    dialogDelete(val) {
      val || this.closeDelete();
    },
  },
};
</script>

<style>
</style>