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
        sort-by="date"
        :sort-desc="[true]"
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>My Upcoming Classes</v-toolbar-title>
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
import clientDetailService from "../services/ClientDetailService";

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
        },
        { text: "Date", value: "date", sortable: true, align: "start" },
        { text: "Start Time", value: "start_time", sortable: false },
        { text: "End Time", value: "end_time", sortable: false },
        { text: "Cancel Signup", value: "actions", sortable: false },
      ],
      allClientEventHeaders: [
        {
          text: "Class Description",
          value: "event_name",
        },
        { text: "Date", value: "date", sortable: true, align: "start" },
        { text: "Start Time", value: "start_time", sortable: false },
        { text: "End Time", value: "end_time", sortable: false },
      ],
      events: [],
      clientEvents: [],
      allClientEvents: [],
      eventClientSignUp: {
        event_id: "",
        date: "",
        client_id: "",
        package_purchase_id: "",
      },
      validSignUp: true,
      allowSignUp: false,
      activePackageList: [],
      snackBarNoPurchaseWarning: false,
      classSignUpItem: {},
      packages: [],
      allHistoricalPackages:[],
      hasSubscriptionPackage: false,
      subscriptionPackages: [],
      quantityPackages: [],
      quantityPackageIdToDecrement: 0,
      initial: 0,
      initial1: 0,
    };
  },
  methods: {
    SignUp(item) {
      this.eventClientSignUp.event_id = item.event_id;
      this.eventClientSignUp.date = item.dateRef;
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
          var today = new Date();
          var dd = String(today.getDate()).padStart(2, "0");
          var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
          var yyyy = today.getFullYear();
          today = yyyy + "-" + mm + "-" + dd;

          this.packages = response.data.filter((item) => {
            return item.expiration_date >= today || item.classes_remaining > 0;
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
            if (item.is_subscription) {
              this.hasSubscriptionPackage = true;
              this.subscriptionPackages =
                this.$store.state.activePackageList.filter((item) => {
                  return item.is_subscription;
                });
              this.initial1 = this.subscriptionPackages[0];
              this.subscriptionPackages.forEach((item) => {
                if (item.expiration_date > this.initial1.expiration_date) {
                  this.initial1 = item;
                }
              });
              this.eventClientSignUp.package_purchase_id =
                this.initial1.package_purchase_id;
            } else {
              this.quantityPackages =
                this.$store.state.activePackageList.filter((item) => {
                  return item.is_subscription == false && (item.expiration_date == null || todaysDate < item.expiration_date);
                });
              this.initial = this.quantityPackages[0];
              this.quantityPackages.forEach((item) => {
                if (item.date_purchased < this.initial.date_purchased) {
                  this.initial = item;
                }
                this.quantityPackageIdToDecrement =
                  this.initial.package_purchase_id;
                this.eventClientSignUp.package_purchase_id =
                  this.quantityPackageIdToDecrement;
              });
            }
          }
        });

        // this here is if they only have a gift certificate or dont have a bundle/subscription
        if (!this.allowSignUp) {
          this.snackBarNoPurchaseWarning = true;
        }
      }

      // if they have an active package then they are allowed to sign up
      if (this.allowSignUp) {
        // console.log(this.eventClientSignUp.date)
        // console.log(this.initial1.expiration_date)
        // console.log(this.eventClientSignUp.date > this.initial1.expiration_date)
        // console.log(this.hasSubscriptionPackage)
        this.clientEvents.forEach((item) => {
          if (item.event_id == this.eventClientSignUp.event_id) {
            alert("You have already signed up for this class!");
            this.validSignUp = false;
          }
          if (
            this.hasSubscriptionPackage &&
            this.eventClientSignUp.date > this.initial1.expiration_date
          ) {
            alert("Error! Your unlimited package will be expired by then.");
            this.validSignUp = false;
          }
        });
        if (this.validSignUp == true) {
          eventService
            .registerForEvent(this.eventClientSignUp)
            .then((response) => {
              if (response.status == 201) {
                if (this.hasSubscriptionPackage == false) {
                  packagePurchaseService.decrementByOne(
                    this.quantityPackageIdToDecrement
                  );
                  alert(
                    "You have used your quantity package. Classes remaining reduced by 1."
                  );
                }
                // call method that updates the client_class_table
                // update client.is_new_client to false through mutation
                alert("You have registered for a class");
                this.$store.commit("SET_CLIENT_DETAILS_NEW_CLIENT", false);
                      clientDetailService.getClientDetailsOfLoggedInUser().then((response) => {
        if (response.data.client_id != 0) {
          this.clientProfile = response.data;
          this.$store.commit("SET_CLIENT_DETAILS", response.data);
        }
      });
                this.getClientEventTable();
              }
            });
        }
      }
    },

    RemoveClassForClient(item) {
      this.eventClientSignUp.event_id = item.event_id;
      this.eventClientSignUp.date = item.dateRef;
      this.eventClientSignUp.package_purchase_id = item.package_purchase_id;
      this.eventClientSignUp.client_id =
        this.$store.state.clientDetails.client_id;

      this.allowSignUp = false;

      // object to hold item passed in just in case
      this.classSignUpItem = Object.assign({}, item);

      // get active packages from API service request
      this.getActivePurchaseServerRequest2();

      // Don't try these below at home
      // this.$root.$refs.A.getActivePurchaseServerRequest();
      // this.$root.$emit("getActivePurchasePackageTable");
    },
    getActivePurchaseServerRequest2() {
      packagePurchaseService.getUserPurchasedPackages().then((response) => {
        if (response.status == 200) {
          this.allHistoricalPackages = response.data;
          // focus on if it's expired or not
          var today = new Date();
          var dd = String(today.getDate()).padStart(2, "0");
          var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
          var yyyy = today.getFullYear();
          today = yyyy + "-" + mm + "-" + dd;

          this.packages = response.data.filter((item) => {
            return item.expiration_date >= today || item.classes_remaining > 0;
          });
          this.packages.forEach((item) => {
            item.date_purchased = new Date(item.date_purchased);
          });
          this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
          this.activePackageList = this.$store.state.activePackageList;
          this.cancelCheck();
        } else {
          alert("Error retrieving package information");
        }
      });
    },
    cancelCheck() {
        let refundPackage = this.allHistoricalPackages.filter((item) => {
          return (
            item.package_purchase_id ==
            this.eventClientSignUp.package_purchase_id
          );
        });
        if (refundPackage[0].is_subscription == true) {
          this.hasSubscriptionPackage = true;
        }
        this.allowSignUp = true;

      if (this.allowSignUp) {
        // console.log(this.eventClientSignUp.date)
        // console.log(this.initial1.expiration_date)
        // console.log(this.eventClientSignUp.date > this.initial1.expiration_date)
        // console.log(this.hasSubscriptionPackage)
        if (this.validSignUp == true) {
          eventService
            .removeEventForClient(this.eventClientSignUp.event_id)
            .then((response) => {
              if (response.status == 200) {
                // call method that updates the client_class_table
                alert("Removed the class from your list");

                if (!this.hasSubscriptionPackage) {
                  packagePurchaseService
                    .incrementByOne(this.eventClientSignUp.package_purchase_id)
                    .then((response) => {
                      if (response.status == 200) alert("+1");
                    });
                }
                this.getClientEventTable();
              }
            });
        }
      }
    },

    getEventTable() {
      eventService.get100Events().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_EVENT_LIST", response.data);
          this.events = response.data;
          this.events.forEach((each) => {
            // YYYY-MM-DD format
            each.date = each.start_time;
            each.dateRef = each.start_time;
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

          this.allClientEvents = response.data;

          var today = new Date();
          var dd = String(today.getDate()).padStart(2, "0");
          var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
          var yyyy = today.getFullYear();
          today = yyyy + "-" + mm + "-" + dd;

          this.clientEvents = this.allClientEvents.filter((item) => {
            return item.start_time > today;
          });

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