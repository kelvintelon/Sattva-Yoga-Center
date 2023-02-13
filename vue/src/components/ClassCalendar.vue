<template>
  <v-container>
    <v-snackbar
      v-model="snackBarDeleteEventWarning"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
    >
      Warning: Are you sure you want to hide this event?

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
                            <!-- <v-menu
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
                                
                              </v-container>
                            </v-menu> -->
                            <v-select label="Start Time" :items="allTimes" v-model="event.start_time" variant="underlined"></v-select>
                          </v-col>
                          <!-- SECOND TIME PICKER -->
                          <v-col>
                            <!-- <v-menu
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
                                
                              </v-container>
                            </v-menu> -->
                            <v-select label="End Time" :items="returnCorrectEndTime" v-model="event.end_time" variant="underlined"></v-select>
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
                          <v-checkbox v-model="event.is_visible_online" label="Visible Online?" required></v-checkbox>
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
                  <v-icon @click="showCardEditForm">mdi-pencil</v-icon>
                </v-btn>
                <v-toolbar-title v-html="selectedEvent.name"></v-toolbar-title>
                <v-spacer></v-spacer>
                <v-btn icon>
                  <v-icon @click="confirmDelete">mdi-delete</v-icon>
                </v-btn>
              </v-toolbar>
              <v-card-text v-if="showEditForm == false">
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
              <v-card-text v-else>
                <v-form
                        ref="form"
                        height="100"
                        width="500"
                        v-model="valid"
                        lazy-validation
                        class="class-form mx-auto"
                        @submit.prevent="submit"
                        justify="center"
                        align="center"
                      >
                        <v-text-field
                          v-model="editedEvent.event_name"
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
                                  v-model="date2"
                                  :rules="timeRules"
                                  readonly
                                  v-bind="attrs"
                                  v-on="on"
                                ></v-text-field>
                              </template>
                              <v-date-picker
                                v-model="date2"
                                @input="menu = false"
                              ></v-date-picker>
                            </v-menu>
                          </v-col>
                          <!-- FIRST TIME PICKER -->
                          <v-col>
                            <v-menu
                              ref="menu2"
                              v-model="dropDownOpen3"
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
                                  v-model="displayTime3"
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
                                  v-if="dropDownOpen3"
                                  v-model="timeModel"
                                  full-width
                                  scrollable
                                  :no-title="true"
                                  ampm-in-title
                                  ><v-spacer></v-spacer>

                                  <v-btn
                                    text
                                    color="primary"
                                    @click="dropDownOpen3 = false"
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
                              v-model="dropDownOpen4"
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
                                  v-model="displayTime4"
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
                                  v-if="dropDownOpen4"
                                  v-model="timeModel2"
                                  full-width
                                  scrollable
                                  :no-title="true"
                                  ampm-in-title
                                  ><v-spacer></v-spacer>

                                  <v-btn
                                    text
                                    color="primary"
                                    @click="dropDownOpen4 = false"
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
                              v-model="menu4"
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
                          <v-checkbox v-model="editedEvent.is_visible_online" label="Visible Online?" required></v-checkbox>
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
              </v-card-text>
              <v-card-actions>
                <v-btn text color="secondary" @click="closeCard">
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
    allTimes: ["12:00 AM", "12:15 AM", "12:30 AM", "12:45 AM", "1:00 AM", "1:15 AM", "1:30 AM", "1:45 AM", "2:00 AM", "2:15 AM", "2:30 AM", "2:45 AM", "3:00 AM", "3:15 AM", "3:30 AM", "3:45 AM", "4:00 AM", "4:15 AM", "4:30 AM", "4:45 AM", "5:00 AM", "5:15 AM", "5:30 AM", "5:45 AM", "6:00 AM", "6:15 AM", "6:30 AM", "6:45 AM", "7:00 AM", "7:15 AM", "7:30 AM", "7:45 AM", "8:00 AM", "8:15 AM", "8:30 AM", "8:45 AM", "9:00 AM", "9:15 AM", "9:30 AM", "9:45 AM", "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM", "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM", "12:00 PM", "12:15 PM", "12:30 PM", "12:45 PM", "1:00 PM", "1:15 PM", "1:30 PM", "1:45 PM", "2:00 PM", "2:15 PM", "2:30 PM", "2:45 PM", "3:00 PM", "3:15 PM", "3:30 PM", "3:45 PM", "4:00 PM", "4:15 PM", "4:30 PM", "4:45 PM", "5:00 PM", "5:15 PM", "5:30 PM", "5:45 PM", "6:00 PM", "6:15 PM", "6:30 PM", "6:45 PM", "7:00 PM", "7:15 PM", "7:30 PM", "7:45 PM", "8:00 PM", "8:15 PM", "8:30 PM", "8:45 PM", "9:00 PM", "9:15 PM", "9:30 PM", "9:45 PM", "10:00 PM", "10:15 PM", "10:30 PM", "10:45 PM", "11:00 PM", "11:15 PM", "11:30 PM"],
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
      class_id: 0,
      event_name: "",
      start_time: "",
      end_time: "",
      color: "#3388FF",
      timed: true,
      is_visible_online: true
    },
    editedEvent: {
      class_id: 0,
      event_name: "",
      start_time: "",
      end_time: "",
      color: "#3388FF",
      timed: true,
      is_visible_online: true
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
    menu4: false,
    dialog: false,
    // start time
    dropDownOpen: false,
    dropDownOpen3: false,
    selectedTime: "",
    displayTime: "",
    displayTime3: "",
    meridiam: "",
    timeModel: "",
    dateModel: "",
    // end time
    dropDownOpen2: false,
    dropDownOpen4: false,
    selectedTime2: "",
    displayTime2: "",
    displayTime4: "",
    meridiam2: "",
    timeModel2: "",
    snackBarDeleteEventWarning: false,
    showEditForm: false,
    createFormIncomplete: true,
  }),
  methods: {
    checkCreateForm() {
      if (
        this.event.event_name == "" ||
        this.event.start_time.length == 0 ||
        this.event.end_time.length == 0
      ) {
        alert("Please fill out your form");
      } else {
        this.createFormIncomplete = false;
      }
    },
    closeCard() {
      this.selectedOpen = false;
      this.showEditForm = false;
    },
    showCardEditForm(){
      this.showEditForm = !this.showEditForm;
      
    },
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
          this.serverEvents[this.selectedEventIndex].color = "#808080"
          this.events[this.selectedEventIndex].color = "#808080"
        }
      }

      // make sure to format the object like you do in the submit method

      // this will eventually be an update instead, 
      eventService.updateEvent(this.serverEvents[this.selectedEventIndex]).then((response) => {
        if (response.status == 200) {
          // remove it from the calendar event list
          alert("Event successfully hidden!");
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

      //    this.editedEvent.event_name = this.selectedEvent.name;
      // this.editedEvent.start_time = new Date(this.selectedEvent.start).toLocaleString("en-US", {
      //                 hour: "numeric",
      //                 minute: "numeric",
      //                 hour12: true,
      //               });
      // // this.displayTime3 = new Date(this.selectedEvent.start).toLocaleString("en-US", {
      // //                 hour: "numeric",
      // //                 minute: "numeric",
      // //                 hour12: true,
      // //               });
      // this.editedEvent.end_time = new Date(this.selectedEvent.end).toLocaleString("en-US", {
      //                 hour: "numeric",
      //                 minute: "numeric",
      //                 hour12: true,
      //               });
      // // this.displayTime4 = new Date(this.selectedEvent.end).toLocaleString("en-US", {
      // //                 hour: "numeric",
      // //                 minute: "numeric",
      // //                 hour12: true,
      // //               });
      // this.date2 = new Date(this.selectedEvent.start).toISOString().split('T')[0];

      // find out if it's visible or not by first finding the 
      // matching the event server object but make sure to call the API if you can't find the object because it was created too recently
      
      // for (let i = 0; i < this.serverEvents.length; i++) {
      //   if (
      //    new Date(this.serverEvents[i].start_time).getTime() == new Date(this.selectedEvent.start).getTime() &&
      //     new Date(this.serverEvents[i].end_time).getTime() == new Date(this.selectedEvent.end).getTime() &&
      //     this.serverEvents[i].event_name == this.selectedEvent.name
      //   ) {
      //     this.selectedEventID = this.serverEvents[i].event_id;
      //     this.selectedEventIndex = i;
      //     this.editedEvent.is_visible_online = this.serverEvents[i].is_visible_online;
      //   }
      //   if (i+1 == this.this.serverEvents.length) {
      //     this.getAllEvents();
      //     this.showEvent();
      //   }
      // } 

      

      if (this.selectedEvent.color == "blue") {
        this.editedEvent.color = "#3388FF"
      }

     


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
      this.event.start_time = "";
      this.event.end_time = "";
      this.event.color = "#3388FF";
      this.event.is_visible_online = true;
    },
    submit() {
      // check if the form is complete 
      this.checkCreateForm() 
      if (this.createFormIncomplete == false) {
        
      // first grab the AM PM times and turn them into date objects

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
        this.date + "T" + this.event.start_time
      );
      this.event.end_time = new Date(
        this.date + "T" + this.event.end_time
      );

      // then call the service

      eventService.createEvent(this.event).then((response) => {
        if (response.status == 201) {
          alert("Successfully created event");
          this.getAllEvents();
          this.close();
        } else {
          alert("Failed to create event");
        }
      });
      } 
    },
  },
  created() {
    this.getAllClasses();
    this.getAllEvents();

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
        for (let index = 0; index < this.allTimes.length-1; index++) {
          if (this.event.start_time == this.allTimes[index]) {
            foundMatch = true;
          }
          if (foundMatch) {
            datesToReturn.push(this.allTimes[index+1])
          }         
        }
        return datesToReturn
      } else {
        return this.allTimes
      }
    }
  }
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