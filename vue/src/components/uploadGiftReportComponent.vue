<template>
    <v-container>
      <form ref="uploadForm" @submit.prevent="submit">
          <v-row>
              <v-col>
                  <v-btn @click="uploadGiftReportCSV" v-if="!attemptedUpload">
                      Upload Gift Card Report
                  </v-btn>
                  <v-btn @click="uploadGiftReportCSV" v-if="attemptedUpload && successfulGiftReportUpload" color="green">
                      Upload Gift Card Report
                  </v-btn>
                  <v-btn @click="uploadGiftReportCSV" v-if="attemptedUpload && !successfulGiftReportUpload" color="red">
                      Upload Gift Card Report
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
    name: "upload-Gift-Report-Component",
    components: {},
    data() {
      return {
          giftReportFormData: null,
          attemptedUpload: false,
          successfulGiftReportUpload: false,
          overlay: false
      };
    },
    created() {
    },
    methods: {
      onCSVUpload() {
          let file = this.$refs.uploadCSV.files[0];
          this.giftReportFormData = new FormData();
          this.giftReportFormData.append("file", file)
      },
      uploadGiftReportCSV() {
          
          if (this.giftReportFormData != null) {
              this.attemptedUpload = true;
              this.overlay = true;
              databaseUploadService.uploadGiftReport(this.giftReportFormData).then((response) => {
                  if (response.status == 200) {
                      this.overlay = false;
                      alert("Successfully uploaded events to database")
                      this.successfulGiftReportUpload = true;
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