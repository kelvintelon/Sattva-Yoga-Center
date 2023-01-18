<template>
  <v-container>
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
            <v-toolbar-title v-if="$refs.calendar">
              {{ $refs.calendar.title }}
            </v-toolbar-title>
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
                  <v-icon>mdi-heart</v-icon>
                </v-btn>
                <v-btn icon>
                  <v-icon>mdi-dots-vertical</v-icon>
                </v-btn>
              </v-toolbar>
              <v-card-text>
                <span>{{new Date(selectedEvent.start).getFullYear()}}-{{new Date(selectedEvent.start).getMonth()+1}}-{{new Date(selectedEvent.start).getDate()}}</span>
                <br>
                <span>Starts at: {{new Date(selectedEvent.start).toLocaleString('en-US', { hour: 'numeric', minute: 'numeric', hour12: true })}} </span>
                <br>
                <span>Ends at: {{new Date(selectedEvent.end).toLocaleString('en-US', { hour: 'numeric', minute: 'numeric', hour12: true })}}</span>
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
    names: [
      "Meeting",
      "Holiday",
      "PTO",
      "Travel",
      "Event",
      "Birthday",
      "Conference",
      "Party",
    ],
    typeToLabel: {
      month: "Month",
      week: "Week",
      day: "Day",
    },
    selectedEvent: {},
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
    eventList: [],
    eventQuantity: "",
  }),
  methods: {
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
  },
  created() {
    this.getAllClasses();
    this.getAllEvents();
  },
  mounted() {
    this.$refs.calendar.checkChange();
  },
};
</script>

<style>
</style>