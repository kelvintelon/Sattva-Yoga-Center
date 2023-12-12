<template>
  <div id="app">
    <v-app id="inspire">
      <v-data-table
        :headers="headers"
        :items="packages"
        class="elevation-5"
        sort-by="package_order"
        dense
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>My Packages</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
            <!-- FORM-->
            <v-dialog v-model="dialog" max-width="500px" persistent>
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
                          v-model.number="packageDetails.package_duration"
                          :items="durationOptions"
                          label="Duration in months"
                          required
                          @change="onPackageDurationChange"
                        ></v-select>
                        <v-text-field
                          v-model.number="packageDetails.package_cost"
                          class="mt-0 pt-0"
                          type="number"
                          style="width: 60px"
                          label="Cost: $"
                          min="10"
                        ></v-text-field>
                        <v-text-field
                          v-model.number="packageDetails.package_order"
                          class="mt-0 pt-0"
                          type="number"
                          style="width: 60px"
                          label="Order: "
                          min="1"
                        ></v-text-field>
                        <v-checkbox
                          v-model="packageDetails.unlimited"
                          label="Unlimited?"
                          required
                          @change="onSubscriptionBooleanChange"
                        ></v-checkbox>
                        <v-checkbox
                          v-model="packageDetails.active"
                          label="Active?"
                          required
                        ></v-checkbox>
                        
                        <!-- <v-checkbox v-if="toggleRecurring || packageDetails.unlimited"
                          v-model="packageDetails.is_recurring"
                          label="Is this recurring?"
                          required
                        ></v-checkbox> -->
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
            <v-dialog v-model="dialog2" max-width="500px" persistent>
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
                          v-model.number="editedItem.package_duration"
                          :items="durationOptions"
                          label="Duration in months"
                          required
                          @change="onPackageDurationChange"
                        ></v-select>
                        
                        <v-text-field
                          v-model.number="editedItem.package_cost"
                          class="mt-0 pt-0"
                          type="number"
                          style="width: 60px"
                          label="Cost: $"
                          min="10"
                        ></v-text-field>
                        <v-text-field
                          v-model.number="editedItem.package_order"
                          class="mt-0 pt-0"
                          type="number"
                          style="width: 60px"
                          label="Order: "
                          min="1"
                        ></v-text-field>
                        <v-checkbox
                          v-model="editedItem.unlimited"
                          label="Unlimited?"
                          required
                          @change="onSubscriptionBooleanChange"
                        ></v-checkbox>
                        <v-checkbox
                          v-model="editedItem.active"
                          label="Active?"
                          required
                        ></v-checkbox>
                        
                        <!-- <v-checkbox v-if="toggleRecurring || editedItem.unlimited"
                          v-model="editedItem.is_recurring"
                          label="Is this recurring?"
                          required
                        ></v-checkbox> -->
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
        <template v-slot:[`item.unlimited`]="{ item }">
          <v-simple-checkbox
            v-model="item.unlimited"
            disabled
          ></v-simple-checkbox>
        </template>
        <template v-slot:[`item.is_visible_online`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_visible_online"
            disabled
          ></v-simple-checkbox>
        </template>
        <template v-slot:[`item.active`]="{ item }">
          <v-simple-checkbox
            v-model="item.active"
            disabled
          ></v-simple-checkbox>
        </template>
        <!-- ACTIONS / ICONS  -->
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon large class="mr-2" @click="editItem(item)">
            mdi-pencil
          </v-icon>
          <v-icon large @click="deleteItem(item)"> mdi-delete </v-icon>
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
      toggleRecurring: false,
      headers: [
        {
          text: "Package ID",
          sortable: true,
          value: "package_id",
        },
        {
          text: "Order",
          align: "start",
          sortable: true,
          value: "package_order"
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
          text: "Duration (months)",
          value: "package_duration",
          sortable: false,
        },
        { text: "Unlimited?", value: "unlimited", sortable: false },
        {
          text: "Visible Online?",
          value: "is_visible_online",
          sortable: false,
        },
        {
          text: "Active",
          value: "active",
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
        package_duration: 0,
        unlimited: false,
        is_visible_online: true,
        is_recurring: false,
      },
      dropDownOpen: false,

      menu2: false,
      menu: false,
      expand: true,

      packageDetails: {
        description: "",
        package_cost: 10,
        classes_amount: 0,
        package_duration: 0,
        unlimited: false,
        active: true,
        is_visible_online: true,
        is_recurring: false,
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
    onPackageDurationChange(){
      if (this.packageDetails.package_duration > 0){
        this.packageDetails.unlimited = true;
        this.toggleRecurring = true;
      } else {
        this.packageDetails.unlimited = false;
        this.toggleRecurring = false;
      }
      if (this.editedItem.package_duration > 0){
        this.editedItem.unlimited = true;
        this.toggleRecurring = true;
      } else {
        this.editedItem.unlimited = false;
        this.toggleRecurring = false;

      }
    },
    onSubscriptionBooleanChange(){
      if (this.packageDetails.unlimited == true){
        this.packageDetails.package_duration = 1;
        this.toggleRecurring = true;
      } else {
        this.packageDetails.package_duration = 0;
        this.toggleRecurring = false;

      }
      if (this.editedItem.unlimited == true){
        this.editedItem.package_duration = 1;
        this.toggleRecurring = true;
      } else {
        this.editedItem.package_duration = 0;
        this.toggleRecurring = false;

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
      })
      .catch((error) => {
          const response = error.response;
          if (response.status === 401) {
            this.$router.push("/login");
          }
          if (response.status == 403) {
            this.$router.push("/logout")
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
      let foundDuplicateName = false
      for (let index = 0; index < this.packages.length; index++) {
        let element = this.packages[index];
        if (element.description == this.packageDetails.description) {
          foundDuplicateName = true;
        }
      }
      if (this.packageDetails.description == "") {
        alert("Please fill out your form");
      } else if (foundDuplicateName) {
        alert("Package name already taken")
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
      this.packageDetails = {
        description: "",
        package_cost: 10,
        classes_amount: 0,
        package_duration: 0,
        unlimited: false,
        active: true,
        is_visible_online: true,
        is_recurring: false,
      }
    },

    submit() {
      this.checkCreateForm();
      if (this.packageDetails.unlimited == false) {
        this.packageDetails.package_duration = 0;
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
      if (this.editedItem.unlimited == false) {
        this.editedItem.package_duration = 0;
      }
      if (this.editFormIncomplete == false) {
        // after completing the object do the PUT REQUEST
        packageDetailService.updatePackage(this.editedItem).then((response) => {
          if (response.status == 200) {
            alert("You have updated a package!");
            this.getPackageTable();
            this.close2();
          } else {
            alert("Error updating a package!");
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
      this.packageDetails.package_order = this.packages.length+1;
    },
    dialogDelete(val) {
      val || this.closeDelete();
    },
  },
};
</script>

<style>
</style>