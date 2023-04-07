<template>
  <v-container>
    <!-- DELETE SNACKBAR -->
    <v-snackbar
      v-model="snackBarDeleteEventWarning"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
    >
      Warning: This Event Has A Roster - Can't Be Deleted

      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarDeleteEventWarning = false"
          left
        >
          Close
        </v-btn>
        <!-- <v-btn color="white" text v-bind="attrs" @click="allowEventDelete">
          Continue
        </v-btn> -->
      </template>
    </v-snackbar>
    <v-snackbar
      v-model="snackBarDeleteEventConfirmation"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
    >
      Warning: Delete this Event?
      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarDeleteEventConfirmation = false"
          left
        >
          Close
        </v-btn>
        <v-btn color="white" text v-bind="attrs" @click="allowEventDelete">
          Continue
        </v-btn>
      </template>
    </v-snackbar>
    <!-- UPDATE SNACKBAR -->
    <!-- <v-snackbar
      v-model="snackBarUpdateEventWarning"
      color="yellow darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
    >
      Warning: Edit this event?

      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarUpdateEventWarning = false"
          left
        >
          Close
        </v-btn>
        <v-btn color="white" text v-bind="attrs" @click.prevent="submitUpdate">
          Continue
        </v-btn>
      </template>
    </v-snackbar> -->
    <v-row class="fill-height">
      <v-col>
        <v-sheet tile height="64" class="d-flex">
          <v-toolbar flat>
            <v-btn
              outlined
              class="mr-4"
              color="grey darken-2"
              @click="setToday"
            >
              Today
            </v-btn>
            <v-btn fab text small color="grey darken-2" @click="prev">
              <v-icon small> mdi-chevron-left </v-icon>
            </v-btn>
            <v-btn fab text small color="grey darken-2" @click="next">
              <v-icon small> mdi-chevron-right </v-icon>
            </v-btn>
            <v-spacer></v-spacer>
            <v-toolbar-title v-if="$refs.calendar">
              {{ $refs.calendar.title }}
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-spacer></v-spacer>
            <v-dialog v-model="dialog" max-width="500px">
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  color="primary"
                  dark
                  class="mb-2"
                  v-bind="attrs"
                  v-on="on"
                >
                  Create A Event
                </v-btn>
              </template>
              <v-card justify="center">
                <v-card-title>
                  <span class="text-h5"> Create Event </span>
                  <v-spacer></v-spacer>
                  <v-btn icon>
                    <v-icon @click="close" alt="close">mdi-window-close</v-icon>
                  </v-btn>
                </v-card-title>

                <!-- START OF CREATE EVENT FORM --->
                <v-container>
                  <v-row justify="center" style="min-height: 160px">
                    <v-col cols="10">
                      <v-form
                        ref="form"
                        height="100"
                        width="500"
                        v-model="valid"
                        lazy-validation
                        class="class-form mx-auto white"
                        @submit.prevent="submit"
                        justify="center"
                        align="center"
                      >
                        <v-text-field
                          v-model="event.event_name"
                          :rules="titleRules"
                          label="Add title"
                          required
                        ></v-text-field>
                        <!-- DATE PICKER -->

                        <v-row>
                          <v-col>
                            <v-menu
                              v-model="menu"
                              :close-on-content-click="false"
                              :nudge-right="40"
                              transition="scale-transition"
                              offset-y
                              min-width="auto"
                            >
                              <template v-slot:activator="{ on, attrs }">
                                <v-text-field
                                  v-model="dateRangeText"
                                  :rules="timeRules"
                                  readonly
                                  v-bind="attrs"
                                  v-on="on"
                                ></v-text-field>
                              </template>
                              <v-date-picker
                                v-model="dates"
                                multiple
                              ></v-date-picker>
                            </v-menu> </v-col
                          ><v-col>
                            <v-checkbox
                              v-model="event.is_visible_online"
                              label="Visible Online?"
                              required
                            ></v-checkbox></v-col
                        ></v-row>
                        <!-- FIRST TIME PICKER -->
                        <v-row>
                          <v-col>
                            <v-select
                              label="Start Time"
                              :items="allTimes"
                              v-model="event.start_time"
                              variant="underlined"
                            ></v-select>
                          </v-col>
                          <!-- SECOND TIME PICKER -->
                          <v-col>
                            <v-select
                              label="End Time"
                              :items="returnCorrectEndTime"
                              v-model="event.end_time"
                              variant="underlined"
                            ></v-select>
                          </v-col> </v-row
                        ><v-row>
                          <v-spacer></v-spacer>
                          <!-- COLOR PICKER -->
                          <v-col cols="3">
                            <v-menu
                              v-model="colorPickerMenu1"
                              :close-on-content-click="false"
                              :nudge-right="40"
                              transition="scale-transition"
                              offset-y
                              min-width="auto"
                            >
                              <template v-slot:activator="{ on, attrs }">
                                <v-text-field
                                  label="Color"
                                  v-model="event.color"
                                  readonly
                                  v-bind="attrs"
                                  v-on="on"
                                ></v-text-field>
                              </template>
                              <v-color-picker
                                v-model="event.color"
                                hide-inputs
                              ></v-color-picker> </v-menu
                          ></v-col>
                          <v-spacer></v-spacer>
                        </v-row>
                        <v-row justify="center" align="center"
                          ><v-col cols="10">
                            <v-btn color="error" class="mr-4" @click="reset">
                              Reset Form
                            </v-btn>
                          </v-col>
                          <v-col>
                            <v-btn
                              class="mr-4"
                              type="submit"
                              :disabled="invalid"
                            >
                              submit
                            </v-btn></v-col
                          ></v-row
                        >
                      </v-form>
                      <!-- End of Create Form -->
                    </v-col>
                  </v-row>
                </v-container>
              </v-card>
            </v-dialog>
            <v-spacer></v-spacer>
            <v-menu bottom right>
              <template v-slot:activator="{ on, attrs }">
                <v-btn outlined color="grey darken-2" v-bind="attrs" v-on="on">
                  <span>{{ typeToLabel[type] }}</span>
                  <v-icon right> mdi-menu-down </v-icon>
                </v-btn>
              </template>
              <v-list>
                <v-list-item @click="type = 'day'">
                  <v-list-item-title>Day</v-list-item-title>
                </v-list-item>
                <v-list-item @click="type = 'week'">
                  <v-list-item-title>Week</v-list-item-title>
                </v-list-item>
                <v-list-item @click="type = 'month'">
                  <v-list-item-title>Month</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </v-toolbar>
        </v-sheet>
        <v-sheet height="600">
          <v-calendar
            ref="calendar"
            v-model="value"
            color="primary"
            :type="type"
            :events="events"
            :event-overlap-mode="mode"
            :event-overlap-threshold="30"
            :event-color="getEventColor"
            @change="getEvents"
            @click:event="showEvent"
            @click:more="viewWeek"
            @click:date="viewDay"
          ></v-calendar>
          <!-- Show Selected Event -->
          <v-menu
            v-model="selectedOpen"
            :close-on-content-click="false"
            :activator="selectedElement"
            offset-x
          >
            <v-card color="grey lighten-4" min-width="350px" flat>
              <v-toolbar :color="selectedEvent.color" dark>
                <v-btn icon>
                  <v-icon @click="showCardEditForm">mdi-pencil</v-icon>
                </v-btn>
                <v-toolbar-title v-html="selectedEvent.name"></v-toolbar-title>
                <v-spacer></v-spacer>

                <v-btn
                  icon
                  @click="toggleVisibleEvent(false)"
                  v-show="toggleVisibilityButton == false"
                >
                  <v-icon>mdi-eye-off</v-icon>
                </v-btn>
                <v-btn
                  icon
                  @click="toggleVisibleEvent(true)"
                  v-show="toggleVisibilityButton == true"
                >
                  <v-icon>mdi-eye-check</v-icon>
                </v-btn>

                <v-btn icon>
                  <v-icon @click="closeSelectedCard">mdi-window-close</v-icon>
                </v-btn>
              </v-toolbar>
              <v-card-text v-show="!showEditForm">
                <span
                  >{{ new Date(selectedEvent.start).getFullYear() }}-{{
                    new Date(selectedEvent.start).getMonth() + 1
                  }}-{{ new Date(selectedEvent.start).getDate() }}</span
                >
                <br />
                <span
                  >Starts at:
                  {{
                    new Date(selectedEvent.start).toLocaleString("en-US", {
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
                    new Date(selectedEvent.end).toLocaleString("en-US", {
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
                            v-model="date2"
                            :rules="timeRules"
                            readonly
                            v-bind="attrs"
                            v-on="on"
                          ></v-text-field>
                        </template>
                        <v-date-picker
                          v-model="date2"
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
                <v-btn text color="secondary" @click="closeSelectedCard">
                  Cancel
                </v-btn>
                <v-btn icon>
                  <v-icon @click="confirmDelete">mdi-delete</v-icon>
                </v-btn>
                <v-spacer></v-spacer>
                <v-btn icon>
                  <v-icon @click="sendToEventDetailsPage"
                    >mdi-account-multiple</v-icon
                  >
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-menu>
        </v-sheet>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import classDetailService from "../services/ClassDetailService";
import eventService from "../services/EventService";

export default {
  name: "class-calendar",
  components: {},
  data: () => ({
    type: "month",
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
    date: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
      .toISOString()
      .substr(0, 10),
    types: ["month", "week", "day"],
    date2: "",
    dates: [],
    mode: "stack",
    modes: ["stack"],
    weekday: [0, 1, 2, 3, 4, 5, 6],
    weekdays: [
      { text: "Sun - Sat", value: [0, 1, 2, 3, 4, 5, 6] },
      // { text: 'Mon - Sun', value: [1, 2, 3, 4, 5, 6, 0] },
      // { text: 'Mon - Fri', value: [1, 2, 3, 4, 5] },
      // { text: 'Mon, Wed, Fri', value: [1, 3, 5] },
    ],
    titleRules: [
      (v) => !!v || "Title is required",
      (v) => (v && v.length <= 30) || "Title must be less than 30 characters",
    ],
    timeRules: [(v) => !!v || "Time is required"],
    colorRules: [(v) => !!v || "Color is required"],
    value: "",
    events: [],
    event: {
      class_id: 0,
      event_name: "",
      start_time: "",
      end_time: "",
      color: "#3388FF",
      timed: true,
      is_visible_online: true,
    },
    editedEvent: {
      class_id: 0,
      event_name: "",
      start_time: "",
      end_time: "",
      color: "#3388FF",
      timed: true,
      is_visible_online: true,
    },

    typeToLabel: {
      month: "Month",
      week: "Week",
      day: "Day",
    },
    selectedEvent: {},
    selectedEventID: "",
    selectedElement: null,
    selectedOpen: false,
    classes: [],
    serverEvents: [],
    menu: false,
    editMenu: false,
    colorPickerMenu1: false,
    colorPickerMenu2: false,
    dialog: false,
    snackBarDeleteEventWarning: false,
    snackBarDeleteEventConfirmation: false,
    showEditForm: false,
    createFormIncomplete: true,
    toggleVisibilityButton: true,
    // UPDATE PROPERTIES BELOW
    dragEvent: null,
    dragStart: null,
    createEvent: null,
    createStart: null,
    extendOriginal: null,
    // DELETE PROPERTIES BELOW
    locatedEvent: {},
    listOfSignedUpClients: [],
  }),
  methods: {
    checkCreateForm() {
      if (
        this.event.event_name == "" ||
        this.event.start_time.length == 0 ||
        this.event.end_time.length == 0 ||
        this.dates.length == 0
      ) {
        alert("Please fill out your form");
        this.createFormIncomplete = true;
      } else {
        this.createFormIncomplete = false;
      }
    },
    closeSelectedCard() {
      this.selectedOpen = false;
      this.showEditForm = false;
    },
    showCardEditForm() {
      this.showEditForm = !this.showEditForm;
    },
    getEventDetailsCallBeforeDeleting() {
      eventService
        .getEventDetailsByEventId(this.selectedEventID)
        .then((response) => {
          if (response.status == 200) {
            this.locatedEvent = response.data;

            this.listOfSignedUpClients = this.locatedEvent.attendanceList;
            if (this.listOfSignedUpClients.length > 0) {
              this.snackBarDeleteEventWarning = true;
            } else {
              this.snackBarDeleteEventConfirmation = true;
            }
          }
        });
    },
    confirmDelete() {
      // 1. create a new snackbar to let the admin know
      // they can't delete an event without an empty roster

      this.getEventDetailsCallBeforeDeleting();
    },
    allowEventDelete() {
      this.snackBarDeleteEventWarning = false;

      // find the ID of selected event
      this.findsMatch();

      eventService.deleteEvent(this.selectedEventID).then((response) => {
        if (response.status == 200) {
          this.events.splice(this.selectedEventIndex, 1);
          this.serverEvents.splice(this.selectedEventIndex, 1);
          // alert("Event successfully deleted!");
        } else {
          alert("Error removing event!");
        }
      });
    },
    submitUpdate() {
      // find the ID of selected event
      for (let i = 0; i < this.serverEvents.length; i++) {
        if (
          new Date(this.serverEvents[i].start_time).getTime() ==
            new Date(this.selectedEvent.start).getTime() &&
          new Date(this.serverEvents[i].end_time).getTime() ==
            new Date(this.selectedEvent.end).getTime() &&
          this.serverEvents[i].event_name == this.selectedEvent.name
        ) {
          this.selectedEventID = this.serverEvents[i].event_id;
          this.selectedEventIndex = i;

          if (this.editedEvent.is_visible_online == false) {
            this.serverEvents[this.selectedEventIndex].color = "#808080";
            this.events[this.selectedEventIndex].color = "#808080";
            this.editedEvent.color = "#808080";
            this.toggleVisibilityButton = true;
          } else {
            this.serverEvents[this.selectedEventIndex].color =
              this.editedEvent.color;
            this.events[this.selectedEventIndex].color = this.editedEvent.color;
            this.toggleVisibilityButton = false;
          }
        }
      }

      // make sure to format the object like you do in the submit method

      const [time, modifier] = this.editedEvent.start_time.split(" ");

      let [hours, minutes] = time.split(":");

      if (hours === "12") {
        hours = "00";
      }

      if (modifier === "PM") {
        hours = parseInt(hours, 10) + 12;
      } else if (hours.length == 1) {
        hours = "0" + hours;
      }

      let startTime = `${hours}:${minutes}`;

      const [time2, modifier2] = this.editedEvent.end_time.split(" ");

      let [hours2, minutes2] = time2.split(":");

      if (hours2 === "12") {
        hours2 = "00";
      }

      if (modifier2 === "PM") {
        hours2 = parseInt(hours2, 10) + 12;
      } else if (hours2.length == 1) {
        hours2 = "0" + hours2;
      }

      let endTime = `${hours2}:${minutes2}`;

      let chosenDate = this.date2;
      let newStartDate = new Date(chosenDate + " " + startTime).toJSON();
      let newEndDate = new Date(chosenDate + " " + endTime).toJSON();
      // alert(newStartDate)
      // alert(newEndDate)
      this.editedEvent.start_time = newStartDate;
      this.editedEvent.end_time = newEndDate;
      // this will eventually be an update instead,
      eventService.updateEvent(this.editedEvent).then((response) => {
        if (response.status == 200) {
          // alert("Event successfully updated!");
          this.getAllEvents();
        } else {
          alert("Error removing event!");
        }
      });
    },
    confirmUpdate() {
      this.snackBarUpdateEventWarning = true;
    },
    toggleVisibleEvent(value) {
      this.snackBarupdateEventWarning = false;

      this.editedEvent.is_visible_online = value;
      if (value) {
        // make it colorful again

        this.editedEvent.color = "orange";
        this.events[this.selectedEventIndex].color = "orange";
        this.serverEvents[this.selectedEventIndex].color = "orange";

        this.toggleVisibilityButton = false;
      } else {
        // make it grey

        this.editedEvent.color = "#808080";
        this.events[this.selectedEventIndex].color = "#808080";
        this.serverEvents[this.selectedEventIndex].color = "#808080";

        this.toggleVisibilityButton = true;
      }
      // find the right times
      this.editedEvent.start_time =
        this.serverEvents[this.selectedEventIndex].start_time;
      this.editedEvent.end_time =
        this.serverEvents[this.selectedEventIndex].end_time;

      eventService.updateEvent(this.editedEvent).then((response) => {
        if (response.status == 200) {
          this.getAllEvents();
          // alert("Event successfully updated!");
        } else {
          alert("Error removing event!");
        }
      });
    },
    viewDay({ date }) {
      this.value = date;
      this.type = "day";
    },
    viewWeek({ date }) {
      this.value = date;
      this.type = "week";
    },
    setToday() {
      this.value = "";
    },
    prev() {
      this.$refs.calendar.prev();
    },
    next() {
      this.$refs.calendar.next();
    },
    showEvent({ nativeEvent, event }) {
      const open = () => {
        this.selectedEvent = event;
        this.selectedElement = nativeEvent.target;
        requestAnimationFrame(() =>
          requestAnimationFrame(() => (this.selectedOpen = true))
        );

        // LOGIC GOES BELOW HERE

        this.findsMatch();
        // LOGIC GOES ABOVE HERE
      };

      if (this.selectedOpen) {
        this.selectedOpen = false;
        requestAnimationFrame(() => requestAnimationFrame(() => open()));
      } else {
        open();
      }

      nativeEvent.stopPropagation();
    },
    findsMatch() {
      this.editedEvent.event_name = this.selectedEvent.name;

      // finds YYYY-MM-DD format
      const [selectedMonth, selectedDay, selectedYear] = new Date(
        this.selectedEvent.start
      )
        .toLocaleDateString()
        .split("/");
      this.date2 = selectedYear + "-" + selectedMonth + "-" + selectedDay;

      // converts Date Object to 12:00 AM/PM string
      this.editedEvent.start_time = new Date(
        this.selectedEvent.start
      ).toLocaleString("en-US", {
        hour: "numeric",
        minute: "numeric",
        hour12: true,
      });
      this.editedEvent.end_time = new Date(
        this.selectedEvent.end
      ).toLocaleString("en-US", {
        hour: "numeric",
        minute: "numeric",
        hour12: true,
      });

      // assigns the correct color pulled from what was selected
      this.editedEvent.color = this.selectedEvent.color;

      // begin looping to find match for more information
      for (let i = 0; i < this.serverEvents.length; i++) {
        if (
          new Date(this.serverEvents[i].start_time).getTime() ==
            new Date(this.selectedEvent.start).getTime() &&
          new Date(this.serverEvents[i].end_time).getTime() ==
            new Date(this.selectedEvent.end).getTime() &&
          this.serverEvents[i].event_name == this.selectedEvent.name
        ) {
          // match the event_id
          this.editedEvent.event_id = this.serverEvents[i].event_id;
          // match the class id
          this.editedEvent.class_id = this.serverEvents[i].class_id;
          // match the visibility boolean
          this.editedEvent.is_visible_online =
            this.serverEvents[i].is_visible_online;
          // log the ID in this variable
          this.selectedEventID = this.serverEvents[i].event_id;
          // log the index in this variable
          this.selectedEventIndex = i;

          // toggle the eye icon
          if (!this.serverEvents[i].is_visible_online) {
            this.toggleVisibilityButton = true;
          } else {
            this.toggleVisibilityButton = false;
          }
        }
      }
    },
    getEvents() {
      this.eventQuantity = this.$store.state.eventList.length;

      const events = [];

      this.serverEvents.forEach((event) => {
        events.push({
          name: event.event_name,
          start: new Date(event.start_time),
          end: new Date(event.end_time),
          color: event.color,
          timed: event.timed,
        });
      });

      this.events = events;
    },
    getEventColor(event) {
      return event.color;
    },
    getAllClasses() {
      classDetailService.getAllClasses().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_CLASS_LIST", response.data);
          this.classes = response.data;
        } else {
          alert("Error retrieving class information");
        }
      });
    },
    getAllEvents() {
      eventService.getAllEvents().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_EVENT_LIST", response.data);
          this.serverEvents = response.data;

          this.getEvents();
        }
      });
    },
    close() {
      // for closing the create event form
      this.dialog = false;
      this.reset();
    },
    reset() {
      // resets a create form
      this.event.start_time = "";
      this.event.event_name = "";
      this.dates = [];
      this.event.color = "#3388FF";
      this.event.is_visible_online = true;
      // dont uncomment the next line, let it stay the computed property
      // this.event.end_time = "";

      // resets a edit form
      this.findsMatch();
    },
    submit() {
      // check if the form is complete
      this.checkCreateForm();
      if (this.createFormIncomplete == false) {
        // first grab the AM PM times and turn them into date objects

        const [time, modifier] = this.event.start_time.split(" ");

        let [hours, minutes] = time.split(":");

        if (hours === "12") {
          hours = "00";
        }

        if (modifier === "PM") {
          hours = parseInt(hours, 10) + 12;
        } else if (hours.length == 1) {
          hours = "0" + hours;
        }

        let startTime = `${hours}:${minutes}`;

        const [time2, modifier2] = this.event.end_time.split(" ");

        let [hours2, minutes2] = time2.split(":");

        if (hours2 === "12") {
          hours2 = "00";
        }

        if (modifier2 === "PM") {
          hours2 = parseInt(hours2, 10) + 12;
        } else if (hours2.length == 1) {
          hours2 = "0" + hours2;
        }

        let endTime = `${hours2}:${minutes2}`;

        for (let index = 0; index < this.dates.length; index++) {
          let chosenDate = this.dates[index];
          let newStartDate = new Date(chosenDate + "T" + startTime).toJSON();
          let newEndDate = new Date(chosenDate + "T" + endTime).toJSON();
          this.event.start_time = newStartDate;

          this.event.end_time = newEndDate;

          // then call the service

          eventService.createEvent(this.event).then((response) => {
            if (response.status == 201) {
              // VERY EXPENSIVE API CALL please optimizie
              this.getAllEvents();

              // the following condition calls the api service to retrieve all events from DB after the last one (expensive way))
              //   if ((index += 1) == this.dates.length) {
              //     alert(index+=1)
              // }
            } else {
              alert("Failed to create event");
            }
          });

          //end of loop block
        }
        // closes create event dialog
        this.close();
      }
    },
    sendToEventDetailsPage() {
      this.$router.push({
        name: "event-attendance-details",
        params: { eventId: this.selectedEventID },
      });
    },
    //////////////////////////////DRAG AND UPDATE METHODS BELOW
    startDrag({ event, timed }) {
      if (event && timed) {
        this.dragEvent = event;
        this.dragTime = null;
        this.extendOriginal = null;
      }
    },
    startTime(tms) {
      const mouse = this.toTime(tms);

      if (this.dragEvent && this.dragTime === null) {
        const start = this.dragEvent.start;

        this.dragTime = mouse - start;
      } else {
        this.createStart = this.roundTime(mouse);
        this.createEvent = {
          name: `Event #${this.events.length}`,
          color: this.rndElement(this.colors),
          start: this.createStart,
          end: this.createStart,
          timed: true,
        };

        this.events.push(this.createEvent);
      }
    },
    extendBottom(event) {
      this.createEvent = event;
      this.createStart = event.start;
      this.extendOriginal = event.end;
    },
    mouseMove(tms) {
      const mouse = this.toTime(tms);

      if (this.dragEvent && this.dragTime !== null) {
        const start = this.dragEvent.start;
        const end = this.dragEvent.end;
        const duration = end - start;
        const newStartTime = mouse - this.dragTime;
        const newStart = this.roundTime(newStartTime);
        const newEnd = newStart + duration;

        this.dragEvent.start = newStart;
        this.dragEvent.end = newEnd;
      } else if (this.createEvent && this.createStart !== null) {
        const mouseRounded = this.roundTime(mouse, false);
        const min = Math.min(mouseRounded, this.createStart);
        const max = Math.max(mouseRounded, this.createStart);

        this.createEvent.start = min;
        this.createEvent.end = max;
      }
    },
    endDrag() {
      //  @mouseleave.native="cancelDrag"
      // this.submitUpdate()
      this.dragTime = null;
      this.dragEvent = null;
      this.createEvent = null;
      this.createStart = null;
      this.extendOriginal = null;
    },
    cancelDrag() {
      if (this.createEvent) {
        if (this.extendOriginal) {
          this.createEvent.end = this.extendOriginal;
        } else {
          const i = this.events.indexOf(this.createEvent);
          if (i !== -1) {
            this.events.splice(i, 1);
          }
        }
      }

      this.createEvent = null;
      this.createStart = null;
      this.dragTime = null;
      this.dragEvent = null;
    },
    roundTime(time, down = true) {
      const roundTo = 15; // minutes
      const roundDownTime = roundTo * 60 * 1000;

      return down
        ? time - (time % roundDownTime)
        : time + (roundDownTime - (time % roundDownTime));
    },
    toTime(tms) {
      return new Date(
        tms.year,
        tms.month - 1,
        tms.day,
        tms.hour,
        tms.minute
      ).getTime();
    },
  },
  created() {
    this.getAllClasses();
    this.getAllEvents();

    this.$root.$refs.Z = this;
  },
  mounted() {
    this.$refs.calendar.checkChange();
  },
  computed: {
    returnCorrectEndTime() {
      // method used to make sure end_time list dropdown comes after the start_time list drop down
      if (this.event.start_time != "") {
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
    dateRangeText() {
      if (this.dates.length == 1) {
        return this.dates.length + " Day Selected";
      } else {
        return this.dates.length + " Days Selected";
      }
    },
  },
};
</script>

<style>
.btn-am {
  float: left;
}

.btn-pm {
  float: right;
}

.v-date-time-widget-container {
  background: white;
  padding: 10px;
}
</style>