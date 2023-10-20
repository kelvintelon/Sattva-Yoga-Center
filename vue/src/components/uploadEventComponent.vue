<template>
    <v-container>
      <form ref="uploadForm" @submit.prevent="submit">
          <v-row>
              <v-col>
                  <v-btn @click="uploadEventCSV" v-if="!attemptedUpload">
                      Upload Events
                  </v-btn>
                  <v-btn @click="uploadEventCSV" v-if="attemptedUpload && successfulEventUpload" color="green">
                      Upload Events
                  </v-btn>
                  <v-btn @click="uploadEventCSV" v-if="attemptedUpload && !successfulEventUpload" color="red">
                      Upload Events
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
    name: "upload-Event-Component",
    components: {},
    data() {
      return {
          eventFormData: null,
          attemptedUpload: false,
          successfulEventUpload: false,
          overlay: false
      };
    },
    created() {
    },
    methods: {
      onCSVUpload() {
          let file = this.$refs.uploadCSV.files[0];
          this.eventFormData = new FormData();
          this.eventFormData.append("file", file)
      },
      uploadEventCSV() {
          
          if (this.eventFormData != null) {
              this.attemptedUpload = true;
              this.overlay = true;
              databaseUploadService.uploadEvents(this.eventFormData).then((response) => {
                  if (response.status == 200) {
                      this.overlay = false;
                      alert("Successfully uploaded events to database")
                      this.successfulEventUpload = true;
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