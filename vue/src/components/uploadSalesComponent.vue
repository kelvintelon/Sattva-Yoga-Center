<template>
    <v-container>
      <form ref="uploadForm" @submit.prevent="submit">
          <v-row>
              <v-col>
                  <v-btn @click="uploadSalesCSV" v-if="!attemptedUpload">
                      Upload Sales
                  </v-btn>
                  <v-btn @click="uploadSalesCSV" v-if="attemptedUpload && successfulSalesUpload" color="green">
                      Upload Sales
                  </v-btn>
                  <v-btn @click="uploadSalesCSV" v-if="attemptedUpload && !successfulSalesUpload" color="red">
                      Upload Sales
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
    name: "upload-Sales-Component",
    components: {},
    data() {
      return {
          salesFormData: null,
          attemptedUpload: false,
          successfulSalesUpload: false,
          overlay: false
      };
    },
    created() {
    },
    methods: {
      onCSVUpload() {
          let file = this.$refs.uploadCSV.files[0];
          this.salesFormData = new FormData();
          this.salesFormData.append("file", file)
      },
      uploadSalesCSV() {
          
          if (this.salesFormData != null) {
              this.attemptedUpload = true;
              this.overlay = true;
              databaseUploadService.uploadSales(this.salesFormData).then((response) => {
                  if (response.status == 200) {
                      this.overlay = false;
                      alert("Successfully uploaded events to database")
                      this.successfulSalesUpload = true;
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