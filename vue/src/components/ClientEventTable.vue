<template>
  <v-container>
    <v-data-table
      :headers="clientEventHeaders"
      :items="clientEvents"
      class="elevation-5"
      sort-by="date"
      :sort-desc="[true]"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Upcoming Classes</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon small class="mr-2" @click="RemoveClassForClient(item)">
          mdi-close-thick
        </v-icon>
      </template>
    </v-data-table>
    <br />
    <br />
    <v-data-table
      :headers="allClientEventHeaders"
      :items="allClientEvents"
      class="elevation-5"
      sort-by="date"
      :sort-desc="[true]"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Sign Up History</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
    </v-data-table>
  </v-container>
</template>

<script>
import eventService from "../services/EventService";

export default {
  name: "client-event-table",
  data() {
    return {
        clientEventHeaders: [
        {
          text: "Class Description",
          value: "event_name",
          
        },
        { text: "Date", value: "date", sortable: true, align: "start", },
        { text: "Start Time", value: "start_time", sortable: false },
        { text: "End Time", value: "end_time", sortable: false },
        { text: "Cancel Signup", value: "actions", sortable: false },
      ],
      allClientEventHeaders: [
        {
          text: "Class Description",
          value: "event_name",
        },
        { text: "Date", value: "date", sortable: true, align: "start", },
        { text: "Start Time", value: "start_time", sortable: false },
        { text: "End Time", value: "end_time", sortable: false },
        
      ],
      events: [],
      clientEvents: [],
      allClientEvents: [],
    };
  },
  methods: {
      getClientEventTable() {
      eventService.getAllClientEventsByClientId(this.$route.params.clientId).then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_CLIENT_EVENT_LIST", response.data);
          
          this.allClientEvents = response.data;

          

          var today = new Date();
          var dd = String(today.getDate()).padStart(2, '0');
          var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
          var yyyy = today.getFullYear();
          today = yyyy + '-' + mm + '-' + dd;

          this.clientEvents = this.allClientEvents.filter((item) => {
            return item.start_time > today;
          })

          this.allClientEvents.forEach((item) => {
            // YYYY-MM-DD format
            item.date = item.start_time;
            let [Month, Day, Year] = new Date(item.date)
              .toLocaleDateString()
              .split("/");
            item.date = Year + "-" + Month + "-" + Day;
            // HH:MM AM/PM format
            item.start_time = new Date(item.start_time).toLocaleString(
              "en-US",
              {
                hour: "numeric",
                minute: "numeric",
                hour12: true,
              }
            );
            item.end_time = new Date(item.end_time).toLocaleString("en-US", {
              hour: "numeric",
              minute: "numeric",
              hour12: true,
            });
          });


        } else {
          alert("Error retrieving class information");
        }
      });
    },

  },
  created() {
      this.getClientEventTable();
  }
};
</script>

<style>
</style>