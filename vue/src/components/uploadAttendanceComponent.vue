<template>
    <v-container>
      <form ref="uploadForm" @submit.prevent="submit">
          <v-row>
              <v-col>
                  <v-btn @click="uploadAttendanceCSV" v-if="!attemptedUpload">
                      Upload Attendance
                  </v-btn>
                  <v-btn @click="uploadAttendanceCSV" v-if="attemptedUpload && successfulAttendanceUpload" color="green">
                      Upload Attendance
                  </v-btn>
                  <v-btn @click="uploadAttendanceCSV" v-if="attemptedUpload && !successfulAttendanceUpload" color="red">
                      Upload Attendance
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
    name: "upload-Attendance-Component",
    components: {},
    data() {
      return {
          attendanceFormData: null,
          attemptedUpload: false,
          successfulAttendanceUpload: false,
          overlay: false
      };
    },
    created() {
    },
    methods: {
      onCSVUpload() {
          let file = this.$refs.uploadCSV.files[0];
          this.attendanceFormData = new FormData();
          this.attendanceFormData.append("file", file)
      },
      uploadAttendanceCSV() {
          
          if (this.attendanceFormData != null) {
              this.attemptedUpload = true;
              this.overlay = true;
              databaseUploadService.uploadAttendance(this.attendanceFormData).then((response) => {
                  if (response.status == 200) {
                      this.overlay = false;
                      alert("Successfully uploaded attendance to database")
                      this.successfulAttendanceUpload = true;
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