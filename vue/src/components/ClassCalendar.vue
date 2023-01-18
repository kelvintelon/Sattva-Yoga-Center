<template>
  <div>
    <v-sheet tile height="54" class="d-flex">
      <v-btn icon class="ma-2" @click="$refs.calendar.prev()">
        <v-icon>mdi-chevron-left</v-icon>
      </v-btn>
      <v-select
        v-model="type"
        :items="types"
        dense
        outlined
        hide-details
        class="ma-2"
        label="type"
      ></v-select>
      <v-select
        v-model="mode"
        :items="modes"
        dense
        outlined
        hide-details
        label="event-overlap-mode"
        class="ma-2"
      ></v-select>
      <v-select
        v-model="weekday"
        :items="weekdays"
        dense
        outlined
        hide-details
        label="weekdays"
        class="ma-2"
      ></v-select>
      <v-spacer></v-spacer>
      <v-btn icon class="ma-2" @click="$refs.calendar.next()">
        <v-icon>mdi-chevron-right</v-icon>
      </v-btn>
    </v-sheet>
    <v-sheet height="600">
      <v-calendar
        ref="calendar"
        v-model="value"
        :weekdays="weekday"
        :type="type"
        :events="events"
        :event-overlap-mode="mode"
        :event-overlap-threshold="30"
        :event-color="getEventColor"
        @change="getEvents"
      ></v-calendar>
    </v-sheet>
  </div>
</template>

<script>
import classDetailService from "../services/ClassDetailService";
import eventService from "../services/EventService";

export default {
  name: "class-calendar",
  components: {},
  data: () => ({
    type: "month",
    types: ["month", "week", "day", "4day"],
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
};
</script>

<style>
</style>