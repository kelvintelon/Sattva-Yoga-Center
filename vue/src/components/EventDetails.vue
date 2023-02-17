<template>
  <v-container>
      
    <v-data-table
    :headers="headers"
    :items="returnListOfClients"
    sort-by="last_name"
    class="elevation-1"
  >
    <template v-slot:top>
      <v-toolbar
        flat
      >
        <v-toolbar-title>Attendance</v-toolbar-title>
        <v-divider
          class="mx-4"
          inset
          vertical
        ></v-divider>
        <span
          >{{ new Date(event.start_time).getFullYear() }}-{{
            new Date(event.start_time).getMonth() + 1
          }}-{{ new Date(event.start_time).getDate() }}</span
        >
      
        <v-divider
          class="mx-4"
          inset
          vertical
        ></v-divider>
        <span
          >
          {{
            new Date(event.start_time).toLocaleString("en-US", {
              hour: "numeric",
              minute: "numeric",
              hour12: true,
            })
          }}-
        </span>
        
        <span
          >
          {{
            new Date(event.end_time).toLocaleString("en-US", {
              hour: "numeric",
              minute: "numeric",
              hour12: true,
            })
          }}</span
        >
        <v-divider
          class="mx-4"
          inset
          vertical
        ></v-divider> 
        <v-btn
              color="primary"
              dark
              class="mb-2"
              v-bind="attrs"
              v-on="on"
            >
              Edit Event
            </v-btn>
        <v-spacer></v-spacer>
        <v-dialog
          v-model="dialog"
          max-width="500px"
        >
          <template v-slot:activator="{ on, attrs }">
            
            <v-btn
              color="primary"
              dark
              class="mb-2"
              v-bind="attrs"
              v-on="on"
            >
              Sign up a client
            </v-btn>
          
          </template>
         <!--  <v-card>
            <v-card-title>
              <span class="text-h5">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row>
                  <v-col
                    cols="12"
                    sm="6"
                    md="4"
                  >
                    <v-text-field
                      v-model="editedItem.name"
                      label="Dessert name"
                    ></v-text-field>
                  </v-col>
                  <v-col
                    cols="12"
                    sm="6"
                    md="4"
                  >
                    <v-text-field
                      v-model="editedItem.calories"
                      label="Calories"
                    ></v-text-field>
                  </v-col>
                  <v-col
                    cols="12"
                    sm="6"
                    md="4"
                  >
                    <v-text-field
                      v-model="editedItem.fat"
                      label="Fat (g)"
                    ></v-text-field>
                  </v-col>
                  <v-col
                    cols="12"
                    sm="6"
                    md="4"
                  >
                    <v-text-field
                      v-model="editedItem.carbs"
                      label="Carbs (g)"
                    ></v-text-field>
                  </v-col>
                  <v-col
                    cols="12"
                    sm="6"
                    md="4"
                  >
                    <v-text-field
                      v-model="editedItem.protein"
                      label="Protein (g)"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                color="blue darken-1"
                text
                @click="close"
              >
                Cancel
              </v-btn>
              <v-btn
                color="blue darken-1"
                text
                @click="save"
              >
                Save
              </v-btn>
            </v-card-actions>
          </v-card> -->
        </v-dialog>
        <v-dialog v-model="dialogDelete" max-width="500px">
          <v-card>
            <v-card-title class="text-h5">Are you sure you want to delete this item?</v-card-title>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="closeDelete">Cancel</v-btn>
              <v-btn color="blue darken-1" text @click="deleteItemConfirm">OK</v-btn>
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

export default {
  name: "event-details",

  data() {
    return {
      listOfClients: [],
      event: {},
      editedEvent: {},
      date: "",
      dialog: false,
      dialogDelete: false,
      editMenu: false,
      showEditForm: false,
      colorPickerMenu: false,
      selectedOpen: true,
      titleRules: [
        (v) => !!v || "Title is required",
        (v) => (v && v.length <= 30) || "Title must be less than 30 characters",
      ],
      timeRules: [(v) => !!v || "Time is required"],
      colorRules: [(v) => !!v || "Color is required"],
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
      // Attendance table properties
      headers: [
        {
          text: 'First Name',
          align: 'start',
          sortable: false,
          value: 'first_name',
        },
        { text: 'Last Name', value: 'last_name' },
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
      // headers: [
      //     {
      //       text: 'Dessert (100g serving)',
      //       align: 'start',
      //       sortable: false,
      //       value: 'name',
      //     },
      //     { text: 'Calories', value: 'calories' },
      //     { text: 'Fat (g)', value: 'fat' },
      //     { text: 'Carbs (g)', value: 'carbs' },
      //     { text: 'Protein (g)', value: 'protein' },
      //     { text: 'Iron (%)', value: 'iron' },
      //   ],
        desserts: [
          {
            name: 'Frozen Yogurt',
            calories: 159,
            fat: 6.0,
            carbs: 24,
            protein: 4.0,
            iron: 1,
          },
          {
            name: 'Ice cream sandwich',
            calories: 237,
            fat: 9.0,
            carbs: 37,
            protein: 4.3,
            iron: 1,
          },
          {
            name: 'Eclair',
            calories: 262,
            fat: 16.0,
            carbs: 23,
            protein: 6.0,
            iron: 7,
          },
          {
            name: 'Cupcake',
            calories: 305,
            fat: 3.7,
            carbs: 67,
            protein: 4.3,
            iron: 8,
          },
          {
            name: 'Gingerbread',
            calories: 356,
            fat: 16.0,
            carbs: 49,
            protein: 3.9,
            iron: 16,
          },
          {
            name: 'Jelly bean',
            calories: 375,
            fat: 0.0,
            carbs: 94,
            protein: 0.0,
            iron: 0,
          },
          {
            name: 'Lollipop',
            calories: 392,
            fat: 0.2,
            carbs: 98,
            protein: 0,
            iron: 2,
          },
          {
            name: 'Honeycomb',
            calories: 408,
            fat: 3.2,
            carbs: 87,
            protein: 6.5,
            iron: 45,
          },
          {
            name: 'Donut',
            calories: 452,
            fat: 25.0,
            carbs: 51,
            protein: 4.9,
            iron: 22,
          },
          {
            name: 'KitKat',
            calories: 518,
            fat: 26.0,
            carbs: 65,
            protein: 7,
            iron: 6,
          },
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
    editItem (item) {
        this.editedIndex = this.desserts.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.dialog = true
      },

      deleteItem (item) {
        this.editedIndex = this.desserts.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.dialogDelete = true
      },

      deleteItemConfirm () {
        this.desserts.splice(this.editedIndex, 1)
        this.closeDelete()
      },

      close () {
        this.dialog = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem)
          this.editedIndex = -1
        })
      },

      closeDelete () {
        this.dialogDelete = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem)
          this.editedIndex = -1
        })
      },

      save () {
        if (this.editedIndex > -1) {
          Object.assign(this.desserts[this.editedIndex], this.editedItem)
        } else {
          this.desserts.push(this.editedItem)
        }
        this.close()
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
          this.listOfClients = this.event.attendanceList;
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
    formTitle () {
        return this.editedIndex === -1 ? 'New Item' : 'Edit Item'
      },
      returnListOfClients() {
        return this.listOfClients;
      }
  },
  watch: {
      dialog (val) {
        val || this.close()
      },
      dialogDelete (val) {
        val || this.closeDelete()
      },
    },
};
</script>

<style>
</style>