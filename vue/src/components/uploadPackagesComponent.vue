<template>
    <v-container>
      <form ref="uploadForm" @submit.prevent="submit">
          <v-row>
              <v-col>
                  <v-btn @click="uploadPackagesCSV" v-if="!attemptedUpload">
                      Upload Packages
                  </v-btn>
                  <v-btn @click="uploadPackagesCSV" v-if="attemptedUpload && successfulPackageUpload" color="green">
                      Upload Packages
                  </v-btn>
                  <v-btn @click="uploadPackagesCSV" v-if="attemptedUpload && !successfulPackageUpload" color="red">
                      Upload Packages
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
    name: "upload-Packages-Component",
    components: {},
    data() {
      return {
          packageFormData: null,
          attemptedUpload: false,
          successfulPackageUpload: false,
          overlay: false
      };
    },
    created() {
    },
    methods: {
      onCSVUpload() {
          let file = this.$refs.uploadCSV.files[0];
          this.packageFormData = new FormData();
          this.packageFormData.append("file", file)
      },
      uploadPackagesCSV() {
          
          if (this.packageFormData != null) {
              this.attemptedUpload = true;
              this.overlay = true;
              databaseUploadService.uploadPackages(this.packageFormData).then((response) => {
                  if (response.status == 200) {
                      this.overlay = false;
                      alert("Successfully uploaded packages to database")
                    this.successfulPackageUpload = true;
                  } 
              }).catch((error) => {
            const response = error.response;
            if (response.status === 500 || response.status === 400) {
              alert(error.response.data.message)
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