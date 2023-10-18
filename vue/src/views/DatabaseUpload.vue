<template>
  <v-container>
    <!-- Upload Clients -->
    <form ref="uploadForm" @submit.prevent="submit">
        <v-row>
            <v-col>
                <v-btn @click="uploadClientCSV" v-if="!attemptedUpload">
                    Upload Clients
                </v-btn>
                <v-btn @click="uploadClientCSV" v-if="attemptedUpload && successfulClientUpload" color="green">
                    Upload Clients
                </v-btn>
                <v-btn @click="uploadClientCSV" v-if="attemptedUpload && !successfulClientUpload" color="red">
                    Upload Clients
                </v-btn>
            </v-col>
            <v-col>
                <input type="file" ref="uploadCSV" @change="onCSVUpload()" class="form-control" required>
            </v-col>
            <v-spacer>  
            </v-spacer>
        </v-row>
    </form>
    <v-overlay :value="overlay">
      <v-progress-circular indeterminate size="70"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>
  
<script>
import databaseUploadService from "../services/DatabaseUploadService";

export default {
  name: "database-upload",
  components: {},
  data() {
    return {
        clientFormData: null,
        attemptedUpload: false,
        successfulClientUpload: false,
        overlay: false
    };
  },
  created() {
    if (this.$store.state.user.username != "admin") {
      this.$router.push({ name: "home" });
    }
  },
  methods: {
    onCSVUpload() {
        let file = this.$refs.uploadCSV.files[0];
        this.clientFormData = new FormData();
        this.clientFormData.append("file", file)
    },
    uploadClientCSV() {
        

        if (this.clientFormData != null) {
            this.attemptedUpload = true;
            this.overlay = true;
            databaseUploadService.uploadClients(this.clientFormData).then((response) => {
                if (response.status == 200) {
                    this.overlay = false;
                    alert("Successfully uploaded clients to database")
                    this.successfulClientUpload = true;
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
  