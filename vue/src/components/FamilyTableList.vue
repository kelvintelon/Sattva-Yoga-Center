<template>
  <div id="app">
    <v-app id="inspire">
      <v-data-table
        :headers="headers"
        :items="familyList"
        class="elevation-5"
        sort-by="family_id"
        dense
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>Families</v-toolbar-title>
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
                  Create A Family
                </v-btn>
              </template>
              <v-card justify="center">
                <v-card-title>
                  <span class="text-h5"  style="color: rgba(245, 104, 71, 0.95)">Create A Family</span>
                </v-card-title>

                <!-- START OF CREATE Family FORM -->
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
                          v-model="newFamilyObj.family_name"
                          label="Family Name"
                          required
                        ></v-text-field>
                      
                        <v-row justify="center" align="center"
                          >
                          <v-col>
                            <v-btn
                              class="mr-4"
                              type="submit"
                              :disabled="invalid"
                              style="color: rgba(245, 104, 71, 0.95)"
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
                  <v-btn color="blue darken-1" text @click="dialog = false">
                    Cancel
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            <!-- END OF CREATE PACKAGE FORM -->

            <!-- START OF EDIT FORM -->
            <!-- <v-dialog v-model="dialog2" max-width="500px" persistent>
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
            </v-dialog> -->
            <!-- END OF EDIT FORM -->

            <!-- DELETE ? -->
            <v-dialog v-model="dialogDelete" max-width="500px">
              <v-card>
                <v-card-title class="text-h5"
                  >Delete this family?</v-card-title
                >
                <v-card-title class="text-h6"
                  >This will delete the family forever
                </v-card-title>
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

        <!-- ACTIONS / ICONS  -->
        <template v-slot:[`item.actions`]="{ item }">
          <v-icon small class="mr-2" @click="editItem(item)">
            mdi-pencil
          </v-icon>
          <v-icon small @click="deleteItem(item)"> mdi-delete </v-icon>
        </template>
        <!-- <template v-slot:no-data>
          <v-btn color="primary" @click="initialize"> Reset </v-btn>
        </template> -->
      </v-data-table>
    </v-app>
  </div>
</template>

<script>
import FamilyService from "../services/FamilyService";

export default {
  name: "family-table-list",
  components: {},
  data() {
    return {
      headers: [
        {
          text: "Family ID",
          sortable: true,
          value: "family_id",
        },
        {
          text: "Family Name",
          sortable: true,
          value: "family_name",
        },
        {
          text: "Active",
          value: "active",
          sortable: false,
        },
        { text: "Actions", value: "actions", sortable: false },
      ],
      familyList: [],
      dialogDelete: false,
      dialog: false,
      editedItem: {},
      newFamilyObj: {},
      createFormComplete: false,
    };
  },
  created() {
    this.getFamilyTable();
  },
  methods: {
    getFamilyTable() {
      FamilyService.getFamilyList().then((response) => {
        if (response.status == 200) {
          this.familyList = response.data;
        } else {
          alert("Error retrieving family information");
        }
      });
    },
    
    submit() {
      this.checkCreateForm();
      "Submitted"
      if (this.createFormComplete) {
        // after completing the object do the POST REQUEST
        FamilyService.createFamily(this.newFamilyObj)
        .then((response) =>{
          if (response.status == 201) {
            this.getFamilyTable();
            alert("Family Created")
            this.dialog = false;
            this.newFamilyObj.family_name = "";
          }
        });
        // packageDetailService
        //   .createPackage(this.packageDetails)
        //   .then((response) => {
        //     if (response.status == 201) {
        //       alert("You have created a package!");
        //       this.getPackageTable();
        //       this.reset();
        //       this.close();
        //     } else {
        //       alert("Error creating a package!");
        //     }
        //   });
      }
    },
    checkCreateForm() {
      let foundDuplicateName = false
      for (let index = 0; index < this.familyList.length; index++) {
        let element = this.familyList[index];
        if (element.family_name == this.newFamilyObj.family_name) {
          foundDuplicateName = true;
        }
      }
      if (this.newFamilyObj.family_name == "") {
        alert("Please fill out your form");
      } else if (foundDuplicateName) {
        alert("Family name already taken")
      } else {
        this.createFormComplete = true;
      }
    },
    deleteItem(item) {
        this.editedItem = Object.assign({}, item);
        this.dialogDelete = true;
    },
    closeDelete() {
      this.dialogDelete = false;
    },
  },
};
</script>

<style>
</style>