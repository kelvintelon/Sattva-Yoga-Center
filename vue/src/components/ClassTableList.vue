<template>
  <div id="app">
    <v-app id="inspire">
      <v-data-table :headers="headers" :items="classes" class="elevation-5">
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>My Classes</v-toolbar-title>
            <v-divider class="mx-4" inset vertical></v-divider>
            <v-spacer></v-spacer>
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
                    <v-col cols="6">
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
                        <v-menu
                          v-model="menu"
                          :close-on-content-click="false"
                          :nudge-right="40"
                          transition="scale-transition"
                          offset-y
                          min-width="auto"
                        >
                          <template v-slot:activator="{ on, attrs }">
                            <v-text-field
                              v-model="date"
                              label="Date picker"
                              prepend-icon="mdi-calendar-multiselect"
                              readonly
                              v-bind="attrs"
                              v-on="on"
                            ></v-text-field>
                          </template>
                          <v-date-picker
                            v-model="date"
                            @input="menu = false"
                          ></v-date-picker>
                        </v-menu>
                        <v-menu
                          ref="menu"
                          v-model="menu2"
                          :close-on-content-click="false"
                          :nudge-right="40"
                          :return-value.sync="time"
                          transition="scale-transition"
                          offset-y
                          max-width="290px"
                          min-width="290px"
                        >
                          <template v-slot:activator="{ on, attrs }">
                            <v-text-field
                              v-model="time"
                              label="Time"
                              prepend-icon="mdi-clock-time-four-outline"
                              readonly
                              v-bind="attrs"
                              v-on="on"
                            ></v-text-field>
                          </template>
                          <v-time-picker
                            v-if="menu2"
                            v-model="time"
                            full-width
                            @click:minute="$refs.menu.save(time)"
                            scrollable
                            :rules="timeRules"
                          ></v-time-picker>
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

                <!-- OLD FORM -->
                <!-- <v-card-text>
                <v-container>
                  <v-row>
                    <v-col
                      cols="12"
                      sm="6"
                      md="4"
                    >
                      <v-text-field
                        v-model="editedItem.name"
                        label="Dessert name"
                      ></v-text-field>
                    </v-col>
                    <v-col
                      cols="12"
                      sm="6"
                      md="4"
                    >
                      <v-text-field
                        v-model="editedItem.calories"
                        label="Calories"
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
              </v-card-text> -->

                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="close">
                    Cancel
                  </v-btn>
                  <!-- <v-btn color="blue darken-1" text @click="save"> Save </v-btn> -->
                </v-card-actions>
              </v-card>
            </v-dialog>

            <!-- DELETE ? -->
            <v-dialog v-model="dialogDelete" max-width="500px">
            <v-card>
              <v-card-title class="text-h5">Are you sure you want to delete this item?</v-card-title>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="closeDelete">Cancel</v-btn>
                <v-btn color="blue darken-1" text @click="deleteItemConfirm">OK</v-btn>
                <v-spacer></v-spacer>
              </v-card-actions>
            </v-card>
          </v-dialog>
          </v-toolbar>
          <!-- CHECK BOX for IS_PAID -->
        </template>
        <template v-slot:[`item.is_paid`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_paid"
          disabled
        ></v-simple-checkbox>
        </template>
        <!-- ICONS  -->
        <template v-slot:[`item.actions`]="{ item }">
        <v-icon
          small
          class="mr-2"
          @click="editItem(item)"
        >
          mdi-pencil
        </v-icon>
        <v-icon
          small
          @click="deleteItem(item)"
        >
          mdi-delete
        </v-icon>
      </template>
      <template v-slot:no-data>
        <v-btn
          color="primary"
          @click="initialize"
        >
          Reset
        </v-btn>
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
  data() {
    return {
      // ==================== this is table stuff vvvv
      dialog: false,
      dialogDelete: false,
      headers: [
        {
          text: "Class Description",
          align: "start",
          sortable: false,
          value: "class_description",
        },
        { text: "Teacher", value: "teacher_name" },
        { text: "Duration (minutes)", value: "class_duration" },
        { text: "Class Time", value: "class_datetime" },
        { text: "Is Paid", value: "is_paid" },
        { text: 'Actions', value: 'actions', sortable: false },
      ],
      classes: [],
      editedIndex: -1,
      editedItem: {
        name: "",
        calories: 0,
        fat: 0,
        carbs: 0,
        protein: 0,
      },
      defaultItem: {
        name: "",
        calories: 0,
        fat: 0,
        carbs: 0,
        protein: 0,
      },
      // ====================  this is form stuff vvvv
      date: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
        .toISOString()
        .substr(0, 10),
      time: null,
      menu2: false,
      menu: false,
      expand: true,
      durationOptions: [10, 20, 30, 40, 50, 60],
      classDetails: {
        teacher_id: "",
        class_datetime: "",
        class_duration: 0,
        is_paid: true,
        class_description: "",
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
      fullName: "",
      formIncomplete: true,
    };
  },
  created() {
    // ==================== this is table stuff vvvv
    this.getClassTable();
    // ==================== this is form stuff vvvv
    this.fetchTeachers();
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
      this.editedItem = Object.assign({}, item);
      this.dialog = true;
    },

    deleteItem(item) {
      this.editedIndex = this.desserts.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialogDelete = true;
    },

    deleteItemConfirm() {
      this.desserts.splice(this.editedIndex, 1);
      this.closeDelete();
    },

    close() {
      this.dialog = false;

        // ==================== This isn't being used right now
        // this.$nextTick(() => {
        //   this.editedItem = Object.assign({}, this.defaultItem);
        //   this.editedIndex = -1;
        // });
    },

    closeDelete() {
      this.dialogDelete = false;
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
    },

    save() {
      if (this.editedIndex > -1) {
        Object.assign(this.desserts[this.editedIndex], this.editedItem);
      } else {
        this.desserts.push(this.editedItem);
      }
      this.close();
    },
    // ==================== this is form stuff vvvv
    checkForm() {
      if (
        this.classDetails.teacher_id == 0 ||
        this.classDetails.class_duration == 0 ||
        this.classDetails.class_description == "" ||
        this.time == "" ||
        this.time == null ||
        this.time == undefined
      ) {
        alert("Please fill out your form");
      } else {
        this.formIncomplete = false;
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

        // assign the date and time through concatenation
        this.classDetails.class_datetime = this.date + "T" + this.time + ":00";

        // checks if form has date time (the only field where rules don't work)

        // after completing the object do the post
        classDetailService.createClass(this.classDetails).then((response) => {
          if (response.status == 201) {
            alert("You have created a class!");
            // this.classDetails.teacher_name = this.selectedTeacherName;
            // this.$store.state.classList.push(this.classDetails);
            this.getClassTable();
            this.reset();
            this.close();
          } else {
            alert("Error creating a class!");
          }
        });
      }
    },

    fetchTeachers() {
      teacherService.getTeacherList().then((response) => {
        this.teacherObj = response.data;
        this.teacherObj.forEach((item) => {
          this.fullName = item.first_name + " " + item.last_name;
          this.teacherNames.push(this.fullName);
        });
      });
    },
  },
  computed: {
    formTitle() {
      return this.editedIndex === -1 ? "Create A Class" : "Edit Class";
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