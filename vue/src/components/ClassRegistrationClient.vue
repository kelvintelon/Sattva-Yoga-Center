<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1 style="color: rgba(245, 104, 71, 0.95)">Sign up for classes</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />
    <v-snackbar
      v-model="snackBarNoPurchaseWarning"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
      role="alert"
    >
      Warning: You Need An Active Package

      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarNoPurchaseWarning = false"
          left
          top
          role="button"
        >
          Close
        </v-btn>
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="sendThemToPurchasePackage"
          bottom
          role="button"
        >
          Buy Package
        </v-btn>
      </template>
    </v-snackbar>
    <v-app>
      <v-data-table
        :headers="headers"
        :items="events"
        class="elevation-5"
        dense
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>All Classes</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
          </v-toolbar>
        </template>
        <template v-slot:[`item.actions`]="{ item }">
          <v-btn class="mr-2" @click="SignUp(item)" style="color: rgba(245, 104, 71, 0.95)" role="button" aria-label="Sign up">
            Sign Up
          </v-btn>
        </template>
      </v-data-table>
      <br />
      <br />
      <v-data-table
        :headers="clientEventHeaders"
        :items="clientEvents"
        class="elevation-5"
       
        dense
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>My Upcoming Classes</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
          </v-toolbar>
        </template>
        <template v-slot:[`item.actions`]="{ item }">
          <v-btn class="mr-2" @click="RemoveClassForClient(item)" role="Cancel class button" style="color: rgba(245, 104, 71, 0.95)">
            Cancel
          </v-btn>
        </template>
      </v-data-table>
      <br />
      <br />
      <v-data-table
        :headers="allClientEventHeaders"
        :items="allClientEvents"
        class="elevation-5"
        :sort-by="date"
        :sort-desc="[true]"
        dense
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
        { text: "Date", value: "date", sortable: true },
        { text: "Start Time", value: "start_time", sortable: false },
        { text: "End Time", value: "end_time", sortable: false },
        { text: "Sign Up", value: "actions", sortable: false },
      ],
      clientEventHeaders: [
        {
          text: "Class Description",
          value: "event_name",
        },
        { text: "Date", value: "date", sortable: true},
        { text: "Start Time", value: "start_time", sortable: false },
        { text: "End Time", value: "end_time", sortable: false },
        { text: "Cancel Signup", value: "actions", sortable: false },
      ],
      allClientEventHeaders: [
        {
          text: "Class Description",
          value: "event_name",
        },
        { text: "Date", value: "date", sortable: true },
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
      sharedPackages: [],
      allHistoricalPackages: [],
      hasSubscriptionPackage: false,
      subscriptionPackages: [],
      quantityPackages: [],
      quantityPackageIdToDecrement: 0,
      initial: 0,
      initial1: 0,
      freeloading: false,
      freeClass: false,
    };
  },
  methods: {
    SignUp(item) {
      this.eventClientSignUp.event_id = item.event_id;
      this.eventClientSignUp.date = item.dateRef;
      this.eventClientSignUp.client_id =
        this.$store.state.clientDetails.client_id;
      this.freeClass = item.is_paid;
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
      packagePurchaseService.getActiveUserPurchasedPackages().then((response) => {
        if (response.status == 200) {
          this.getSharedActivePackages();
          // focus on if it's expired or not
          var today = new Date();
          var dd = String(today.getDate()).padStart(2, "0");
          var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
          var yyyy = today.getFullYear();
          today = yyyy + "-" + mm + "-" + dd;

          this.packages = response.data.filter((item) => {
            // return item.expiration_date >= today || item.classes_remaining > 0;
            return (
              (item.unlimited && item.expiration_date >= today) ||
              (!item.unlimited &&
                item.expiration_date >= today &&
                item.classes_remaining > 0)
            );
          });
          this.packages.forEach((item) => {
            item.date_purchased = new Date(item.date_purchased);
            // Expiration date format here different from clientActivePackageTable->getActivePurchaseServerRequest
            item.expiration_date = new Date(item.expiration_date);
          });
          this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
          this.activePackageList = this.$store.state.activePackageList;
          this.formattingSignUp();
        } else {
          alert("Error retrieving package information");
        }
      });
    },
    getSharedActivePackages() {
      packagePurchaseService
        .getAllSharedActiveQuantityPackages()
        .then((response) => {
          if (response.status == 200) {
            this.sharedPackages = response.data;
          }
          this.$store.commit("SET_SHARED_PACKAGE_LIST", this.sharedPackages);
        });
    },
    formattingSignUp() {
      // find out if they have at least one active package that's a subscription or a bundle and active
      // this.activePackageList = this.$store.state.activePackageList;
      if (
        this.$store.state.activePackageList.length == 0 &&
        this.$store.state.sharedPackages.length == 0 &&
        this.freeClass == false
      ) {
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
            if (item.unlimited) {
              this.hasSubscriptionPackage = true;
              this.subscriptionPackages =
                this.$store.state.activePackageList.filter((item) => {
                  return item.unlimited;
                });
              this.initial1 = this.subscriptionPackages[0];
              this.subscriptionPackages.forEach((item) => {
                if (item.expiration_date < this.initial1.expiration_date) {
                  this.initial1 = item;
                }
              });
              this.eventClientSignUp.package_purchase_id =
                this.initial1.package_purchase_id;
            } else {
              this.quantityPackages =
                this.$store.state.activePackageList.filter((item) => {
                  //
                  return (
                    item.unlimited == false &&
                    (item.expiration_date == null ||
                      todaysDate < item.expiration_date) &&
                    item.classes_remaining > 0
                  );
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
          // final package_id is always the subscription package with the earliest exp date.
          if (this.hasSubscriptionPackage) {
            this.eventClientSignUp.package_purchase_id =
              this.initial1.package_purchase_id;
          }
        });

        // this here is if they only have a gift certificate or dont have a bundle/subscription
        if (!this.allowSignUp) {
          this.snackBarNoPurchaseWarning = true;
        }
      } else if (this.$store.state.sharedPackages.length > 0) {
        if (this.$store.state.sharedPackages[0].classes_remaining > 0) {
          this.allowSignUp = true;
        }
        this.eventClientSignUp.package_purchase_id =
          this.$store.state.sharedPackages[0].package_purchase_id;
        this.quantityPackageIdToDecrement =
          this.$store.state.sharedPackages[0].package_purchase_id;
        this.freeloading = true;
      } else if (this.freeClass == true) {
        this.allowSignUp = true;
        this.eventClientSignUp.package_purchase_id = -1;
      }

      // if they have an active package then they are allowed to sign up
      if (this.allowSignUp) {
        // console.log(this.eventClientSignUp.date)
        // console.log(this.initial1.expiration_date)
        // console.log(this.eventClientSignUp.date > this.initial1.expiration_date)
        // console.log(this.hasSubscriptionPackage)
        // console.log(this.eventClientSignUp)
        this.clientEvents.forEach((item) => {
          if (item.event_id == this.eventClientSignUp.event_id) {
            alert("You have already signed up for this class!");
            this.validSignUp = false;
          }
          if (
            this.hasSubscriptionPackage &&
            this.eventClientSignUp.date > this.initial1.expiration_date && this.freeClass == false
          ) {
            alert("Error! Your unlimited package will be expired by then.");
            this.validSignUp = false;
          }
        });

        if (this.freeloading && this.freeClass == false) {
          if (
            confirm("You will use up packages shared by the group") == false
          ) {
            this.validSignUp = false;
          }
        }

        if (this.validSignUp == true) {
          eventService
            .registerForEvent(this.eventClientSignUp)
            .then((response) => {
              if (response.status == 201) {
                if (this.hasSubscriptionPackage == false && this.freeClass == false) {
                  packagePurchaseService.decrementByOne(
                    this.quantityPackageIdToDecrement
                  );
                  this.getSharedActivePackages();
                  this.freeloading = false;
                  this.quantityPackageIdToDecrement = "";
                  alert(
                    "You have used your quantity package. Classes remaining reduced by 1."
                  );
                }
                // call method that updates the client_class_table
                // update client.is_new_client to false through mutation
                alert("You have registered for a class");
                this.$store.commit("SET_CLIENT_DETAILS_NEW_CLIENT", false);
                clientDetailService
                  .getClientDetailsOfLoggedInUser()
                  .then((response) => {
                    if (response.data.client_id != 0) {
                      this.clientProfile = response.data;
                      this.$store.commit("SET_CLIENT_DETAILS", response.data);
                    }
                  });
                this.getEventTable();
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
      if (item.package_purchase_id <= 0) {
        this.allowSignUp = true;
        alert("Success");
        this.cancelCheck();
      } else {
        this.getActivePurchaseServerRequest2();
      }

      // Don't try these below at home
      // this.$root.$refs.A.getActivePurchaseServerRequest();
      // this.$root.$emit("getActivePurchasePackageTable");
    },
    getActivePurchaseServerRequest2() {
      packagePurchaseService.getActiveUserPurchasedPackages().then((response) => {
        if (response.status == 200) {
          this.allHistoricalPackages = response.data;
          // focus on if it's expired or not
          var today = new Date();
          var dd = String(today.getDate()).padStart(2, "0");
          var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
          var yyyy = today.getFullYear();
          today = yyyy + "-" + mm + "-" + dd;

          this.packages = response.data.filter((item) => {
            // return item.expiration_date >= today || item.classes_remaining > 0;
            return (
              (item.unlimited && item.expiration_date >= today) ||
              (!item.unlimited &&
                item.expiration_date >= today &&
                item.classes_remaining > 0)
            );
          });
          this.packages.forEach((item) => {
            item.date_purchased = new Date(item.date_purchased);
          });
          this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
          this.activePackageList = this.$store.state.activePackageList;
          let refundPackage = this.allHistoricalPackages.filter((item) => {
            return (
              item.package_purchase_id ==
              this.eventClientSignUp.package_purchase_id
            );
          });
          //console.log(refundPackage);
          if (refundPackage.length > 0) {
            if (refundPackage[0].unlimited == true) {
              this.hasSubscriptionPackage = true;
            }
          }
          this.allowSignUp = true;
          this.cancelCheck();
        } else {
          alert("Error retrieving package information");
        }
      });
    },
    cancelCheck() {
      if (this.allowSignUp || this.eventClientSignUp.package_purchase_id <= 0) {
        //console.log(this.hasSubscriptionPackage);
        if (
          this.validSignUp == true ||
          this.eventClientSignUp.package_purchase_id <= 0
        ) {
          eventService
            .removeEventForClient(this.eventClientSignUp.event_id)
            .then((response) => {
              if (response.status == 200) {
                // call method that updates the client_class_table
                alert("Removed the class from your list");
                if (
                  !this.hasSubscriptionPackage &&
                  this.eventClientSignUp.package_purchase_id > 0
                ) {
                  packagePurchaseService
                    .incrementByOne(this.eventClientSignUp.package_purchase_id)
                    .then((response) => {
                      if (response.status == 200)
                        alert("Package Incremented +1");
                    });
                }
                this.hasSubscriptionPackage = false;
                this.getClientEventTable();
                this.getEventTable();
              }
            });
        }
      }
    },

    getEventTable() {
      eventService
        .get100EventsForClient(this.$store.state.clientDetails.client_id)
        .then((response) => {
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