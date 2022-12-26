<template>
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
            v-model="classDetails.teacher.teacher_id"
            :items="teacherNames"
            :rules="[(v) => !!v || 'Item is required']"
            label="Teacher Names"
            required
          ></v-select>

          <v-text-field
            v-model="classDetails.class_datetime"
            :counter="10"
            :rules="nameRules"
            label="Class Date and Time"
            required
          ></v-text-field>

          <v-text-field
            v-model="classDetails.class_duration"
            :counter="10"
            :rules="nameRules"
            label="Duration"
            required
          ></v-text-field>

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

          <v-btn color="error" class="mr-4" @click="reset"> Reset Form </v-btn>

          <v-btn class="mr-4" type="submit" :disabled="invalid"> submit </v-btn>
        </v-form>
      </v-expand-transition>
    </v-col>

    <div class="mx-4 hidden-sm-and-down"></div>

    <v-col class="shrink">
      <v-btn class="ma-2" color="secondary" @click="expand2 = !expand2">
        Expand X Transition
      </v-btn>

      <v-expand-x-transition>
        <v-card
          v-show="expand2"
          height="100"
          width="100"
          class="mx-auto secondary"
        ></v-card>
      </v-expand-x-transition>
    </v-col>
  </v-row>
</template>


<script>
import classDetailService from "../services/ClassDetailService";
import teacherService from "../services/TeacherService";

export default {
  props: ['teacher'],
  data: () => ({
    expand: false,

    expand2: false,

    classDetails: {
      teacher_id: "",
      class_datetime: "",
      class_duration: "",
      is_paid: true,
      class_description: "",
    },

    nameRules: [
      (v) => !!v || "Name is required",
      (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
    ],

    teacherNames: [],
  }),

  created(){
    this.fetchTeachers();
  },

  methods: {
    reset() {
      this.$refs.form.reset();
    },

    submit() {
      classDetailService.createClass(this.classDetails).then((response) => {
        if (response.status == 201) {
          alert("You have created a class!");
        }
      });
    },

    fetchTeachers(){
      teacherService.getTeacherList()
      .then((response)=>{
        this.teacherNames = response.data
      })
    }
  },
};
</script>


<style scoped>
</style>