<template>
  <v-container>
    <v-row justify="center" style="min-height: 160px">
      <v-col cols="5">
        <v-btn class="ma-2" color="primary" @click="expand = !expand">
          Create a class
        </v-btn>

        <v-expand-transition>
          <v-form
            ref="form"
            v-show="expand"
            height="100"
            width="500"
            v-model="valid"
            lazy-validation
            class="class-form mx-auto white"
            @submit.prevent="submit"
            justify="center"
            align="center"
          >
            <h1>Create a class</h1>

            <v-select
              v-model="selectedTeacherName"
              :items="teacherNames"
              :rules="[(v) => !!v || 'Name is required']"
              label="Teacher Names"
              required
            ></v-select>

            <!-- <v-text-field
            v-model="classDetails.class_datetime"
            :counter="10"
            :rules="calendarRules"
            label="YYYY-MM-DD"
            required
          ></v-text-field> -->
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
                  label="Time in Military Format"
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
                format="24hr"
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
              label="Description"
              required
            ></v-text-field>

            <v-checkbox
              v-model="classDetails.is_paid"
              label="Paid class?"
              required
            ></v-checkbox>

            <v-btn color="error" class="mr-4" @click="reset">
              Reset Form
            </v-btn>

            <v-btn class="mr-4" type="submit" :disabled="invalid">
              submit
            </v-btn>
          </v-form>
        </v-expand-transition>
      </v-col>
    </v-row>
  </v-container>
</template>


<script>
import classDetailService from "../services/ClassDetailService";
import teacherService from "../services/TeacherService";
import TopHeader from "./TopHeader2.vue";

export default {
  // props: ['teacher'],
  data: () => ({
    name: "create-class-form",
    components: {
      TopHeader,
    },
    date: new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
      .toISOString()
      .substr(0, 10),
    time: null,
    menu2: false,
    menu: false,
    expand: false,
    expand2: false,
    durationOptions: [10, 20, 30, 40, 50, 60],
    classDetails: {
      teacher_id: "",
      class_datetime: "",
      class_duration: "",
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
    durationRules: [(v) => !!v || "Duration is required"],
    teacherNames: [],
    teacherObj: [],
    fullName: "",
  }),

  created() {
    this.fetchTeachers();
  },

  methods: {
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

      // assign the date and time through concatenation
      this.classDetails.class_datetime = this.date+"T"+this.time+":00"
 
      // after completing the object do the post
      classDetailService.createClass(this.classDetails).then((response) => {
        if (response.status == 201) {
         alert("You have created a class!");
        }
      });
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
};
</script>


<style scoped>
</style>