<template>
  <v-container>
    <v-card color="grey lighten-4" min-width="350px" flat>
      <v-toolbar :color="event.color" dark>
        <v-btn icon>
          <v-icon @click="showCardEditForm">mdi-pencil</v-icon>
        </v-btn>
        <v-toolbar-title v-html="event.event_name"></v-toolbar-title>
        <v-spacer></v-spacer>

        <!-- <v-btn
                  icon
                  @click="toggleVisibleEvent(false)"
                  v-show="toggleVisibilityButton == false"
                >
                  <v-icon>mdi-eye-off</v-icon>
                </v-btn>
                <v-btn icon @click="toggleVisibleEvent(true)" v-show="toggleVisibilityButton == true">
                  <v-icon>mdi-eye-check</v-icon>
                </v-btn> -->

        
      </v-toolbar>
      <v-card-text v-show="!showEditForm">
        <span
          >{{ new Date(event.start_time).getFullYear() }}-{{
            new Date(event.start_time).getMonth() + 1
          }}-{{ new Date(event.start_time).getDate() }}</span
        >
        <br />
        <span
          >Starts at:
          {{
            new Date(event.start_time).toLocaleString("en-US", {
              hour: "numeric",
              minute: "numeric",
              hour12: true,
            })
          }}
        </span>
        <br />
        <span
          >Ends at:
          {{
            new Date(event.end_time).toLocaleString("en-US", {
              hour: "numeric",
              minute: "numeric",
              hour12: true,
            })
          }}</span
        >
      </v-card-text>
      <!-- UPDATE EVENT FORM -->
      <v-card-text v-show="showEditForm">
        <v-form
          ref="form"
          height="100"
          width="400"
          v-model="valid"
          lazy-validation
          class="class-form mx-auto"
          @submit.prevent="submitUpdate"
          justify="center"
          align="center"
        >
          <v-text-field
            v-model="editedEvent.event_name"
            :rules="titleRules"
            label="Edit title"
            required
          ></v-text-field>
          <!-- DATE PICKER -->
          <v-row>
            <v-col>
              <v-menu
                v-model="editMenu"
                :close-on-content-click="false"
                :nudge-right="40"
                transition="scale-transition"
                offset-y
                min-width="auto"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field
                    v-model="date"
                    :rules="timeRules"
                    readonly
                    v-bind="attrs"
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-date-picker
                  v-model="date"
                  @input="editMenu = false"
                ></v-date-picker>
              </v-menu>
            </v-col>
            <!-- FIRST TIME PICKER -->
            <v-col>
              <v-select
                label="Start Time"
                :items="allTimes"
                v-model="editedEvent.start_time"
                variant="underlined"
              ></v-select>
            </v-col>
            <!-- SECOND TIME PICKER -->
            <v-col>
              <v-select
                label="End Time"
                :items="returnCorrectEndTime"
                v-model="editedEvent.end_time"
                variant="underlined"
              ></v-select>
            </v-col> </v-row
          ><v-row>
            <v-spacer></v-spacer>
            <!-- COLOR PICKER -->
            <v-col cols="3">
              <v-menu
                v-model="colorPickerMenu2"
                :close-on-content-click="false"
                :nudge-right="40"
                transition="scale-transition"
                offset-y
                min-width="auto"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field
                    label="Color"
                    v-model="editedEvent.color"
                    readonly
                    v-bind="attrs"
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-color-picker
                  v-model="editedEvent.color"
                  hide-inputs
                ></v-color-picker> </v-menu
            ></v-col>
            <v-spacer></v-spacer>
            <v-checkbox
              v-model="editedEvent.is_visible_online"
              label="Visible Online?"
              required
            ></v-checkbox>
            <v-spacer></v-spacer>
          </v-row>
          <v-row justify="center" align="center"
            ><v-spacer></v-spacer
            ><v-col cols="3">
              <v-btn color="error" class="mr-4" @click="reset">
                Reset Form
              </v-btn>
            </v-col>
            <v-spacer></v-spacer>
            <v-col>
              <v-btn class="mr-4" type="submit" :disabled="invalid">
                submit
              </v-btn></v-col
            ><v-spacer></v-spacer></v-row
          ><v-spacer></v-spacer>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <!-- <v-btn text color="secondary" @click="closeSelectedCard">
          Cancel
        </v-btn> -->
        <v-btn icon>
          <v-icon @click="confirmDelete">mdi-delete</v-icon>
        </v-btn>
        <v-spacer></v-spacer>
        <!-- <v-btn icon>
          <v-icon @click="sendToEventDetailsPage">mdi-account-multiple</v-icon>
        </v-btn> -->
      </v-card-actions>
    </v-card>
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
    };
  },
  methods: {
    formatsIncomingEvent() {
      // finds YYYY-MM-DD format
    //   const [selectedMonth, selectedDay, selectedYear] = new Date(
    //     this.event.start_time
    //   )
    //     .toLocaleDateString()
    //     .split("/");
    //   this.date = selectedYear + "-" + selectedMonth + "-" + selectedDay;

    //   // converts Date Object to 12:00 AM/PM string
    //   this.editedEvent.start_time = new Date(
    //     this.event.start_time
    //   ).toLocaleString("en-US", {
    //     hour: "numeric",
    //     minute: "numeric",
    //     hour12: true,
    //   });
    //   //   this.event.start_time = this.editedEvent.start_time;
    //   this.editedEvent.end_time = new Date(
    //     this.event.end_time
    //   ).toLocaleString("en-US", {
    //     hour: "numeric",
    //     minute: "numeric",
    //     hour12: true,
    //   });
    //   //   this.event.end_time = this.editedEvent.end_time;
    },
    showCardEditForm() {
      this.showEditForm = !this.showEditForm;
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
  },
};
</script>

<style>
</style>