<template>
  <div id="app">
    <v-app id="inspire">
      <v-data-table
        :headers="headers"
        :items="classes"
        class="elevation-5"
        sort-by="class_id"
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>My Classes</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
            <!-- FORM from CreateClassForm-->
            <v-dialog v-model="dialog" max-width="500px">
              <template v-slot:activator="{ on, attrs }">
                <v-btn
                  color="primary"
                  dark
                  class="mb-2"
                  v-bind="attrs"
                  v-on="on"
                >
                  Create A Class
                </v-btn>
              </template>
              <v-card justify="center">
                <v-card-title>
                  <span class="text-h5">{{ formTitle }}</span>
                </v-card-title>

                <!-- FORM from CreateClassForm-->
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
                        <v-select
                          v-model="selectedTeacherName"
                          :items="teacherNames"
                          :rules="[(v) => !!v || 'Name is required']"
                          label="Teacher Names"
                          required
                        ></v-select>
                        <!-- TIME PICKER -->
                        <v-menu
                          ref="menu"
                          v-model="dropDownOpen"
                          :close-on-content-click="false"
                          :nudge-right="40"
                          :return-value.sync="selectedTime"
                          lazy
                          transition="scale-transition"
                          offset-y
                          max-width="290px"
                          min-width="290px"
                        >
                          <template v-slot:activator="{ on }">
                            <v-text-field
                              v-model="displayTime"
                              label="Start Time"
                              prepend-icon="mdi-clock-time-four-outline"
                              readonly
                              v-on="on"
                            ></v-text-field>
                          </template>
                          <v-container class="v-date-time-widget-container">
                            <v-row>
                              <v-spacer></v-spacer>
                              <v-btn
                                fab
                                small
                                :color="getMeridiamButtonColor('AM')"
                                class="btn-am"
                                @click="meridiam = 'AM'"
                                >AM</v-btn
                              >
                              <v-spacer></v-spacer>
                              <v-btn
                                fab
                                small
                                :color="getMeridiamButtonColor('PM')"
                                class="btn-pm"
                                @click="meridiam = 'PM'"
                                >PM</v-btn
                              >
                              <v-spacer></v-spacer
                            ></v-row>
                            <v-time-picker
                              v-if="dropDownOpen"
                              v-model="timeModel"
                              full-width
                              scrollable
                              :rules="timeRules"
                              :no-title="true"
                              ampm-in-title
                              ><v-spacer></v-spacer>
        
                              <v-btn
                                text
                                color="primary"
                                @click="dropDownOpen = false"
                              >
                                Cancel
                              </v-btn>
                              <v-btn text color="primary" @click="confirm()">
                                OK
                              </v-btn></v-time-picker
                            >
                            <!-- END OF TIME PICKER -->
                          </v-container>
                        </v-menu>
                        <v-select
                          v-model="classDetails.class_duration"
                          :items="durationOptions"
                          :rules="durationRules"
                          label="Duration in minutes"
                          required
                        ></v-select>
                        <v-text-field
                          v-model="classDetails.class_description"
                          :rules="descriptionRules"
                          label="Description"
                          required
                        ></v-text-field>
                        <v-select
                          v-model="classDetails.date_range"
                          :items="days"
                          :menu-props="{ maxHeight: '400' }"
                          label="Select Days"
                          multiple
                          hint="Pick your Days For Class"
                          persistent-hint
                        ></v-select>
                        <v-checkbox
                          v-model="classDetails.is_repeating"
                          label="Repeat Every Week"
                          required
                        ></v-checkbox>
                        <v-checkbox
                          v-model="classDetails.is_paid"
                          label="Paid class?"
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
                  <!-- <v-btn color="blue darken-1" text @click="save"> Save </v-btn> -->
                </v-card-actions>
              </v-card>
            </v-dialog>

            <!-- EDIT FORM -->
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
                        <v-select
                          v-model="editedTeacherName"
                          item-value="editedTeacherName"
                          :items="teacherNames"
                          :rules="[(v) => !!v || 'Name is required']"
                          label="Teacher Names"
                          required
                        ></v-select>
                        <!-- TIME PICKER -->
                        <v-menu
                          ref="menu"
                          v-model="menu4"
                          :close-on-content-click="false"
                          :nudge-right="40"
                          :return-value.sync="selectedTime"
                          lazy
                          transition="scale-transition"
                          offset-y
                          max-width="290px"
                          min-width="290px"
                        >
                          <template v-slot:activator="{ on }">
                            <v-text-field
                              v-model="displayTime"
                              label="Start Time"
                              prepend-icon="mdi-clock-time-four-outline"
                              readonly
                              v-on="on"
                            ></v-text-field>
                          </template>
                          <v-container class="v-date-time-widget-container">
                            <v-row>
                              <v-spacer></v-spacer>
                              <v-btn
                                fab
                                small
                                :color="getMeridiamButtonColor('AM')"
                                class="btn-am"
                                @click="meridiam = 'AM'"
                                >AM</v-btn
                              >
                              <v-spacer></v-spacer>
                              <v-btn
                                fab
                                small
                                :color="getMeridiamButtonColor('PM')"
                                class="btn-pm"
                                @click="meridiam = 'PM'"
                                >PM</v-btn
                              >
                              <v-spacer></v-spacer
                            ></v-row>
                          <v-time-picker
                            v-if="menu4"
                            v-model="timeModel"
                            full-width
                            scrollable
                            :rules="timeRules"
                            :no-title="true"
                              ampm-in-title
                          ><v-spacer></v-spacer>
        
                              <v-btn
                                text
                                color="primary"
                                @click="menu4 = false"
                              >
                                Cancel
                              </v-btn>
                              <v-btn text color="primary" @click="confirm()">
                                OK
                              </v-btn></v-time-picker
                            >
                        <!-- END OF TIME PICKER -->
                          </v-container>
                        </v-menu>
                        <v-select
                          v-model="editedItem.class_duration"
                          :items="durationOptions"
                          :rules="durationRules"
                          label="Duration in minutes"
                          required
                        ></v-select>
                        <v-text-field
                          v-model="editedItem.class_description"
                          :rules="descriptionRules"
                          label="Description"
                          required
                        ></v-text-field>
                        <v-select
                          v-model="editedItem.date_range"
                          :items="days"
                          :menu-props="{ maxHeight: '400' }"
                          label="Select Days"
                          multiple
                          hint="Pick your Days For Class"
                          persistent-hint
                        ></v-select>
                        <v-checkbox
                          v-model="editedItem.is_repeating"
                          label="Repeat Every Week"
                          required
                        ></v-checkbox>
                        <v-checkbox
                          v-model="editedItem.is_paid"
                          label="Paid class?"
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
                  <!-- <v-btn color="blue darken-1" text @click="save"> Save </v-btn> -->
                </v-card-actions>
              </v-card>
            </v-dialog>

            <!-- <v-dialog v-model="dialog2" max-width="500px">
                <v-card-text>
                <v-container>
                  <v-row>
                    <v-col
                      cols="12"
                      sm="6"
                      md="4"
                    >
                      <v-text-field
                        v-model="editedItem.class_description"
                        label="Class Descripton"
                      ></v-text-field>
                    </v-col>
                    <v-col
                      cols="12"
                      sm="6"
                      md="4"
                    >
                      <v-text-field
                        v-model="editedItem.teacher_name"
                        label="Teacher"
                      ></v-text-field>
                    </v-col>
                    <v-col
                      cols="12"
                      sm="6"
                      md="4"
                    >
                      <v-text-field
                        v-model="editedItem.fat"
                        label="Fat (g)"
                      ></v-text-field>
                    </v-col>
                    <v-col
                      cols="12"
                      sm="6"
                      md="4"
                    >
                      <v-text-field
                        v-model="editedItem.carbs"
                        label="Carbs (g)"
                      ></v-text-field>
                    </v-col>
                    <v-col
                      cols="12"
                      sm="6"
                      md="4"
                    >
                      <v-text-field
                        v-model="editedItem.protein"
                        label="Protein (g)"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text> 
            </v-dialog> -->

            <!-- DELETE ? -->
            <v-dialog v-model="dialogDelete" max-width="500px">
              <v-card>
                <v-card-title class="text-h5"
                  >Are you sure you want to delete this item?</v-card-title
                >
                <v-card-title class="text-h6"
                  >This will delete the sign up list as well</v-card-title
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
          <!-- CHECK BOX for IS_PAID AND IS_REPEATING -->
        </template>
        <template v-slot:[`item.is_repeating`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_repeating"
            disabled
          ></v-simple-checkbox>
        </template>
        <template v-slot:[`item.is_paid`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_paid"
            disabled
          ></v-simple-checkbox>
        </template>
        <!-- ICONS  -->
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
import classDetailService from "../services/ClassDetailService";
import teacherService from "../services/TeacherService";

export default {
  name: "class-table-list",
  components: {},
  props: ["value"],
  data() {
    return {
      // ==================== this is table stuff vvvv
      dialog: false,
      dialog2: false,
      dialogDelete: false,
      headers: [
        { text: "Class ID", align: "start", sortable: true, value: "class_id" },
        {
          text: "Class Description",
          value: "class_description",
        },
        { text: "Teacher", value: "teacher_name" },
        { text: "Duration (minutes)", value: "class_duration" },
        { text: "Class Time", value: "start_time", sortable: true },
        { text: "Repeat Every Week", value: "is_repeating" },
        { text: "Selected Days", value: "date_range" },
        { text: "Is Paid", value: "is_paid" },
        { text: "Actions", value: "actions", sortable: false },
      ],
      classes: [],
      editedIndex: -1,
      editedItem: {
        class_description: "",
        teacher_id: "",
        class_duration: 0,
        start_time: "",
        is_paid: true,
        is_repeating: "",
        date_range: [],
      },
      editedTeacherName: "",
      editedDate: "",
      editedTime: "",
      menu3: false,
      menu4: false,
      defaultItem: {
        class_description: "",
        teacher_id: "",
        class_duration: 0,
        start_time: "",
        is_paid: true,
        is_repeating: true,
        date_range: [],
      },

      // ====================  this is form stuff vvvv
      date: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .substr(0, 10),
      displayTime: "",
      timeModel: "",
      dropDownOpen: false,
      time: null,
      menu2: false,
      menu: false,
      expand: true,
      durationOptions: [10, 20, 30, 40, 50, 60],
      classDetails: {
        class_description: "",
        teacher_id: "",
        class_duration: 0,
        start_time: "",
        is_paid: true,
        is_repeating: true,
        date_range: [],
      },

      selectedTeacherName: "",

      nameRules: [
        (v) => !!v || "Name is required",
        (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
      ],
      calendarRules: [
        (v) => !!v || "Date and Time are required",
        (v) =>
          (v && v.length <= 30) ||
          "Date and Time must be less than 30 characters",
      ],
      descriptionRules: [(v) => !!v || "Description is required"],
      durationRules: [(v) => !!v || "Duration is required"],
      teacherNames: [],
      teacherObj: [],
      days: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      fullName: "",
      fullname2: "",
      formIncomplete: true,
      formIncomplete2: true,
      meridiam: "",
      selectedTime: "",
      
    };
  },
  created() {
    // ==================== this is table stuff vvvv
    this.getClassTable();
    // ==================== this is form stuff vvvv
    this.fetchTeachers();

    var d = new Date();
    var currentHour = d.getHours() % 12;
    var minutes = (d.getMinutes() < 10 ? "0" : "") + d.getMinutes();
    var currentTime = currentHour + ":" + minutes;
    this.timeModel = currentTime;
    this.dateModel = d.toISOString().substr(0, 10);

    if (d.getHours() >= 12) {
      this.meridiam = "PM";
    }
  },
  methods: {
    // ==================== this is table stuff vvvv
    getClassTable() {
      classDetailService.getAllClasses().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_CLASS_LIST", response.data);
          this.classes = response.data;
        } else {
          alert("Error retrieving class information");
        }
      });
    },
    editItem(item) {
      this.editedIndex = this.classes.indexOf(item);
      this.editedTeacherName = item.teacher_name.value;
      this.editedItem = Object.assign({}, item);
      this.dialog2 = true;
    },

    deleteItem(item) {
      this.editedIndex = this.classes.indexOf(item);

      this.editedItem = Object.assign({}, item);
      this.dialogDelete = true;
    },

    deleteItemConfirm() {
      this.classes.splice(this.editedIndex, 1);
      classDetailService
        .deleteClass(this.editedItem.class_id)
        .then((response) => {
          if (response.status == 200) {
            alert("Class successfully removed!");
          } else {
            alert("Error removing class!");
          }
        });
      this.closeDelete();
    },

    close() {
      this.dialog = false;
      this.reset();
      // ==================== This isn't being used right now
      // this.$nextTick(() => {
      //   this.editedItem = Object.assign({}, this.defaultItem);
      //   this.editedIndex = -1;
      // });
    },
    close2() {
      this.dialog2 = false;
      this.reset();
      // ==================== This isn't being used right now
      // this.$nextTick(() => {
      //   this.editedItem = Object.assign({}, this.defaultItem);
      //   this.editedIndex = -1;
      // });
    },
    closeDelete() {
      this.dialogDelete = false;
      //   this.$nextTick(() => {
      //     this.editedItem = Object.assign({}, this.defaultItem);
      //     this.editedIndex = -1;
      //   });
    },

    save() {
      //   if (this.editedIndex > -1) {
      //     Object.assign(this.desserts[this.editedIndex], this.editedItem);
      //   } else {
      //     this.desserts.push(this.editedItem);
      //   }
      this.close();
    },
    // ==================== this is form stuff vvvv
    checkForm() {
      if (
        this.classDetails.teacher_id == 0 ||
        this.classDetails.class_duration == 0 ||
        this.classDetails.class_description == "" ||
        this.classDetails.start_time == "" ||
        this.classDetails.date_range == []
      ) {
        alert("Please fill out your form");
      } else {
        this.formIncomplete = false;
      }
    },
    checkForm2() {
      if (
        this.editedItem.teacher_id == 0 ||
        this.editedItem.class_duration == 0 ||
        this.editedItem.class_description == "" ||
        this.editedItem.start_time == "" ||
        this.editedItem.date_range == []
      ) {
        alert("Please fill out your form");
      } else {
        this.formIncomplete2 = false;
      }
    },
    reset() {
      this.$refs.form.reset();
    },

    submit() {
      // assign teacher name to object
      this.teacherObj.forEach((item) => {
        this.fullName = item.first_name + " " + item.last_name;
        if (this.selectedTeacherName == this.fullName) {
          this.classDetails.teacher_id = item.teacher_id;
        }
      });

      this.checkForm();
      if (this.formIncomplete == false) {
        // after completing the object do the POST REQUEST
        classDetailService.createClass(this.classDetails).then((response) => {
          if (response.status == 201) {
            alert("You have created a class!");
            // this.classDetails.teacher_name = this.selectedTeacherName;
            // this.$store.state.classList.push(this.classDetails);
            this.getClassTable();
            this.close();
          } else {
            alert("Error creating a class!");
          }
        });
      }
    },
    update() {
      // assign teacher name to object
      this.teacherObj.forEach((item2) => {
        this.fullName2 = item2.first_name + " " + item2.last_name;
        if (this.editedTeacherName == this.fullName2) {
          this.editedItem.teacher_id = item2.teacher_id;
        }
      });

      this.checkForm2();
      if (this.formIncomplete2 == false) {
        // after completing the object do the PUT REQUEST
        classDetailService.updateClass(this.editedItem).then((response) => {
          if (response.status == 200) {
            alert("You have updated a class!");
            this.getClassTable();
            this.close2();
          } else {
            alert("Error updating a class!");
          }
        });
      }
    },
    fetchTeachers() {
      teacherService.getTeacherList().then((response) => {
        this.teacherObj = response.data;
        this.teacherObj.forEach((item) => {
          this.teacherNames.push(item.first_name + " " + item.last_name);
        });
      });
    },
    getMeridiamButtonColor(m) {
      if (m == this.meridiam) {
        return "primary";
      } else {
        return "dark-gray";
      }
    },
    confirm() {
      this.onUpdateDate();
      this.dropDownOpen = false;
      this.menu4=false;
    },
    onUpdateDate() {
      if (!this.timeModel) {
        return false;
      }
      this.selectedTime = this.timeModel + " " + this.meridiam;
      this.displayTime = this.selectedTime;
      this.classDetails.start_time = this.selectedTime;
      this.editedItem.start_time = this.selectedTime;
      this.$emit("input", this.selectedTime);
    },
  },
  computed: {
    model: {
      get() {
        return this.value;
      },
      // set(model) {}
    },

    // currentSelection() {
    //   this.selectedTime = this.timeModel + " " + this.meridiam;
    //   return this.selectedTime;
    // },

    formTitle() {
      return this.editedIndex === -1 ? "Create A Class" : "Edit Class";
    },
    modifiedTime() {
      var currentTime = new Date(this.time);
      var hours = currentTime.getHours();
      //Note: before converting into 12 hour format
      var suffix = "";
      if (hours > 11) {
        suffix += "PM";
      } else {
        suffix += "AM";
      }
      var minutes = currentTime.getMinutes();
      if (minutes < 10) {
        minutes = "0" + minutes;
      }
      if (hours > 12) {
        hours -= 12;
      } else if (hours === 0) {
        hours = 12;
      }
      var time = hours + ":" + minutes + " " + suffix;
      return time;
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
.btn-am {
  float: left;
}

.btn-pm {
  float: right;
}

.v-date-time-widget-container {
  background: white;
  padding: 10px;
}
</style>