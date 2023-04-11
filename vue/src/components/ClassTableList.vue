<template>
  <div id="app">
    <v-app id="inspire">
      <v-data-table
        :headers="headers"
        :items="classes"
        class="elevation-5"
        sort-by="class_id"
        :loading="loading"
        loading-text="Loading... Please wait"
        dense
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

                <!-- START OF CREATE CLASS FORM from CreateClassForm.vue-->
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

                        <v-select
                          label="Start Time"
                          :items="allTimes"
                          v-model="classDetails.start_time"
                          variant="underlined"
                        ></v-select>

                        <!-- START OF TIME PICKER -->
                        <!-- <v-menu
                          ref="menu"
                          v-model="dropDownOpen"
                          :close-on-content-click="false"
                          :nudge-right="40"
                          :return-value.sync="selectedTime"
                          lazy
                          transition="scale-transition"
                          offset-y
                          max-width="330px"
                          min-width="330px"
                        >
                          <template v-slot:activator="{ on }">
                            <v-text-field
                              v-model="displayTime"
                              label="Start Time"
                              prepend-icon="mdi-clock-time-four-outline"
                              readonly
                              v-on="on"
                              :rules="timeRules"
                            ></v-text-field>
                          </template>
                          <v-container class="v-date-time-widget-container" fluid>
                            <br>
                            <v-row>
                              <br>
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
                            <br>
                            <v-time-picker
                              v-if="dropDownOpen"
                              v-model="timeModel"
                              full-width
                              scrollable
                              
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
                          
                          </v-container>
                        </v-menu> -->
                        <!-- END OF TIME PICKER -->
                        <v-select
                          v-model.number="classDetails.class_duration"
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
                        <!-- <v-checkbox
                          v-model="classDetails.is_repeating"
                          label="Repeat Every Week"
                          required
                        ></v-checkbox> -->
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
            <!-- END OF CREATE CLASS FORM -->

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
                        <v-select
                          v-model="editedItem.editedTeacherName"
                          item-value="editedTeacherName"
                          :items="teacherNames"
                          
                          label="Teacher Names"
                         
                        ></v-select>
                        <v-select
                          label="Start Time"
                          :items="allTimes"
                          v-model="editedItem.start_time"
                          variant="underlined"
                        ></v-select>
                        <!-- START OF TIME PICKER -->
                        <!-- <v-menu
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
                              :rules="timeRules"
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
                            
                          </v-container>
                        </v-menu> -->
                        <!-- END OF TIME PICKER -->
                        <v-select
                          v-model.number="editedItem.class_duration"
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
                        <!-- <v-checkbox
                          v-model="editedItem.is_repeating"
                          label="Repeat Every Week"
                          required
                        ></v-checkbox> -->
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
            <!-- END OF EDIT FORM -->

            <!-- DELETE ? -->
            <v-dialog v-model="dialogDelete" max-width="500px">
              <v-card>
                <v-card-title class="text-h5"
                  >Sure you want to delete this class?</v-card-title
                >
                <v-card-title class="text-h6"
                  >This will delete the class sign up list as well</v-card-title
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
        <!-- <template v-slot:[`item.is_repeating`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_repeating"
            disabled
          ></v-simple-checkbox>
        </template> -->
        <template v-slot:[`item.is_paid`]="{ item }">
          <v-simple-checkbox
            v-model="item.is_paid"
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
import classDetailService from "../services/ClassDetailService";
import teacherService from "../services/TeacherService";
import _ from "lodash";
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
      headers: [
        { text: "Class ID", align: "start", sortable: true, value: "class_id" },
        {
          text: "Class Description",
          value: "class_description",
        },
        { text: "Teacher", value: "teacher_name" },
        {
          text: "Duration (minutes)",
          value: "class_duration",
          sortable: false,
        },
        { text: "Class Time", value: "start_time", sortable: false },
        // { text: "Repeat Every Week", value: "is_repeating", sortable: false },
        { text: "Selected Days", value: "date_range", sortable: false },
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
        is_repeating: true,
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
        class_duration: 60,
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
        class_duration: 60,
        start_time: "",
        is_paid: true,
        is_repeating: true,
        date_range: [],
      },

      selectedTeacherName: "Chuck Mallur",

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
      createFormIncomplete: true,
      editFormIncomplete: true,
      meridiam: "",
      selectedTime: "",
      dateModel: "",
      loading: true,
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
          this.loading = false;
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
      classDetailService
        .deleteClass(this.editedItem.class_id)
        .then((response) => {
          if (response.status == 200) {
            this.classes.splice(this.editedIndex, 1);
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
      if (
        this.classDetails.teacher_id == 0 ||
        this.classDetails.class_duration == 0 ||
        this.classDetails.class_description == "" ||
        this.classDetails.start_time == "" ||
        this.classDetails.date_range == []
      ) {
        alert("Please fill out your form");
      } else {
        this.createFormIncomplete = false;
      }
    },
    checkEditForm() {
      if (
        // compare two objects to see if they are exact
        _.isEqual(this.editedItem, this.classes[this.editedIndex]) ||
        this.editedItem.teacher_id == 0 ||
        this.editedItem.class_duration == 0 ||
        this.editedItem.class_description == "" ||
        this.editedItem.start_time == "" ||
        this.editedItem.date_range.length == 0
      ) {
        alert("Please fill out your form");
      } else {
        this.editFormIncomplete = false;
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

      this.checkCreateForm();
      if (this.createFormIncomplete == false) {
        // after completing the object do the POST REQUEST
        classDetailService.createClass(this.classDetails).then((response) => {
          if (response.status == 201) {
            alert("You have created a class!");
            // this.classDetails.teacher_name = this.selectedTeacherName;
            // this.$store.state.classList.push(this.classDetails);
            this.getClassTable();
            this.reset();
            this.$root.$refs.Z.getAllEvents();
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

      this.checkEditForm();
      if (this.editFormIncomplete == false) {
        // after completing the object do the PUT REQUEST
        classDetailService.updateClass(this.editedItem).then((response) => {
          if (response.status == 200) {
            alert("You have updated a class!");
            this.getClassTable();
            this.close2();
            this.$root.$refs.Z.getAllEvents();
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
      this.dropDownOpen = false;
      this.onUpdateDate();

      this.menu2 = false;
    },
    onUpdateDate() {
      if (!this.timeModel) {
        return false;
      }
      if (this.timeModel == "00:00") {
        this.timeModel = "12:00";
      }
      this.selectedTime = this.timeModel + " " + this.meridiam;
      this.displayTime = this.selectedTime;
      this.classDetails.start_time = this.selectedTime;
      // this.editedItem.start_time = this.selectedTime;
      this.$emit("input", this.selectedTime);
    },
  },
  computed: {
    calculateTime() {
      let date = new Date();
      var hours = date.getHours();
      var minutes = date.getMinutes();
      var ampm = hours >= 12 ? "pm" : "am";
      hours = hours % 12;
      hours = hours ? hours : 12; // the hour '0' should be '12'
      minutes = minutes < 10 ? "0" + minutes : minutes;
      var strTime = hours + ":" + minutes + " " + ampm;
      return strTime;
    },

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