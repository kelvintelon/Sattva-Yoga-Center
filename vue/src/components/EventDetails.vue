<template>
  <v-container>
    <v-data-table
      :headers="headers"
      :items="returnListOfSignedUpClients"
      sort-by="last_name"
      class="elevation-1"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Attendance</v-toolbar-title>
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
          <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
            Edit Event
          </v-btn>
          <v-spacer></v-spacer>
          <v-dialog v-model="dialog" max-width="500px">
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
                Sign up a client
              </v-btn>
            </template>
            <v-card>
              <!-- Add a Client Form starts here -->
              <v-card-title>
                <span class="text-h5">Add a client</span>
              </v-card-title>

              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col>
                      <v-select
                        label="Choose one or multiple"
                        :items="returnCorrectClientListToChoose"
                        v-model="selectedClients"
                        item-text="quick_details"
                        return-object
              
                        multiple
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <v-btn color="blue darken-1" text @click="save"> Save </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
          <v-dialog v-model="dialogDelete" max-width="500px">
            <v-card>
              <v-card-title class="text-h5"
                >Are you sure you want to delete this item?</v-card-title
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
      <!-- <template v-slot:item.actions="{ item }">
      <v-icon
        small
        class="mr-2"
        @click="editItem(item)"
      >
        mdi-pencil
      </v-icon>
      <v-icon
        small
        @click="deleteItem(item)"
      >
        mdi-delete
      </v-icon>
    </template>
    <template v-slot:no-data>
      <v-btn
        color="primary"
        @click="initialize"
      >
        Reset
      </v-btn>
    </template> -->
    </v-data-table>
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
      dialog: false,
      // Edit event properties
      allTimes: [
        "12:00 AM",
        "12:15 AM",
        "12:30 AM",
        "12:45 AM",
        "1:00 AM",
        "1:15 AM",
        "1:30 AM",
        "1:45 AM",
        "2:00 AM",
        "2:15 AM",
        "2:30 AM",
        "2:45 AM",
        "3:00 AM",
        "3:15 AM",
        "3:30 AM",
        "3:45 AM",
        "4:00 AM",
        "4:15 AM",
        "4:30 AM",
        "4:45 AM",
        "5:00 AM",
        "5:15 AM",
        "5:30 AM",
        "5:45 AM",
        "6:00 AM",
        "6:15 AM",
        "6:30 AM",
        "6:45 AM",
        "7:00 AM",
        "7:15 AM",
        "7:30 AM",
        "7:45 AM",
        "8:00 AM",
        "8:15 AM",
        "8:30 AM",
        "8:45 AM",
        "9:00 AM",
        "9:15 AM",
        "9:30 AM",
        "9:45 AM",
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
        "1:00 PM",
        "1:15 PM",
        "1:30 PM",
        "1:45 PM",
        "2:00 PM",
        "2:15 PM",
        "2:30 PM",
        "2:45 PM",
        "3:00 PM",
        "3:15 PM",
        "3:30 PM",
        "3:45 PM",
        "4:00 PM",
        "4:15 PM",
        "4:30 PM",
        "4:45 PM",
        "5:00 PM",
        "5:15 PM",
        "5:30 PM",
        "5:45 PM",
        "6:00 PM",
        "6:15 PM",
        "6:30 PM",
        "6:45 PM",
        "7:00 PM",
        "7:15 PM",
        "7:30 PM",
        "7:45 PM",
        "8:00 PM",
        "8:15 PM",
        "8:30 PM",
        "8:45 PM",
        "9:00 PM",
        "9:15 PM",
        "9:30 PM",
        "9:45 PM",
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
        {text: "Client ID",
        value: "client_id"},
        {
          text: "First Name",
          align: "start",
          sortable: false,
          value: "first_name",
        },
        { text: "Last Name", value: "last_name" },
        { text: "Phone Number", value: "phone_number", sortable: false },
        { text: "Email List", value: "is_on_email_list", sortable: true },
        { text: "Email", value: "email", sortable: false },
        { text: "Actions", value: "actions", sortable: false },
      ],
      editedIndex: -1,
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
    showCardEditForm() {
      this.showEditForm = !this.showEditForm;
    },
    // Attendance table methods
    editItem(item) {
      this.editedIndex = this.desserts.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialog = true;
    },

    deleteItem(item) {
      this.editedIndex = this.desserts.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialogDelete = true;
    },

    deleteItemConfirm() {
      this.desserts.splice(this.editedIndex, 1);
      this.closeDelete();
    },

    close() {
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

    save() {

      
      for (let index = 0; index < this.selectedClients.length; index++) {
        // first check if the clients have an active package
        packagePurchaseService.getUserPurchasedPackages(this.selectedClients[index].user_id).then((response) => {
        if (response.status == 200) {
          // focus on if it's expired or not

          this.packages = response.data.filter((item) => {
            return item.is_expired == false;
          });
          this.packages.forEach((item) => {
            item.date_purchased = new Date(item.date_purchased);
          });
          this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
          this.activePackageList = this.$store.state.activePackageList;
          this.formattingSignUp();
        } else {
          alert("Error retrieving package information");
        }
      });
        
      }

      // TODO: Change the following line
      // empty the selected Array,
      // add them to the listofSignedUpclients
      // call the API to add them to the event in the DB


    
      this.close();
    },
    formattingSignUp() {
      // find out if they have at least one active package that's a subscription or a bundle and active
      // this.activePackageList = this.$store.state.activePackageList;
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
          }
        });
        // this here is if they only have a gift certificate or dont have a bundle/subscription
        if (!this.allowSignUp) {
          this.snackBarNoPurchaseWarning = true;
        }
      }

      // if they have an active package then they are allowed to sign up
      if (this.allowSignUp) {
        this.clientEvents.forEach((item) => {
          if (item.event_id == this.eventClientSignUp.event_id) {
            alert("You have already signed up for this class!");
            this.validSignUp = false;
          }
        });
        if (this.validSignUp == true) {
          eventService
            .registerForEvent(this.eventClientSignUp)
            .then((response) => {
              if (response.status == 201) {
                // call method that updates the client_class_table
                // update client.is_new_client to false through mutation
                alert("You have registered for a class");
                this.$store.commit("SET_CLIENT_DETAILS_NEW_CLIENT", false);
                this.getClientEventTable();
              }
            });
        }
      }
    },
    getAllClients() {
      clientDetailService.getClientList().then((response) => {
        if (response.status == 200) {
          this.allClientsList = response.data;

          this.$store.commit("SET_CLIENT_LIST", response.data);

          

        
          // TODO: Change the following line to filter through 
          //the ones where client_id already match
          // this.allClientsList.filter((item) => 
          //   
          //   !this.listOfSignedUpClients.includes((item))
          // )
          
          
        } else {
          alert("Error retrieving client information");
        }
      });
    },
  },
  created() {
    // TODO: Change the following so you can redirect users who stumbled onto this page right here in this moment

    eventService
      .getEventDetailsByEventId(this.$route.params.eventId)
      .then((response) => {
        if (response.status == 200) {
          this.event = response.data;
          this.editedEvent = response.data;
          this.formatsIncomingEvent();
          this.listOfSignedUpClients = this.event.attendanceList;
          // formats the date to be readable
          this.getAllClients()
          this.listOfSignedUpClients.forEach((item) => {
            item.date_of_entry = new Date(item.date_of_entry);
          })
          ;
          
        }
      });
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
      let finalizedList = [];
      for (let index = 0; index < this.allClientsList.length; index++) {
            let foundMatch = false;
            this.listOfSignedUpClients.forEach((element) => {
              
              if (element.client_id == this.allClientsList[index].client_id) {
                foundMatch = true;
              } 
            }); 
            if (foundMatch == false) {
              finalizedList.push(this.allClientsList[index])
            }
          }
          return finalizedList;
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