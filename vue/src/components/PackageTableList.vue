<template>
  <div id="app">
    <v-app id="inspire">
      <v-data-table
        :headers="headers"
        :items="packages"
        class="elevation-5"
        sort-by="package_id"
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>My Packages</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
            <!-- FORM-->
            <v-dialog v-model="dialog" max-width="500px">
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  color="primary"
                  dark
                  class="mb-2"
                  v-bind="attrs"
                  v-on="on"
                >
                  Create A Package
                </v-btn>
              </template>
              <v-card justify="center">
                <v-card-title>
                  <span class="text-h5">{{ formTitle }}</span>
                </v-card-title>

                <!-- START OF CREATE PACKAGE FORM -->
                <v-container>
                  <v-row justify="center" style="min-height: 160px">
                    <v-col cols="7">
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
                          v-model="packageDetails.description"
                          :rules="descriptionRules"
                          label="Description"
                          required
                        ></v-text-field>
                        <v-select
                          v-model.number="packageDetails.classes_amount"
                          :items="classesAmountOptions"
                          label="Amount of Classes"
                          required
                        ></v-select>
                        <v-select
                          v-model.number="packageDetails.subscription_duration"
                          :items="durationOptions"
                          label="Duration in months"
                          required
                          @change="onSubscriptionDurationChange"
                        ></v-select>
                        <br />
                        <v-text-field
                          v-model="packageDetails.package_cost"
                          class="mt-0 pt-0"
                          type="number"
                          style="width: 60px"
                          label="Cost: $"
                          min="10"
                        ></v-text-field>
                        <v-checkbox
                          v-model="packageDetails.is_subscription"
                          label="Is this a Subscription?"
                          required
                          @change="onSubscriptionBooleanChange"
                        ></v-checkbox>
                        <v-checkbox
                          v-model="packageDetails.is_visible_online"
                          label="Should this be Visible Online?"
                          required
                        ></v-checkbox>
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

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="close">
                    Cancel
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            <!-- END OF CREATE PACKAGE FORM -->

            <!-- START OF EDIT FORM -->
            <v-dialog v-model="dialog2" max-width="500px">
              <v-card justify="center">
                <v-card-title>
                  <span class="text-h5">{{ formTitle }}</span>
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
                          v-model="editedItem.description"
                          :rules="descriptionRules"
                          label="Description"
                          required
                        ></v-text-field>
                        <v-select
                          v-model.number="editedItem.classes_amount"
                          :items="classesAmountOptions"
                          label="Amount of Classes"
                          required
                        ></v-select>
                        <v-select
                          v-model.number="editedItem.subscription_duration"
                          :items="durationOptions"
                          label="Duration in months"
                          required
                          @change="onSubscriptionDurationChange"
                        ></v-select>
                        <br />
                        <v-text-field
                          v-model="editedItem.package_cost"
                          class="mt-0 pt-0"
                          type="number"
                          style="width: 60px"
                          label="Cost: $"
                          min="10"
                        ></v-text-field>

                        <v-checkbox
                          v-model="editedItem.is_subscription"
                          label="Subscription?"
                          required
                          @change="onSubscriptionBooleanChange"
                        ></v-checkbox>
                        <v-checkbox
                          v-model="editedItem.is_visible_online"
                          label="Visible Online?"
                          required
                        ></v-checkbox>
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
                  <v-btn color="blue darken-1" text @click="close2">
                    Cancel
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            <!-- END OF EDIT FORM -->

            <!-- DELETE ? -->
            <v-dialog v-model="dialogDelete" max-width="500px">
              <v-card>
                <v-card-title class="text-h5"
                  >Are you sure you want to delete this package?</v-card-title
                >
                <v-card-title class="text-h6"
                  >This will delete the package purchase history as
                  well</v-card-title
                >
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="closeDelete"
                    >Cancel</v-btn
                  >
                  <v-btn color="blue darken-1" text @click="deleteItemConfirm"
                    >OK</v-btn
                  >
                  <v-spacer></v-spacer>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-toolbar>
        </template>
        <!-- CHECK BOX for IS_PAID AND IS_REPEATING -->
        <template v-slot:[`item.is_subscription`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_subscription"
            disabled
          ></v-simple-checkbox>
        </template>
        <template v-slot:[`item.is_visible_online`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_visible_online"
            disabled
          ></v-simple-checkbox>
        </template>
        <!-- ACTIONS / ICONS  -->
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon small class="mr-2" @click="editItem(item)">
            mdi-pencil
          </v-icon>
          <v-icon small @click="deleteItem(item)"> mdi-delete </v-icon>
        </template>
        <template v-slot:no-data>
          <v-btn color="primary" @click="initialize"> Reset </v-btn>
        </template>
      </v-data-table>
    </v-app>
  </div>
</template>

<script>
import packageDetailService from "../services/PackageDetailService";

export default {
  name: "package-table-list",
  components: {},
  data() {
    return {
      // ==================== this is table stuff vvvv
      dialog: false,
      dialog2: false,
      dialogDelete: false,
      headers: [
        {
          text: "Package ID",
          align: "start",
          sortable: true,
          value: "package_id",
        },
        {
          text: "Package Description",
          value: "description",
        },
        { text: "Cost $", value: "package_cost" },
        {
          text: "Amount of Classes",
          value: "classes_amount",
          sortable: false,
        },
        {
          text: "Subscription Duration (months)",
          value: "subscription_duration",
          sortable: false,
        },
        { text: "Subscription?", value: "is_subscription", sortable: false },
        {
          text: "Visible Online?",
          value: "is_visible_online",
          sortable: false,
        },
        { text: "Actions", value: "actions", sortable: false },
      ],
      packages: [],
      durationOptions: [0, 1, 2,3,4,5,6],
      classesAmountOptions: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20],
      editedIndex: -1,

      menu3: false,
      menu4: false,


      // ====================  this is form stuff vvvv
      editedItem: {
        package_id: "",
        description: "",
        package_cost: 10,
        classes_amount: 0,
        subscription_duration: 0,
        is_subscription: false,
        is_visible_online: false,
      },
      dropDownOpen: false,

      menu2: false,
      menu: false,
      expand: true,

      packageDetails: {
        description: "",
        package_cost: 10,
        classes_amount: 0,
        subscription_duration: 0,
        is_subscription: false,
        is_visible_online: false,
      },
      descriptionRules: [(v) => !!v || "Description is required"],

      createFormIncomplete: true,
      editFormIncomplete: true,
    };
  },
  created() {
    // ==================== this is table stuff vvvv
    this.getPackageTable();
    // ==================== this is form stuff vvvv
  },
  methods: {
    onSubscriptionDurationChange(){
      if (this.packageDetails.subscription_duration > 0){
        this.packageDetails.is_subscription = true;
      } else {
        this.packageDetails.is_subscription = false;
      }
      if (this.editedItem.subscription_duration > 0){
        this.editedItem.is_subscription = true;
      } else {
        this.editedItem.is_subscription = false;
      }
    },
    onSubscriptionBooleanChange(){
      if (this.packageDetails.is_subscription == true){
        this.packageDetails.subscription_duration = 1;
      } else {
        this.packageDetails.subscription_duration = 0;
      }
      if (this.editedItem.is_subscription == true){
        this.editedItem.subscription_duration = 1;
      } else {
        this.editedItem.subscription_duration = 0;
      }
    },
    // ==================== this is table stuff vvvv
    getPackageTable() {
      packageDetailService.getAllPackages().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_PACKAGE_LIST", response.data);
          this.packages = response.data;
        } else {
          alert("Error retrieving package information");
        }
      });
    },
    editItem(item) {
      this.editedIndex = this.packages.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialog2 = true;
    },

    deleteItem(item) {
      this.editedIndex = this.packages.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialogDelete = true;
    },

    deleteItemConfirm() {
      packageDetailService
        .deletePackage(this.editedItem.package_id)
        .then((response) => {
          if (response.status == 200) {
            this.packages.splice(this.editedIndex, 1);
            alert("Package successfully removed!");
          } else {
            alert("Error removing package!");
          }
        });
      this.closeDelete();
    },

    close() {
      this.dialog = false;
      this.reset();
    },
    close2() {
      this.dialog2 = false;
      this.reset();
    },
    closeDelete() {
      this.dialogDelete = false;
    },
    // ==================== this is form stuff vvvv
    checkCreateForm() {
      if (this.packageDetails.description == "") {
        alert("Please fill out your form");
      } else {
        this.createFormIncomplete = false;
      }
    },
    checkEditForm() {
      if (this.editedItem.description == "") {
        alert("Please fill out your form");
      } else {
        this.editFormIncomplete = false;
      }
    },
    reset() {
      this.$refs.form.reset();
    },

    submit() {
      this.checkCreateForm();
      if (this.packageDetails.is_subscription == false) {
        this.packageDetails.subscription_duration = 0;
      }
      if (this.createFormIncomplete == false) {
        // after completing the object do the POST REQUEST
        packageDetailService
          .createPackage(this.packageDetails)
          .then((response) => {
            if (response.status == 201) {
              alert("You have created a package!");
              this.getPackageTable();
              this.reset();
              this.close();
            } else {
              alert("Error creating a package!");
            }
          });
      }
    },
    update() {
      this.checkEditForm();
      if (this.editedItem.is_subscription == false) {
        this.editedItem.subscription_duration = 0;
      }
      if (this.editFormIncomplete == false) {
        // after completing the object do the PUT REQUEST
        packageDetailService.updatePackage(this.editedItem).then((response) => {
          if (response.status == 200) {
            alert("You have updated a class!");
            this.getPackageTable();
            this.close2();
          } else {
            alert("Error updating a class!");
          }
        });
      }
    },
  },
  computed: {
    formTitle() {
      return this.editedIndex === -1 ? "Create A Package" : "Edit Package";
    },
  },
  watch: {
    dialog(val) {
      val || this.close();
    },
    dialogDelete(val) {
      val || this.closeDelete();
    },
  },
};
</script>

<style>
</style>