<template>
  <v-container>
    <v-data-table
      :headers="clientEventHeaders"
      :items="clientEvents"
      class="elevation-5"
      sort-by="date"
      :sort-desc="[true]"
      dense
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Upcoming Classes</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-dialog
            v-model="dialog"
            max-width="500px"
            v-if="$store.state.user.username == 'admin'"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-btn
                color="primary"
                dark
                class="mb-2"
                v-bind="attrs"
                v-on="on"
                @click="get100Events"
              >
                Add a class
              </v-btn>
            </template>
            <v-card>
              <!-- Add a Class Form starts here -->
              <v-card-title>
                <span class="text-h5">Add a class</span>
              </v-card-title>

              <v-card-title>
                <v-container>
                  <v-row>
                    <v-col>
                      <v-select
                        label="Choose one or multiple"
                        :items="availableClasses"
                        v-model="selectedClass"
                        return-object
                      ></v-select>
                      <!--<v-row>
                        <v-col>
                          <v-text-field
                            v-if="!showPercentDiscount"
                            v-model="selectedPackage.discount"
                            class="mt-0 pt-0"
                            type="number"
                            label="Discount: $"
                            min="0"
                          ></v-text-field>
                          <v-text-field
                            v-if="showPercentDiscount"
                            v-model="percentDiscount"
                            class="mt-0 pt-0"
                            type="number"
                            label="Discount: %"
                            min="0"
                          ></v-text-field
                        ></v-col>
                        <v-col>
                          <v-btn
                            @click="showPercentDiscount = true"
                            v-if="!showPercentDiscount"
                            ><v-icon>mdi-percent</v-icon></v-btn
                          >
                          <v-btn
                            @click="showPercentDiscount = false"
                            v-if="showPercentDiscount"
                            ><v-icon>mdi-currency-usd</v-icon></v-btn
                          >
                        </v-col></v-row
                      >
                      <div class="text--primary">
                        Package Cost: ${{ selectedPackage.package_cost }}
                      </div>
                      <div class="text--primary">
                        Package Discount: -${{ returnDiscount }}
                      </div>
                      <div class="text--primary" style="border-top: 1px solid">
                        Total Cost: ${{ returnTotal }}
                      </div> -->
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-title>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <v-btn color="blue darken-1" text @click="addClassForClient">
                  Save
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
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
      dense
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Sign Up History</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
      <template
        v-slot:[`item.actions`]="{ item }"
        v-if="$store.state.user.username == 'admin'"
      >
        <v-icon small class="mr-2" @click="RemoveClassForClient(item)">
          mdi-close-thick
        </v-icon>
      </template>
    </v-data-table>
  </v-container>
</template>

<script>
import eventService from "../services/EventService";
// import clientDetailService from "../services/ClientDetailService";
import packagePurchaseService from "../services/PackagePurchaseService";

export default {
  name: "client-event-table",
  data() {
    return {
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
      eventClientSignUp: {},
      allowSignUp: false,
      validSignUp: true,
      classSignUpItem: {},
      allHistoricalPackages: [],
      packages: [],
      hasSubscriptionPackage: false,
      activePackageList: [],
      availableClasses: [],
      selectedClass: {},
      dialog: false,
    };
  },
  methods: {
    getClientEventTable() {
      eventService
        .getAllClientEventsByClientId(this.$route.params.clientId)
        .then((response) => {
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
    get100Events() {
      eventService.get100Events().then((response) => {
        if (response.status == 200) {
          this.availableClasses = response.data;
        }
      });
    },
    close() {
      this.selectedClass = {};
      this.dialog = false;
    },
    RemoveClassForClient(item) {
      this.eventClientSignUp.event_id = item.event_id;
      this.eventClientSignUp.date = item.dateRef;
      // retrieve the package_purchase id correctly
      this.eventClientSignUp.package_purchase_id = item.package_purchase_id;
      this.eventClientSignUp.client_id = this.$route.params.clientId;

      // retrieve the package_purchase id correctly

      this.allowSignUp = false;

      // object to hold item passed in just in case
      this.classSignUpItem = Object.assign({}, item);

      // get active packages from API service request
      if (this.eventClientSignUp.package_purchase_id == 0) {
        this.allowSignUp = true;

        this.cancelCheck();
      } else {
        this.getActivePurchaseServerRequest2();
      }
    },
    getActivePurchaseServerRequest2() {
      packagePurchaseService
        .getUserPurchasedPackagesByClientId(this.eventClientSignUp.client_id)
        .then((response) => {
          if (response.status == 200) {
            this.allHistoricalPackages = response.data;
            // focus on if it's expired or not
            var today = new Date();
            var dd = String(today.getDate()).padStart(2, "0");
            var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
            var yyyy = today.getFullYear();
            today = yyyy + "-" + mm + "-" + dd;

            this.packages = response.data.filter((item) => {
              return (
                item.expiration_date >= today || item.classes_remaining > 0
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
            if (refundPackage[0].is_subscription == true) {
              this.hasSubscriptionPackage = true;
            }
            this.allowSignUp = true;
            this.cancelCheck();
          } else {
            alert("Error retrieving package information");
          }
        });
    },
    cancelCheck() {
      if (this.allowSignUp || this.eventClientSignUp.package_purchase_id == 0) {
        // console.log(this.eventClientSignUp.date)
        // console.log(this.initial1.expiration_date)
        // console.log(this.eventClientSignUp.date > this.initial1.expiration_date)
        // console.log(this.hasSubscriptionPackage)
        this.validSignUp = true;
        if (
          this.validSignUp == true ||
          this.eventClientSignUp.package_purchase_id == 0
        ) {
          eventService
            .removeEventForClientByClientId(
              this.eventClientSignUp.event_id,
              this.$route.params.clientId
            )
            .then((response) => {
              if (response.status == 200) {
                // call method that updates the client_class_table

                if (
                  !this.hasSubscriptionPackage &&
                  this.eventClientSignUp.package_purchase_id > 0
                ) {
                  packagePurchaseService
                    .incrementByOne(this.eventClientSignUp.package_purchase_id)
                    .then((response) => {
                      if (response.status == 200) {
                        response;
                        alert("Removed Class from your schedule");
                        // this.listOfSignedUpClients.splice(
                        //   this.indexOfClientToBeDeleted,
                        //   1
                        // );
                      }
                    });
                } else {
                  alert("Removed Class from your schedule");
                  // this.listOfSignedUpClients.splice(
                  //   this.indexOfClientToBeDeleted,
                  //   1
                  // );
                }
                this.getClientEventTable();
                this.$root.$refs.A.getActivePurchaseServerRequest();
                this.$root.$refs.B.getPackageHistoryTable();
              }
            });
        }
      }
    },
  },
  created() {
    this.getClientEventTable();

    if (this.$store.state.user.username == "admin") {
      this.clientEventHeaders.unshift({
        text: "Event ID",
        value: "event_id",
        sortable: false,
      });
      this.allClientEventHeaders.unshift({
        text: "Event ID",
        value: "event_id",
        sortable: false,
      });
      this.allClientEventHeaders.push({
        text: "Remove From History",
        value: "actions",
        sortable: false,
      });
    }
  },
};
</script>

<style>
</style>