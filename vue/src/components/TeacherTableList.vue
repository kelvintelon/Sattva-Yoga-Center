<template>
  <v-data-table
    :headers="headers"
    :items="teachers"
    sort-by="calories"
    class="elevation-1"
  >
    <template v-slot:top>
      <v-toolbar flat>
        <v-toolbar-title>Teacher List</v-toolbar-title>
        <v-divider class="mx-4" inset vertical></v-divider>
        <v-spacer></v-spacer>
        <v-dialog v-model="dialog" max-width="500px">
          <template v-slot:activator="{ on, attrs }">
            <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
              Add Teacher
            </v-btn>
          </template>
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

                    <v-btn class="mr-4" type="submit" :disabled="invalid">
                      submit
                    </v-btn>
                  </v-form>
                </v-col>
              </v-row>
            </v-container>

<!-- OLD FORM
            <v-card-text>
              <v-container>
                <v-row>
                  <v-col cols="12" sm="6" md="4">
                    <v-text-field
                      v-model="editedItem.name"
                      label="Dessert name"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6" md="4">
                    <v-text-field
                      v-model="editedItem.calories"
                      label="Calories"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6" md="4">
                    <v-text-field
                      v-model="editedItem.fat"
                      label="Fat (g)"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6" md="4">
                    <v-text-field
                      v-model="editedItem.carbs"
                      label="Carbs (g)"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6" md="4">
                    <v-text-field
                      v-model="editedItem.protein"
                      label="Protein (g)"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>
-->
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="close"> Cancel </v-btn>
              <!-- <v-btn color="blue darken-1" text @click="save"> Save </v-btn> -->
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog v-model="dialogDelete" max-width="500px">
          <v-card>
            <v-card-title class="text-h5"
              >Are you sure you want to delete this item?</v-card-title
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
<!-- EDIT DELETE ICONS
    <template v-slot:item.actions="{ item }">
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
-->
  </v-data-table>
</template>

<script>
import teacherService from "../services/TeacherService";
export default {
  name: "teacher-table-list",
  data() {
    return {
      dialog: false,
      dialogDelete: false,
      headers: [
        {
          text: "First Name",
          align: "start",
          sortable: true,
          value: "first_name",
        },
        { text: "Last Name", value: "last_name" },
        { text: "Active", value: "is_teacher_active" },
      ],
      teachers: [],
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

      //============ form data
      // expand: false,
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
      //============
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
          } else {
            alert("Error creating a teacher!");
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
        } else {
          alert("Error retrieving class information");
        }
      });
    },

    editItem(item) {
      this.editedIndex = this.desserts.indexOf(item);
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
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem);
        this.editedIndex = -1;
      });
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
    this.getTeachers();
  },
};
</script>

<style>
</style>