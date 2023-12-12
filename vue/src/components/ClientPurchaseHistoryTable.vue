<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1 v-if="$store.state.user.username != 'admin'" style="color: rgba(245, 104, 71, 0.95)">Purchase History</h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />

    <v-data-table
      :headers="headers"
      :items="packageHistoryList"
      class="elevation-5"
      :sort-by.sync="sortBy"
      :sort-desc.sync="sortDesc"
      @update:sort-by="sortTable"
      @update:sort-desc="sortTable"
       :loading="loading"
        loading-text="Loading... Please wait"
      :options.sync="options"
      :server-items-length="totalPackagesPurchased"
      hide-default-footer
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Purchase History</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-dialog v-model="dialog" max-width="500px" persistent>
            <v-card justify="center">
              <v-card-title>
                <span class="text-h5" style="color: rgba(245, 104, 71, 0.95)"> Edit Package </span>
              </v-card-title>

              <v-container>
                <v-row justify="center" style="min-height: 160px">
                  <v-col cols="8">
                    <v-form
                      ref="form"
                      height="100"
                      width="1000"
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
                      <!-- <v-row
                        ><v-checkbox
                          v-model="editedItem.is_monthly_renew"
                          label="Renew Monthly?"
                          required
                        ></v-checkbox
                      ></v-row> -->
                      <v-row justify="center" align="center">
                        <v-col>
                          <v-btn color="blue darken-1" outlined class="mr-4" type="submit">
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
                <v-btn color="red" text @click="close">
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
        <v-icon large class="mr-2" @click="editItem(item)"> mdi-pencil </v-icon>
        <v-icon large class="mr-2" @click.stop="resendEmailForm(item)"> mdi-email </v-icon>
      </template>
    </v-data-table>
    <v-row>
      <v-col lg="10" md="9" sm="8">
        <v-pagination
          v-model="page"
          :length="Math.ceil(totalPackagesPurchased / pageSize)"
          @input="temporaryPageMethod"
          total-visible="8"
        ></v-pagination>
      </v-col>
      <v-col lg="2" md="3" sm="3" class="mt-2">
        <v-select
          v-model="pageSize"
          :items="[10, 20, 30, 40, 50]"
          outlined
          filled
          @change="temporaryPageSizeMethod"
        >
        </v-select>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
    <br />
    <br />
    <v-overlay :value="overlay">
      <v-progress-circular
        indeterminate
        size="70"
      ></v-progress-circular>
    </v-overlay>
    <v-row justify="center">
    <v-dialog
      v-model="showEmailForm"
      persistent
      max-width="400"
    >
      <v-card>
        <v-card-title class="text-h5" style="color: rgba(245, 104, 71, 0.95)">
          Resend Email
        </v-card-title>
        <v-text-field class="ml-4 mr-4"
                      v-model="resendEmailObj.email"
                     
                      label="Email for Receipt"
                    ></v-text-field>
        <v-card-actions>
          <v-spacer></v-spacer>
          
          <v-btn
            color="primary"
            text
            @click.stop="resendEmailToClient"
          >
            Send
          </v-btn>
          <v-btn
            color="red"
            text
            @click="showEmailForm = false"
          >
            Cancel
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
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
          value: "package_description",
          sortable: false,
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
          text: "Payment",
          value: "payment_description",
          sortable: false,
        },
        {
          text: "Classes Remaining",
          value: "classes_remaining",
          sortable: true,
        },
      ],
      page: 1,
      pageSize: 10,
      sortBy: 'date_purchased',
      sortDesc: true,
      totalPackagesPurchased: 0,
      paginatedObject: {},
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
      "01:00 AM",
      "01:15 AM",
      "01:30 AM",
      "01:45 AM",
      "02:00 AM",
      "02:15 AM",
      "02:30 AM",
      "02:45 AM",
      "03:00 AM",
      "03:15 AM",
      "03:30 AM",
      "03:45 AM",
      "04:00 AM",
      "04:15 AM",
      "04:30 AM",
      "04:45 AM",
      "05:00 AM",
      "05:15 AM",
      "05:30 AM",
      "05:45 AM",
      "06:00 AM",
      "06:15 AM",
      "06:30 AM",
      "06:45 AM",
      "07:00 AM",
      "07:15 AM",
      "07:30 AM",
      "07:45 AM",
      "08:00 AM",
      "08:15 AM",
      "08:30 AM",
      "08:45 AM",
      "09:00 AM",
      "09:15 AM",
      "09:30 AM",
      "09:45 AM",
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
      "01:00 PM",
      "01:15 PM",
      "01:30 PM",
      "01:45 PM",
      "02:00 PM",
      "02:15 PM",
      "02:30 PM",
      "02:45 PM",
      "03:00 PM",
      "03:15 PM",
      "03:30 PM",
      "03:45 PM",
      "04:00 PM",
      "04:15 PM",
      "04:30 PM",
      "04:45 PM",
      "05:00 PM",
      "05:15 PM",
      "05:30 PM",
      "05:45 PM",
      "06:00 PM",
      "06:15 PM",
      "06:30 PM",
      "06:45 PM",
      "07:00 PM",
      "07:15 PM",
      "07:30 PM",
      "07:45 PM",
      "08:00 PM",
      "08:15 PM",
      "08:30 PM",
      "08:45 PM",
      "09:00 PM",
      "09:15 PM",
      "09:30 PM",
      "09:45 PM",
      "10:00 PM",
      "10:15 PM",
      "10:30 PM",
      "10:45 PM",
      "11:00 PM",
      "11:15 PM",
      "11:30 PM",
    ],
    loading: true,
    overlay: false,
    showEmailForm: false,
    resendEmailObj : {},
    };
  },
  watch: {
    options: {
      handler() {
        this.getPackageHistoryTable();
      },
      deep: true,
    },
  },
  created() {
    this.getPackageHistoryTable();

    this.$root.$refs.B = this;

    if (this.$store.state.user.username == "admin") {
      this.headers.push({ text: "Actions", value: "actions", sortable: false });
       this.headers.unshift({ text: "Purchase ID", value: "package_purchase_id", sortable: true });
        this.headers.splice(-5, 0, { text: "Discount", value: "discount", sortable: false });
    }
    
  },
  methods: {
    resendEmailForm(item) {
      this.showEmailForm = true;
      this.resendEmailObj.package_purchase_id = item.package_purchase_id;
      this.resendEmailObj.email = this.$store.state.clientDetails.email;
    },
    resendEmailToClient() {
      this.showEmailForm = false;
      packagePurchaseService.resendEmail(this.resendEmailObj).then((response) => {
        if (response.status == 201) {
          alert("Email sent")
        }
      });
    },
    temporaryPageMethod() {
     
      this.getPackageHistoryTable();
    },
    temporaryPageSizeMethod() {
      if (this.page == 1) {
        this.getPackageHistoryTable();
      } else {
       
        this.page = 1;
        this.getPackageHistoryTable();
      }
    },
    sortTable() {
      if (this.sortDesc == undefined) {
        this.sortDesc = false;
      } 
      this.getPackageHistoryTable();
    },
    getPackageHistoryTable() {
      this.loading = true;
      if (this.$store.state.user.username == "admin") {
        packagePurchaseService
        .getPaginatedUserPurchasedPackagesByClientId(parseInt(this.$route.params.clientId),this.page, this.pageSize, this.sortBy, this.sortDesc)
          .then((response) => {
            if (response.status == 200) {
              this.loading = false;
              this.paginatedObject = response.data;
              this.totalPackagesPurchased = this.paginatedObject.totalRows;
              this.packageHistoryList = this.paginatedObject.listOfPurchasedPackages;
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
        packagePurchaseService.getPaginatedUserPurchasedPackages(this.page, this.pageSize, this.sortBy, this.sortDesc).then((response) => {
          if (response.status == 200) {
              this.loading = false;
              this.paginatedObject = response.data;
              this.totalPackagesPurchased = this.paginatedObject.totalRows;
              this.packageHistoryList = this.paginatedObject.listOfPurchasedPackages;
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
      this.loading = true;
      this.dialog = false;
      this.overlay = !this.overlay;
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
            this.overlay = false;
            this.loading = false;
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