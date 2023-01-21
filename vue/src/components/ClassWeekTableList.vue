<template>
  <v-container class="weekContainer">
    <v-row>
      <v-col align="center" class="rowClass">Monday</v-col>
    </v-row>
    <v-row>
      <div class="col" v-for="(column, key) in mondayColumns" :key="key">
    <div class="item-container" v-for="(item, key) in column" :key="key">{{item.start_time}}&nbsp;&nbsp; {{item.class_description}}</div>
  </div>
    </v-row>
    <v-row>
      <v-col align="center" class="rowClass">Tuesday</v-col>
    </v-row>
    <v-row>
      <div class="col" v-for="(column, key) in tuesdayColumns" :key="key">
    <div class="item-container" v-for="(item, key) in column" :key="key">{{item.start_time}}&nbsp;&nbsp; {{item.class_description}}</div>
  </div>
    </v-row>
    <v-row>
      <v-col align="center" class="rowClass">Wednesday</v-col>
    </v-row>
    <v-row>
      <div class="col" v-for="(column, key) in wednesdayColumns" :key="key">
    <div class="item-container" v-for="(item, key) in column" :key="key">{{item.start_time}}&nbsp;&nbsp; {{item.class_description}}</div>
  </div>
    </v-row>
    <v-row>
      <v-col align="center" class="rowClass">Thursday</v-col>
    </v-row>
    <v-row>
      <div class="col" v-for="(column, key) in thursdayColumns" :key="key">
    <div class="item-container" v-for="(item, key) in column" :key="key">{{item.start_time}}&nbsp;&nbsp; {{item.class_description}}</div>
  </div>
    </v-row>
    <v-row>
      <v-col align="center" class="rowClass">Friday</v-col>
    </v-row>
    <v-row>
      <div class="col" v-for="(column, key) in fridayColumns" :key="key">
    <div class="item-container" v-for="(item, key) in column" :key="key">{{item.start_time}}&nbsp;&nbsp; {{item.class_description}}</div>
  </div>
    </v-row>
    <v-row>
      <v-col align="center" class="rowClass">Saturday</v-col>
    </v-row>
    <v-row>
      <div class="col" v-for="(column, key) in saturdayColumns" :key="key">
    <div class="item-container" v-for="(item, key) in column" :key="key">{{item.start_time}}&nbsp;&nbsp; {{item.class_description}}</div>
  </div>
    </v-row>
    <v-row>
      <v-col align="center" class="rowClass">Sunday</v-col>
    </v-row>
    <v-row>
      <div class="col" v-for="(column, key) in sundayColumns" :key="key">
    <div class="item-container" v-for="(item, key) in column" :key="key">{{item.start_time}}&nbsp;&nbsp; {{item.class_description}}</div>
  </div>
    </v-row>
    <v-row>
      <v-col align="center">All classes are 1 hour long.</v-col>
    </v-row>
    <v-row>
      <v-col align="center">Effective {{todaysDate.toLocaleString()}}</v-col>
    </v-row>
  </v-container>
</template>

<script>
import classDetailService from "../services/ClassDetailService";
import teacherService from "../services/TeacherService";

export default {
  name: "class-week-table-list",
  components: {},
  props: ["value"],
  data() {  
    return {
      // ==================== this is table stuff vvvv
      todaysDate: new Date(),
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
        {
          text: "Duration (minutes)",
          value: "class_duration",
          sortable: false,
        },
        { text: "Class Time", value: "start_time", sortable: false },
        { text: "Repeat Every Week", value: "is_repeating", sortable: false },
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
      createFormIncomplete: true,
      editFormIncomplete: true,
      meridiam: "",
      selectedTime: "",
      sundayClasses: [],
      sundayOrganized: [],
      mondayClasses: [],
      tuesdayClasses: [],
      wednesdayClasses: [],
      thursdayClasses: [],
      fridayClasses: [],
      saturdayClasses: [],
      cols: 2
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
          for (let index = 0; index < this.classes.length; index++) {
            const obj = this.classes[index];
            
          
            if (obj.date_range.includes("Sun")) {
              this.sundayClasses.push(obj);
              // this.sortSundayColumns();
            }
            if (obj.date_range.includes("Mon")) {
              this.mondayClasses.push(obj);
            }
            if (obj.date_range.includes("Tue")) {
              this.tuesdayClasses.push(obj);
            }
            if (obj.date_range.includes("Wed")) {
              this.wednesdayClasses.push(obj);
            }
            if (obj.date_range.includes("Thu")) {
              this.thursdayClasses.push(obj);
            }
            if (obj.date_range.includes("Fri")) {
              this.fridayClasses.push(obj);
            }
            if (obj.date_range.includes("Sat")) {
              this.saturdayClasses.push(obj);
            }
          }

        } else {
          alert("Error retrieving class information");
        }
      });
    },
    sortSundayColumns() {
      this.sundayOrganized = this.sundayColumns;
      this.sundayOrganized.sort( function (a,b) {
        return new Date('1970/01/01 ' + a.start_time.replace("pm"," PM").replace("am"," AM")) - new Date('1970/01/01 ' + b.start_time.replace("pm"," PM").replace("am"," AM"))
      })
      this.sundayColumns = this.sundayOrganized
    }
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
        this.editedItem.teacher_id == 0 ||
        this.editedItem.class_duration == 0 ||
        this.editedItem.class_description == "" ||
        this.editedItem.start_time == "" ||
        this.editedItem.date_range == []
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
      this.menu4 = false;
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
    
  computed: {
    sundayColumns () {
      let columns = []
      let mid = Math.ceil(this.sundayClasses.length / this.cols)
      for (let col = 0; col < this.cols; col++) {
        columns.push(this.sundayClasses.slice(col * mid, col * mid + mid))
      }
      return columns
    },
    mondayColumns () {
      let columns = []
      let mid = Math.ceil(this.mondayClasses.length / this.cols)
      for (let col = 0; col < this.cols; col++) {
        columns.push(this.mondayClasses.slice(col * mid, col * mid + mid))
      }
      return columns
    },
    tuesdayColumns () {
      let columns = []
      let mid = Math.ceil(this.tuesdayClasses.length / this.cols)
      for (let col = 0; col < this.cols; col++) {
        columns.push(this.tuesdayClasses.slice(col * mid, col * mid + mid))
      }
      return columns
    },
    wednesdayColumns () {
      let columns = []
      let mid = Math.ceil(this.wednesdayClasses.length / this.cols)
      for (let col = 0; col < this.cols; col++) {
        columns.push(this.wednesdayClasses.slice(col * mid, col * mid + mid))
      }
      return columns
    },
    thursdayColumns () {
      let columns = []
      let mid = Math.ceil(this.thursdayClasses.length / this.cols)
      for (let col = 0; col < this.cols; col++) {
        columns.push(this.thursdayClasses.slice(col * mid, col * mid + mid))
      }
      return columns
    },
    fridayColumns () {
      let columns = []
      let mid = Math.ceil(this.fridayClasses.length / this.cols)
      for (let col = 0; col < this.cols; col++) {
        columns.push(this.fridayClasses.slice(col * mid, col * mid + mid))
      }
      return columns
    },
    saturdayColumns () {
      let columns = []
      let mid = Math.ceil(this.saturdayClasses.length / this.cols)
      for (let col = 0; col < this.cols; col++) {
        columns.push(this.saturdayClasses.slice(col * mid, col * mid + mid))
      }
      return columns
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

.rowClass {
  font-size: 20px;
  background: rgb(238, 233, 233);
}
.weekContainer {
  
  border: 1px solid;
}

</style>