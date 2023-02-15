<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1>Sign up for classes</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />
    <v-snackbar
      v-model="snackBarNoPurchaseWarning"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
    >
      Warning: You Need An Active Package

      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarNoPurchaseWarning = false"
          left
        >
          Close
        </v-btn>
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="sendThemToPurchasePackage"
        >
          Buy a Package
        </v-btn>
      </template>
    </v-snackbar>
    <v-app>
      <v-data-table :headers="headers" :items="events" class="elevation-5">
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>All Classes</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
          </v-toolbar>
        </template>
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon small class="mr-2" @click="SignUp(item)">
            mdi-account-plus
          </v-icon>
        </template>
      </v-data-table>
      <br />
      <br />
      <v-data-table
        :headers="clientEventHeaders"
        :items="clientEvents"
        class="elevation-5"
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>My Classes</v-toolbar-title>
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
    </v-app>

    <!-- <v-btn elevation="5" v-on:click="buyFirstClass()">
      <v-icon> mdi-cart </v-icon> Sign up For Class
    </v-btn> -->
  </v-container>
</template>

<script>
// import classDetailService from "../services/ClassDetailService";
import eventService from "../services/EventService";
import packagePurchaseService from "../services/PackagePurchaseService";

export default {
  name: "class-registration",
  components: {},
  data() {
    return {
      headers: [
        {
          text: "Class Description",
          value: "event_name",
          align: "start",
        },
        { text: "Date", value: "date", sortable: false },
        { text: "Start Time", value: "start_time", sortable: false },
        { text: "End Time", value: "end_time", sortable: false },
        { text: "Sign Up", value: "actions", sortable: false },
      ],
      clientEventHeaders: [
        {
          text: "Class Description",
          value: "event_name",
          align: "start",
        },
        { text: "Date", value: "date", sortable: false },
        { text: "Start Time", value: "start_time", sortable: false },
        { text: "End Time", value: "end_time", sortable: false },
        { text: "Cancel Signup", value: "actions", sortable: false },
      ],
      events: [],
      clientEvents: [],
      eventClientSignUp: {
        event_id: "",
        client_id: "",
      },
      validSignUp: true,
      allowSignUp: false,
      activePackageList: [],
      snackBarNoPurchaseWarning: false,
      classSignUpItem: {},
      packages: [],
    };
  },
  methods: {
    SignUp(item) {
      this.eventClientSignUp.event_id = item.event_id;
      this.eventClientSignUp.client_id =
        this.$store.state.clientDetails.client_id;

      this.allowSignUp = false;

      // object to hold item passed in just in case
      this.classSignUpItem = Object.assign({}, item);

      // get active packages from API service request
      this.getActivePurchaseServerRequest();

      // Don't try these below at home
      // this.$root.$refs.A.getActivePurchaseServerRequest();
      // this.$root.$emit("getActivePurchasePackageTable");
    },
    getActivePurchaseServerRequest() {
      packagePurchaseService.getUserPurchasedPackages().then((response) => {
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
    RemoveClassForClient(item) {
      eventService.removeEventForClient(item.event_id).then((response) => {
        if (response.status == 200) {
          // call method that updates the client_class_table
          alert("Removed the class from your list");
          this.getClientEventTable();
        }
      });
    },
    getEventTable() {
      eventService.get100Events().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_EVENT_LIST", response.data);
          this.events = response.data;
          this.events.forEach((each) => {
            // YYYY-MM-DD format
            each.date = each.start_time;
            const [Month, Day, Year] = new Date(each.date)
              .toLocaleDateString()
              .split("/");
            each.date = Year + "-" + Month + "-" + Day;
            // HH:MM AM/PM format
            each.start_time = new Date(each.start_time).toLocaleString(
              "en-US",
              {
                hour: "numeric",
                minute: "numeric",
                hour12: true,
              }
            );
            each.end_time = new Date(each.end_time).toLocaleString("en-US", {
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
    getClientEventTable() {
      eventService.getAllClientEvents().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_CLIENT_EVENT_LIST", response.data);
          this.clientEvents = response.data;
          this.clientEvents.forEach((each) => {
            // YYYY-MM-DD format
            each.date = each.start_time;
            const [Month, Day, Year] = new Date(each.date)
              .toLocaleDateString()
              .split("/");
            each.date = Year + "-" + Month + "-" + Day;
            // HH:MM AM/PM format
            each.start_time = new Date(each.start_time).toLocaleString(
              "en-US",
              {
                hour: "numeric",
                minute: "numeric",
                hour12: true,
              }
            );
            each.end_time = new Date(each.end_time).toLocaleString("en-US", {
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
    sendThemToPurchasePackage() {
      this.snackBarNoPurchaseWarning = false;
      this.$router.push({ name: "client-package-management" });
    },
  },
  created() {
    this.getEventTable();
    this.getClientEventTable();
  },
};
</script>

<style>
</style>