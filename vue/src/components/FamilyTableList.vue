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
             <v-dialog v-model="dialog2" max-width="500px" persistent>
              <v-card justify="center">
                <v-card-title>
                  <span class="text-h5" style="color: rgba(245, 104, 71, 0.95)">Update a Family Name</span>
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
                          v-model="editedItem.family_name"
                          label="Family Name"
                          required
                        ></v-text-field>
                        <v-row justify="center" align="center"
                          ><v-col>
                            <v-btn
                              class="mr-4"
                              type="submit"
                              :disabled="invalid"
                              style="color: rgba(245, 104, 71, 0.95)"
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
                  <v-btn color="blue darken-1" text @click="dialog2=false">
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
          <v-icon @click="sendToFamilyDetailsPage(item)">mdi-account-multiple</v-icon>
          <v-icon large class="mr-2" @click="editItem(item)">
            mdi-pencil
          </v-icon>
          <v-icon large @click="deleteItem(item)"> mdi-delete </v-icon>
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
          text: "Member Count",
          value: "listOfFamilyMembers.length",
          sortable: false,
        },
        { text: "Actions", value: "actions", sortable: false },
      ],
      familyList: [],
      dialogDelete: false,
      dialog: false,
      dialog2: false,
      editedItem: {},
      newFamilyObj: {},
      createFormComplete: false,
      updateFormComplete: false,
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
        } 
      })
      .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            } else {
            alert("Error retrieving family information");
            }
      });
    },
    sendToFamilyDetailsPage(item) {
      this.$router.push({
        name: "family-details",
        params: { familyId: item.family_id },
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
        })
        .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            }
      });
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
    deleteItemConfirm() {
        FamilyService
          .deleteFamily(this.editedItem.family_id)
          .then((response) => {
            if (response.status == 200) {
              alert("Family successfully removed!");
              this.getFamilyTable();
            }
          })
          .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            } else {
              // error alert not showing if server gets error
              alert("Error removing teacher!");
            }
          });
        this.closeDelete();
       
    },
    update() {
      this.checkForm2();
      if (this.updateFormComplete) {
        FamilyService.updateFamilyName(this.editedItem).then((response) => {
        
           if (response.status == 200) {
            alert("You have updated a family name");
            this.getFamilyTable();
            this.dialog2=false;
          } else {
            alert("Error updating a family server side!");
          }
          
        })
        .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            }
        });
      }
    },
    editItem(item) {
      this.editedIndex = this.familyList.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialog2 = true;
    },
    checkForm2() {
      if (this.editedItem.family_name == "") {
        alert("Please fill out your form");
      } else {
        this.updateFormComplete = true;
      }
    },
  },
};
</script>

<style>
</style>