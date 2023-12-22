<template>
  <v-data-table
    :headers="headers"
    :items="teachers"
    sort-by="first_name"
    class="elevation-1"
  >
    <template v-slot:top>
      <v-toolbar flat>
        <v-toolbar-title>Teacher List</v-toolbar-title>
        <v-divider class="mx-4" inset vertical></v-divider>
        <v-spacer></v-spacer>

        <v-dialog v-model="dialog" max-width="500px" persistent>
          <template v-slot:activator="{ on, attrs }">
            <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
              Add Teacher
            </v-btn>
          </template>
          <v-card justify="center">
            <v-card-title>
              <span class="text-h5">{{ formTitle }}</span>
            </v-card-title>
            <!-- FORM from CreateTeacherForm-->
            <v-container>
              <v-row justify="center" style="min-height: 160px">
                <v-col cols="5">
                  <v-form
                    ref="form"
                    height="100"
                    width="500"
                    v-model="valid"
                    lazy-validation
                    class="class-form mx-auto white"
                    @submit.prevent="submit"
                  >
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

                    <v-btn class="mr-4 mt-4" outlined color="blue" type="submit" :disabled="invalid">
                      submit
                    </v-btn>
                  </v-form>
                </v-col>
              </v-row>
            </v-container>

      
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="red" text @click="close"> Cancel </v-btn>
            
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- EDIT Form -->
        <v-dialog v-model="dialog2" max-width="500px" persistent>
          <v-card justify="center">
            <v-card-title>
              <span class="text-h5">{{ formTitle }}</span>
            </v-card-title>

            <v-container>
              <v-row justify="center" style="min-height: 160px">
                <v-col cols="5">
                  <v-form
                    ref="form"
                    height="100"
                    width="500"
                    v-model="valid"
                    lazy-validation
                    class="class-form mx-auto white"
                    @submit.prevent="update"
                  >
                    <v-text-field
                      v-model="editedItem.first_name"
                      :rules="nameRules"
                      label="First Name"
                      required
                    ></v-text-field>

                    <v-text-field
                      v-model="editedItem.last_name"
                      :rules="nameRules"
                      label="Last Name"
                      required
                    ></v-text-field>

                    <v-checkbox
                      v-model="editedItem.is_teacher_active"
                      label="Active?"
                      required
                    ></v-checkbox>

                    <v-btn color="error" class="mr-4" @click="reset">
                      Reset Form
                    </v-btn>

                    <v-btn class="mr-4" type="submit" :disabled="invalid">
                      update
                    </v-btn>
                  </v-form>
                </v-col>
              </v-row>
            </v-container>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="close2"> Cancel </v-btn>
              <!-- <v-btn color="blue darken-1" text @click="save"> Save </v-btn> -->
            </v-card-actions>
          </v-card>
        </v-dialog>

        <!-- DELETE -->
        <v-dialog v-model="dialogDelete" max-width="500px">
          <v-card>
            <v-card-title class="text-h5"
              >Are you sure you want to remove this teacher?</v-card-title
            >
            <v-card-title class="text-h6"
              >You need to unassign this teacher from classes first for this to
              work</v-card-title
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
    <!-- EDIT/DELETE ICONS -->
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon large class="mr-2" @click="editItem(item)"> mdi-pencil </v-icon>
      <v-icon large @click="deleteItem(item)"> mdi-delete </v-icon>
    </template>
    <template v-slot:no-data>
      <v-btn color="primary" @click="initialize"> Reset </v-btn>
    </template>
    <template v-slot:[`item.is_teacher_active`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_teacher_active"
          disabled
        ></v-simple-checkbox>
      </template>
  </v-data-table>
</template>

<script>
import teacherService from "../services/TeacherService";
import classDetailService from "../services/ClassDetailService";

export default {
  name: "teacher-table-list",
  data() {
    return {
      //============ Table stuff
      dialog: false,
      dialog2: false,
      dialogDelete: false,
      headers: [
        {
          text: "First Name",
          align: "start",
          sortable: true,
          value: "first_name",
        },
        { text: "Last Name", value: "last_name" },
        { text: "Teacher ID", value: "teacher_id" },
        { text: "Active", value: "is_teacher_active" },
        { text: "Actions", value: "actions", sortable: false },
      ],
      teachers: [],
      editedIndex: -1,
      editedItem: {
        teacher_id: "",
        first_name: "",
        last_name: "",
        is_teacher_active: true,
      },
      defaultItem: {
        teacher_id: "",
        first_name: "",
        last_name: "",
        is_teacher_active: true,
      },

      //============ form data
      teacherDetails: {
        teacher_id: "",
        first_name: "",
        last_name: "",
        is_teacher_active: true,
      },

      nameRules: [
        (v) => !!v || "Name is required",
        (v) => (v && v.length <= 30) || "Name must be less than 30 characters",
      ],

      formIncomplete: true,
      formIncomplete2: true,
      //============

      classes: [],
    };
  },

  methods: {
    // =========== form stuff
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

    checkForm2() {
      if (this.editedItem.first_name == "" || this.editedItem.last_name == "") {
        alert("Please fill out your form");
      } else {
        this.formIncomplete2 = false;
      }
    },

    reset() {
      this.$refs.form.reset();
    },

    submit() {
      this.checkForm();
      if (this.formIncomplete == false) {
        teacherService.registerTeacher(this.teacherDetails).then((response) => {
          if (response.status == 201) {
            alert("You have created a teacher");
            this.getTeachers();
            this.reset();
            this.close();
          }
        })
        .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            } else {
            alert("Error creating a teacher!");
          }
      });
      }
    },

    update() {
      this.checkForm2();
      if (this.formIncomplete2 == false) {
        teacherService.updateTeacher(this.editedItem).then((response) => {
          if (response.status == 200) {
            alert("You have updated a teacher");
            this.getTeachers();
            this.reset();
            this.close2();
          }
        })
        .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            } else {
            alert("Error updating a teacher!");
          }
      }); 
      }
    },
    // ===========

    getTeachers() {
      teacherService.getTeacherList().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_TEACHER_LIST", response.data);
          this.teachers = response.data;
        }
      })
      .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            } else {
          alert("Error retrieving class information");
        }
      });
    },

    editItem(item) {
      this.editedIndex = this.teachers.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialog2 = true;
    },

    deleteItem(item) {
      this.editedIndex = this.teachers.indexOf(item);
      this.editedItem = Object.assign({}, item);
      this.dialogDelete = true;
    },

    getClassTable() {
      classDetailService.getAllClasses().then((response) => {
        if (response.status == 200) {
          this.$store.commit("SET_CLASS_LIST", response.data);
          this.classes = response.data;
        } 
      })
      .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            } else {
          alert("Error retrieving class information");
        }
      });
    },

    deleteItemConfirm() {
      var unique = true;
      this.classes.forEach((item) => {
   
        if (item.teacher_id === this.editedItem.teacher_id) {
          unique = false;
 
        }
      });

      if (unique == true) {
        this.teachers.splice(this.editedIndex, 1);
        teacherService
          .deleteTeacher(this.editedItem.teacher_id)
          .then((response) => {
            if (response.status == 200) {
              alert("Teacher successfully removed!");
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
      } else {
        alert("Error! This teacher is currently assigned to a class!");
        this.closeDelete();
      }
    },

    close() {
      this.dialog = false;
      this.reset();
      // this.$nextTick(() => {
      //   this.editedItem = Object.assign({}, this.defaultItem);
      //   this.editedIndex = -1;
      // });
    },

    close2() {
      this.dialog2 = false;
      this.reset();
      // this.$nextTick(() => {
      //   this.editedItem = Object.assign({}, this.defaultItem);
      //   this.editedIndex = -1;
      // });
    },

    closeDelete() {
      this.dialogDelete = false;
      // this.$nextTick(() => {
      //   this.editedItem = Object.assign({}, this.defaultItem);
      //   this.editedIndex = -1;
      // });
    },

    save() {
      // if (this.editedIndex > -1) {
      //   Object.assign(this.desserts[this.editedIndex], this.editedItem);
      // } else {
      //   this.desserts.push(this.editedItem);
      // }
      this.close();
    },
  },

  computed: {
    formTitle() {
      return this.editedIndex === -1 ? "Create A Teacher" : "Edit Teacher";
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

  created() {
    if (this.$store.state.user.username != "admin") {
      this.$router.push({name: 'home'})
    }

    this.getTeachers();
    this.getClassTable();
  },
};
</script>

<style>
</style>