<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1 v-if="$store.state.user.username != 'admin'">Purchase History</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />

    <v-data-table
      :headers="headers"
      :items="packageHistoryList"
      class="elevation-5"
      sort-by="date_purchased"
      sort-desc="[true]"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Purchase History</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-dialog v-model="dialog" max-width="500px">
            <v-card justify="center">
              <v-card-title>
                <span class="text-h5"> Edit Package </span>
              </v-card-title>

              <v-container>
                <v-row justify="center" style="min-height: 160px">
                  <v-col cols="6">
                    <v-form
                      ref="form"
                      height="100"
                      width="500"
                      v-model="valid"
                      lazy-validation
                      class="class-form mx-auto white"
                      @submit.prevent="update"
                      justify="center"
                      align="center"
                    >
                      <v-text-field
                        v-model="editedItem.package_description"
                        label="Package Description"
                        required
                      ></v-text-field>
                      <v-select
                        v-model.number="editedItem.classes_remaining"
                        :items="classesAmountOptions"
                        label="Amount of Classes"
                        required
                      ></v-select>
                      <v-row>
                        <v-col>
                          <v-menu
                            v-model="editMenu2"
                            :close-on-content-click="false"
                            :nudge-right="40"
                            transition="scale-transition"
                            offset-y
                            min-width="auto"
                          >
                            <template v-slot:activator="{ on, attrs }">
                              <v-text-field
                                label="Activation Date"
                                v-model="editedItem.activation_date"
                                v-bind="attrs"
                                v-on="on"
                              ></v-text-field>
                            </template>
                            <v-date-picker
                              v-model="editedItem.activation_date"
                              @input="editMenu = false"
                            ></v-date-picker> </v-menu
                        ></v-col>
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
                                label="Expiration Date"
                                v-model="editedItem.expiration_date"
                                v-bind="attrs"
                                v-on="on"
                              ></v-text-field>
                            </template>
                            <v-date-picker
                              v-model="editedItem.expiration_date"
                              @input="editMenu2 = false"
                            ></v-date-picker> </v-menu
                        ></v-col>
                      </v-row>
                      <v-row
                        ><v-checkbox
                          v-model="editedItem.is_monthly_renew"
                          label="Renew Monthly?"
                          required
                        ></v-checkbox
                      ></v-row>
                      <v-row justify="center" align="center">
                        <v-col>
                          <v-btn class="mr-4" type="submit">
                            update
                          </v-btn></v-col
                        ></v-row
                      >
                    </v-form>
                  </v-col>
                </v-row>
              </v-container>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <!-- <v-btn color="blue darken-1" text @click="save"> Save </v-btn> -->
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <template v-slot:[`item.is_monthly_renew`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_monthly_renew"
          disabled
        ></v-simple-checkbox>
      </template>
      <template
        v-slot:[`item.actions`]="{ item }"
        v-if="$store.state.user.username == 'admin'"
      >
        <v-icon small class="mr-2" @click="editItem(item)"> mdi-pencil </v-icon>
      </template>
    </v-data-table>
    <br />
    <br />
  </v-container>
</template>

<script>
import packagePurchaseService from "../services/PackagePurchaseService";

export default {
  name: "client-purchase-history-table",
  components: {},
  data() {
    return {
      headers: [
        {
          text: "Package Description",
          align: "start",
          value: "package_description",
        },
        { text: "Purchase Date", value: "date_purchased", sortable: true },
        {
          text: "Total Cost",
          value: "total_amount_paid",
        },
        {
          text: "Activation Date",
          value: "activation_date",
          sortable: true,
        },
        {
          text: "Expiration Date",
          value: "expiration_date",
          sortable: true,
        },
        {
          text: "Classes Remaning",
          value: "classes_remaining",
          sortable: true,
        },
        {
          text: "Monthly Renewal?",
          value: "is_monthly_renew",
        },
      ],
      packageHistoryList: [],
      packagePurchase: {
        package_purchase_id: "",
        client_id: "",
        date_purchased: "",
        package_id: "",
        classes_remaining: "",
        activation_date: "",
        expiration_date: "",
        total_amount_paid: "",
        is_monthly_renew: "",
        discount: "",
        package_description: "",
      },
      // EDIT FORM PROPERTIES
      dialog: false,
      editedItem: {},
      editedIndex: "",
      date2: "",
      classesAmountOptions: [
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        20,
      ],
      editMenu: false,
      editMenu2: false,
      timeRules: [(v) => !!v || "Time is required"],
      allTimes: [
        "12:00 AM",
        "12:15 AM",
        "12:30 AM",
        "12:45 AM",
        "1:00 AM",
        "1:15 AM",
        "1:30 AM",
        "1:45 AM",
        "2:00 AM",
        "2:15 AM",
        "2:30 AM",
        "2:45 AM",
        "3:00 AM",
        "3:15 AM",
        "3:30 AM",
        "3:45 AM",
        "4:00 AM",
        "4:15 AM",
        "4:30 AM",
        "4:45 AM",
        "5:00 AM",
        "5:15 AM",
        "5:30 AM",
        "5:45 AM",
        "6:00 AM",
        "6:15 AM",
        "6:30 AM",
        "6:45 AM",
        "7:00 AM",
        "7:15 AM",
        "7:30 AM",
        "7:45 AM",
        "8:00 AM",
        "8:15 AM",
        "8:30 AM",
        "8:45 AM",
        "9:00 AM",
        "9:15 AM",
        "9:30 AM",
        "9:45 AM",
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
        "1:00 PM",
        "1:15 PM",
        "1:30 PM",
        "1:45 PM",
        "2:00 PM",
        "2:15 PM",
        "2:30 PM",
        "2:45 PM",
        "3:00 PM",
        "3:15 PM",
        "3:30 PM",
        "3:45 PM",
        "4:00 PM",
        "4:15 PM",
        "4:30 PM",
        "4:45 PM",
        "5:00 PM",
        "5:15 PM",
        "5:30 PM",
        "5:45 PM",
        "6:00 PM",
        "6:15 PM",
        "6:30 PM",
        "6:45 PM",
        "7:00 PM",
        "7:15 PM",
        "7:30 PM",
        "7:45 PM",
        "8:00 PM",
        "8:15 PM",
        "8:30 PM",
        "8:45 PM",
        "9:00 PM",
        "9:15 PM",
        "9:30 PM",
        "9:45 PM",
        "10:00 PM",
        "10:15 PM",
        "10:30 PM",
        "10:45 PM",
        "11:00 PM",
        "11:15 PM",
        "11:30 PM",
      ],
    };
  },
  created() {
    this.getPackageHistoryTable();

    this.$root.$refs.B = this;

    if (this.$store.state.user.username == "admin") {
      this.headers.push({ text: "Edit", value: "actions", sortable: false });
    }
  },
  methods: {
    getPackageHistoryTable() {
      if (this.$store.state.user.username == "admin") {
        packagePurchaseService
          .getUserPurchasedPackagesByClientId(this.$route.params.clientId)
          .then((response) => {
            if (response.status == 200) {
              this.packageHistoryList = response.data;
              this.packageHistoryList.forEach((item) => {
                item.date_purchased = new Date(
                  item.date_purchased
                ).toLocaleString();
              });
              this.$store.commit(
                "SET_PACKAGE_HISTORY_LIST",
                this.packageHistoryList
              );
            } else {
              alert("Error retrieving package information");
            }
          });
      } else {
        packagePurchaseService.getUserPurchasedPackages().then((response) => {
          if (response.status == 200) {
            this.packageHistoryList = response.data;
            this.packageHistoryList.forEach((item) => {
              item.date_purchased = new Date(
                item.date_purchased
              ).toLocaleString();
            });
            this.$store.commit(
              "SET_PACKAGE_HISTORY_LIST",
              this.packageHistoryList
            );
          } else {
            alert("Error retrieving package information");
          }
        });
      }
    },
    close() {
      this.dialog = false;
    },
    editItem(item) {
      this.editedItem = Object.assign({}, item);
      this.editedIndex = this.packageHistoryList.indexOf(item);
      this.dialog = true;
    },
    update() {
      this.editedItem.date_purchased = new Date(
        this.editedItem.date_purchased.replace("-", "/")
      ).toJSON();
      if (this.editedItem.activation_date != null) {
        this.editedItem.activation_date = new Date(
          this.editedItem.activation_date.replace("-", "/")
        ).toJSON();
      }
      if (this.editedItem.expiration_date != null) {
        this.editedItem.expiration_date = new Date(
          this.editedItem.expiration_date.replace("-", "/")
        ).toJSON();
      }
      packagePurchaseService
        .updatePackagePurchase(this.editedItem)
        .then((response) => {
          if (response.status == 200) {
            response;
            alert("success");
            this.close();
            this.$root.$refs.A.getActivePurchaseServerRequest();
            this.getPackageHistoryTable();
          }
        });
    },
  },
};
</script>

<style>
</style>