<template>
  <v-container>
    <v-snackbar
      v-model="snackBarDeleteEventWarning"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
    >
      Warning: Are you sure you want to delete this event?

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
        <v-btn color="white" text v-bind="attrs" @click="allowEventDelete">
          Continue
        </v-btn>
      </template>
    </v-snackbar>
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
                                  v-model="date"
                                  :rules="timeRules"
                                  readonly
                                  v-bind="attrs"
                                  v-on="on"
                                ></v-text-field>
                              </template>
                              <v-date-picker
                                v-model="date"
                                @input="menu = false"
                              ></v-date-picker>
                            </v-menu>
                          </v-col>
                          <!-- FIRST TIME PICKER -->
                          <v-col>
                            <v-menu
                              ref="menu2"
                              v-model="dropDownOpen"
                              :close-on-content-click="false"
                              :nudge-right="40"
                              :return-value.sync="selectedTime"
                              lazy
                              transition="scale-transition"
                              offset-y
                              max-width="330px"
                              min-width="330px"
                            >
                              <template v-slot:activator="{ on }">
                                <v-text-field
                                  v-model="displayTime"
                                  :rules="timeRules"
                                  label="Start Time"
                                  readonly
                                  v-on="on"
                                ></v-text-field>
                              </template>
                              <v-container
                                class="v-date-time-widget-container"
                                fluid
                              >
                                <br />
                                <v-row>
                                  <br />
                                  <v-spacer></v-spacer>
                                  <v-btn
                                    fab
                                    small
                                    :color="getMeridiamButtonColor('AM')"
                                    class="btn-am"
                                    @click="meridiam = 'AM'"
                                    >AM</v-btn
                                  >
                                  <v-spacer></v-spacer>
                                  <v-btn
                                    fab
                                    small
                                    :color="getMeridiamButtonColor('PM')"
                                    class="btn-pm"
                                    @click="meridiam = 'PM'"
                                    >PM</v-btn
                                  >
                                  <v-spacer></v-spacer
                                ></v-row>
                                <br />
                                <v-time-picker
                                  v-if="dropDownOpen"
                                  v-model="timeModel"
                                  full-width
                                  scrollable
                                  :no-title="true"
                                  ampm-in-title
                                  ><v-spacer></v-spacer>

                                  <v-btn
                                    text
                                    color="primary"
                                    @click="dropDownOpen = false"
                                  >
                                    Cancel
                                  </v-btn>
                                  <v-btn
                                    text
                                    color="primary"
                                    @click="confirm()"
                                  >
                                    OK
                                  </v-btn></v-time-picker
                                >
                                <!-- END OF FIRST TIME PICKER -->
                              </v-container>
                            </v-menu>
                          </v-col>
                          <!-- SECOND TIME PICKER -->
                          <v-col>
                            <v-menu
                              ref="menu2"
                              v-model="dropDownOpen2"
                              :close-on-content-click="false"
                              :nudge-right="40"
                              :return-value.sync="selectedTime2"
                              lazy
                              transition="scale-transition"
                              offset-y
                              max-width="330px"
                              min-width="330px"
                            >
                              <template v-slot:activator="{ on }">
                                <v-text-field
                                  v-model="displayTime2"
                                  :rules="timeRules"
                                  label="End Time"
                                  readonly
                                  v-on="on"
                                ></v-text-field>
                              </template>
                              <v-container
                                class="v-date-time-widget-container"
                                fluid
                              >
                                <br />
                                <v-row>
                                  <br />
                                  <v-spacer></v-spacer>
                                  <v-btn
                                    fab
                                    small
                                    :color="getMeridiamButtonColor2('AM')"
                                    class="btn-am"
                                    @click="meridiam2 = 'AM'"
                                    >AM</v-btn
                                  >
                                  <v-spacer></v-spacer>
                                  <v-btn
                                    fab
                                    small
                                    :color="getMeridiamButtonColor2('PM')"
                                    class="btn-pm"
                                    @click="meridiam2 = 'PM'"
                                    >PM</v-btn
                                  >
                                  <v-spacer></v-spacer
                                ></v-row>
                                <br />
                                <v-time-picker
                                  v-if="dropDownOpen2"
                                  v-model="timeModel2"
                                  full-width
                                  scrollable
                                  :no-title="true"
                                  ampm-in-title
                                  ><v-spacer></v-spacer>

                                  <v-btn
                                    text
                                    color="primary"
                                    @click="dropDownOpen2 = false"
                                  >
                                    Cancel
                                  </v-btn>
                                  <v-btn
                                    text
                                    color="primary"
                                    @click="confirm2()"
                                  >
                                    OK
                                  </v-btn></v-time-picker
                                >
                                <!-- END OF TIME PICKER -->
                              </v-container>
                            </v-menu>
                          </v-col> </v-row
                        ><v-row>
                          <v-spacer></v-spacer>
                          <!-- COLOR PICKER -->
                          <v-col cols="3">
                            <v-menu
                              v-model="menu3"
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
          <v-menu
            v-model="selectedOpen"
            :close-on-content-click="false"
            :activator="selectedElement"
            offset-x
          >
            <v-card color="grey lighten-4" min-width="350px" flat>
              <v-toolbar :color="selectedEvent.color" dark>
                <v-btn icon>
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-toolbar-title v-html="selectedEvent.name"></v-toolbar-title>
                <v-spacer></v-spacer>
                <v-btn icon>
                  <v-icon @click="confirmDelete">mdi-delete</v-icon>
                </v-btn>
              </v-toolbar>
              <v-card-text>
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
              <v-card-actions>
                <v-btn text color="secondary" @click="selectedOpen = false">
                  Cancel
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

    date: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
      .toISOString()
      .substr(0, 10),

    types: ["month", "week", "day"],
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
    colors: [
      "blue",
      "indigo",
      "deep-purple",
      "cyan",
      "green",
      "orange",
      "grey darken-1",
    ],
    event: {
      class_id: 16,
      event_name: "",
      start_time: "",
      end_time: "",
      color: "",
      timed: true,
    },
    typeToLabel: {
      month: "Month",
      week: "Week",
      day: "Day",
    },
    selectedEvent: {},
    selectedEventID: "",
    selectedEventIndex: "",
    selectedElement: null,
    selectedOpen: false,
    eventCount: "",
    classes: [],
    days: "",
    allDay: "",
    firstTimestamp: "",
    first: "",
    secondTimestamp: "",
    second: "",
    serverEvents: [],
    eventQuantity: "",
    menu: false,
    menu2: false,
    menu3: false,
    dialog: false,
    // start time
    dropDownOpen: false,
    selectedTime: "",
    displayTime: "",
    meridiam: "",
    timeModel: "",
    dateModel: "",
    // end time
    dropDownOpen2: false,
    selectedTime2: "",
    displayTime2: "",
    meridiam2: "",
    timeModel2: "",
    snackBarDeleteEventWarning: false,
  }),
  methods: {
    confirmDelete() {
      this.snackBarDeleteEventWarning = true;
    },
    allowEventDelete() {
      this.snackBarDeleteEventWarning = false;
      // find the ID of selected event
      for (let i = 0; i < this.serverEvents.length; i++) {
        if (
         new Date(this.serverEvents[i].start_time).getTime() == new Date(this.selectedEvent.start).getTime() &&
          new Date(this.serverEvents[i].end_time).getTime() == new Date(this.selectedEvent.end).getTime() &&
          this.serverEvents[i].event_name == this.selectedEvent.name
        ) {
          this.selectedEventID = this.serverEvents[i].event_id;
          this.selectedEventIndex = i;
        }
      }

      // this will eventually be an update instead
      eventService.deleteEvent(this.selectedEventID).then((response) => {
        if (response.status == 200) {
          // remove it from the calendar event list
          this.events.splice(this.selectedEventIndex,1)
          alert("Event successfully removed!");
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
      };

      if (this.selectedOpen) {
        this.selectedOpen = false;
        requestAnimationFrame(() => requestAnimationFrame(() => open()));
      } else {
        open();
      }

      nativeEvent.stopPropagation();
    },
    getEvents() {
      // ==============================================  TEMPLATE

      // const events = []
      this.eventQuantity = this.$store.state.eventList.length;

      // for (let i = 0; i < this.eventQuantity; i++) {

      //   events.push({
      //     name: this.names[this.rnd(0, this.names.length - 1)],
      //     start: new Date("2023-01-23T12:00:00.000+00:00"),
      //     end: new Date("2023-01-23T12:00:00.000+00:00"),
      //     color: this.colors[this.rnd(0, this.colors.length - 1)],
      //     timed: true,
      //   })
      // }

      // this.events = events

      // ==============================================  TEMPLATE

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
    rnd(a, b) {
      return Math.floor((b - a + 1) * Math.random()) + a;
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
    getMeridiamButtonColor(m) {
      if (m == this.meridiam) {
        return "primary";
      } else {
        return "dark-gray";
      }
    },
    getMeridiamButtonColor2(m) {
      if (m == this.meridiam2) {
        return "primary";
      } else {
        return "dark-gray";
      }
    },
    confirm() {
      this.dropDownOpen = false;
      this.dropDownOpen2 = false;
      this.onUpdateDate();
    },
    onUpdateDate() {
      if (!this.timeModel) {
        return false;
      }
      if (this.timeModel == "00:00") {
        this.timeModel = "12:00";
      }
      this.selectedTime = this.timeModel + " " + this.meridiam;
      this.displayTime = this.selectedTime;
      this.event.start_time = this.selectedTime;
      this.editedItem.start_time = this.selectedTime;
      this.$emit("input", this.selectedTime);
      this.dropDownOpen = false;
    },
    confirm2() {
      this.dropDownOpen2 = false;
      this.onUpdateDate2();
    },
    onUpdateDate2() {
      if (!this.timeModel2) {
        return false;
      }
      if (this.timeModel2 == "00:00") {
        this.timeModel2 = "12:00";
      }
      this.selectedTime2 = this.timeModel2 + " " + this.meridiam2;
      this.displayTime2 = this.selectedTime2;
      this.event.end_time = this.selectedTime2;
      this.editedItem.end_time = this.selectedTime2;
      this.$emit("input", this.selectedTime2);
      this.dropDownOpen2 = false;
    },
    close() {
      this.dialog = false;
      this.reset();
    },
    reset() {
      this.event.event_name = "";
      this.event.start_time = "";
      this.event.end_time = "";
      this.event.color = "";
    },
    submit() {
      const [time, modifier] = this.event.start_time.toString().split(" ");

      let [hours, minutes] = time.split(":");

      if (hours === "12") {
        hours = "00";
      }

      if (modifier === "PM") {
        hours = parseInt(hours, 10) + 12;
      }

      this.event.start_time = `${hours}:${minutes}`;

      const [time2, modifier2] = this.event.end_time.toString().split(" ");

      let [hours2, minutes2] = time2.split(":");

      if (hours2 === "12") {
        hours2 = "00";
      }

      if (modifier2 === "PM") {
        hours2 = parseInt(hours2, 10) + 12;
      }

      this.event.end_time = `${hours2}:${minutes2}`;

      this.event.start_time = new Date(
        this.dateModel + "T" + this.event.start_time
      );
      this.event.end_time = new Date(
        this.dateModel + "T" + this.event.end_time
      );
      eventService.createEvent(this.event).then((response) => {
        if (response.status == 201) {
          alert("Successfully created event");
          this.getAllEvents();
          this.reset();
          this.close();
        } else {
          alert("Failed to create event");
        }
      });
    },
  },
  created() {
    this.getAllClasses();
    this.getAllEvents();

    var d = new Date();
    var currentHour = d.getHours() % 12;
    var minutes = (d.getMinutes() < 10 ? "0" : "") + d.getMinutes();
    var currentTime = currentHour + ":" + minutes;
    this.timeModel = currentTime;
    this.dateModel = d.toISOString().substr(0, 10);

    if (d.getHours() >= 12) {
      this.meridiam = "PM";
      this.meridiam2 = "PM";
    }
  },
  convertTime12to24(amPmTime) {
    const [time, modifier] = amPmTime.toString().split(" ");

    let [hours, minutes] = time.split(":");

    if (hours === "12") {
      hours = "00";
    }

    if (modifier === "PM") {
      hours = parseInt(hours, 10) + 12;
    }

    return `${hours}:${minutes}`;
  },
  mounted() {
    this.$refs.calendar.checkChange();
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