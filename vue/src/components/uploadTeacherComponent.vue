<template>
    <v-container>
      <form ref="uploadForm" @submit.prevent="submit">
          <v-row>
              <v-col>
                  <v-btn @click="uploadTeacherCSV" v-if="!attemptedUpload">
                      Upload Teachers
                  </v-btn>
                  <v-btn @click="uploadTeacherCSV" v-if="attemptedUpload && successfulTeacherUpload" color="green">
                      Upload Teachers
                  </v-btn>
                  <v-btn @click="uploadTeacherCSV" v-if="attemptedUpload && !successfulTeacherUpload" color="red">
                      Upload Teachers
                  </v-btn>
              </v-col>
              <v-col>
                  <input type="file" ref="uploadCSV" @change="onCSVUpload()" class="form-control" required>
              </v-col>
              <v-spacer>  
              </v-spacer>
          </v-row>
      </form>
    </v-container>
</template>

<script>
  import databaseUploadService from "../services/DatabaseUploadService";
  export default {
    name: "upload-Client-Component",
    components: {},
    data() {
      return {
          teacherFormData: null,
          attemptedUpload: false,
          successfulTeacherUpload: false,
          overlay: false
      };
    },
    created() {
    },
    methods: {
      onCSVUpload() {
          let file = this.$refs.uploadCSV.files[0];
          this.teacherFormData = new FormData();
          this.teacherFormData.append("file", file)
      },
      uploadTeacherCSV() {
          
          if (this.teacherFormData != null) {
              this.attemptedUpload = true;
              this.overlay = true;
              databaseUploadService.uploadTeachers(this.teacherFormData).then((response) => {
                  if (response.status == 200) {
                      this.overlay = false;
                      alert("Successfully uploaded teachers to database")
                      this.successfulTeacherUpload = true;
                  } 
              }).catch((error) => {
            const response = error.response;
            if (response.status === 500 || response.status === 400) {
              alert("Error uploading file")
              this.overlay = false;
            }
          });
          } else if (this.attemptedUpload) {
              alert("Select a different file to upload")
          } else {
              alert("Please choose a file")
          }
      }
    },
  };
  </script>
  
  <style>
  </style>