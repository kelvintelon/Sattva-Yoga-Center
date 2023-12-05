<template>
    <v-container>
      <form ref="uploadForm" @submit.prevent="submit">
          <v-row>
              <v-col>
                  <v-btn @click="uploadFamilyCSV" v-if="!attemptedUpload">
                      Upload Family
                  </v-btn>
                  <v-btn @click="uploadFamilyCSV" v-if="attemptedUpload && successfulFamilyUpload" color="green">
                      Upload Family
                  </v-btn>
                  <v-btn @click="uploadFamilyCSV" v-if="attemptedUpload && !successfulFamilyUpload" color="red">
                      Upload Family
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
    name: "upload-Family-Component",
    components: {},
    data() {
      return {
          familyFormData: null,
          attemptedUpload: false,
          successfulFamilyUpload: false,
          overlay: false
      };
    },
    created() {
    },
    methods: {
      onCSVUpload() {
          let file = this.$refs.uploadCSV.files[0];
          this.familyFormData = new FormData();
          this.familyFormData.append("file", file)
      },
      uploadFamilyCSV() {
          
          if (this.familyFormData != null) {
              this.attemptedUpload = true;
              this.overlay = true;
              databaseUploadService.uploadFamily(this.familyFormData).then((response) => {
                  if (response.status == 200) {
                      this.overlay = false;
                      alert("Successfully uploaded families to database")
                      this.successfulFamilyUpload = true;
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