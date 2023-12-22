<template>
  <v-container>
    <v-row justify="center" style="min-height: 160px">
      <v-col cols="5">
        <v-btn class="ma-2" color="primary" @click="expand = !expand">
          Create a teacher
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
          >
            <h1>Create a teacher</h1>

            <v-text-field
              v-model="teacherDetails.first_name"
              :rules="nameRules"
              label="First Name"
              required
            ></v-text-field>

            <v-text-field
              v-model="teacherDetails.last_name"
              :rules="nameRules"
              label="Last Name"
              required
            ></v-text-field>

            <v-checkbox
              v-model="teacherDetails.is_teacher_active"
              label="Active?"
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
import teacherService from "../services/TeacherService";

export default {
  data: () => ({
    name: "create-teacher-form",
    expand: false,
    teacherDetails: {
      first_name: "",
      last_name: "",
      is_teacher_active: true,
    },

    nameRules: [
      (v) => !!v || "Name is required",
      (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
    ],

    formIncomplete: true,
  }),


  methods: {
    checkForm() {
      if (
        this.teacherDetails.first_name == "" ||
        this.teacherDetails.last_name == "" 
      ) {
        alert("Please fill out your form");
      } else {
        this.formIncomplete = false;
      }
    },
    reset() {
      this.$refs.form.reset();
    },
    submit(){
        this.checkForm();
        if(this.formIncomplete == false){
            teacherService.registerTeacher(this.teacherDetails).then((response) => {
                if(response.status == 201){
                    alert("You have created a teacher")
                }
            })
            .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            } else{
                    alert("!Error creating a teacher")
                }
      });
        }
    }
  },
};
</script>


<style scoped>
</style>