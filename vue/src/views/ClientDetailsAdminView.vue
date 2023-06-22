<template>
  <v-container>

    <v-container class="py-7">
      <v-snackbar v-if="$store.state.user.username == 'admin'"
      v-model="snackBarReconcileWarning"
      color="blue darken-2"
      elevation="24"
      :vertical="vertical"
      outlined
      top
      text
    >
      Client Needs Active Package To Reconcile For Classes
    </v-snackbar>
    <v-snackbar v-if="$store.state.user.username == 'admin'"
      v-model="snackBarEmailReset"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      outlined
      top
      text
      :timeout="timeout"
    >
      Email A Reset Link?
      <template v-slot:action="{ attrs }">
        <v-btn
          color="black"
          text
          v-bind="attrs"
          @click="snackBarEmailReset = false"
          left
        >
          Close
        </v-btn>
        <v-btn color="black" text v-bind="attrs" @click="allowEmailReset">
          Continue
        </v-btn>
      </template>
    </v-snackbar>
      <v-row justify="center" align="center">
        <v-col cols="5 ">
          <v-card align="center" justify="center"  class="rounded-xl  py-7 d-flex flex-column justify-space-around justify-space-between">
          <span class="text-h5" style="color: blue">{{ clientDetails.first_name }} {{ clientDetails.last_name }} </span>  Username: {{clientDetails.username}} 
            <v-btn @click.prevent="showEditForm = !showEditForm" outlined color="primary" class="mx-2 my-3"
              >Edit<v-icon dark>
        mdi-pencil
      </v-icon></v-btn
            >
            <v-btn @click.prevent="checkForEmail" outlined color="red" class="mx-2"
              >Reset <v-icon dark>
        mdi-email-lock
      </v-icon></v-btn>
      <v-btn
            color="#9948B6ED"
            dark
            class="mx-2 my-3"
            v-bind="attrs"
            v-on="on"
            @click.prevent="emailRecipient"
            title="Email"
            outlined
          >Email
            <v-icon>mdi-email</v-icon></v-btn></v-card
          ></v-col
        >
      </v-row>
    </v-container>
    <v-container class="edit-profile" fill-height fluid v-if="showEditForm">
      <v-row justify="center" align="center">
        
        <v-col justify="center" align="center" lg="6" md="12">
          <v-card
            class="rounded-xl mx-auto pb-4"
            color="gray lighten-4"
            min-height="100%"
          >
            <edit-profile-form></edit-profile-form>
            <v-btn @click="showEditForm = !showEditForm">Close</v-btn>
          </v-card>
        </v-col>
       
      </v-row>
    </v-container>
    <client-event-table py-7></client-event-table>
    <client-active-package-table></client-active-package-table>
    <client-purchase-history-table></client-purchase-history-table>
  </v-container>
</template>

<script>
import ClientActivePackageTable from "../components/ClientActivePackageTable.vue";
import ClientPurchaseHistoryTable from "../components/ClientPurchaseHistoryTable.vue";
import ClientEventTable from "../components/ClientEventTable.vue";
import EditProfileForm from "../components/EditProfileForm.vue";
import clientDetailService from "../services/ClientDetailService";
import authService from "../services/AuthService";


export default {
  name: "client-details-admin-view",
  components: {
    ClientActivePackageTable,
    ClientPurchaseHistoryTable,
    EditProfileForm,
    ClientEventTable,
  },
  data() {
    return {
      showEditForm: false,
      clientDetails: {},
      snackBarReconcileWarning: false,
      snackBarEmailReset: false,
      timeout: -1,
      emailLink: "https://mail.google.com/mail/u/0/?fs=1&tf=cm&to=",
    };
  },
  methods: {
    emailRecipient() {
      if (this.$store.state.clientDetails.email.length > 0) {
        this.emailLink = this.emailLink + this.$store.state.clientDetails.email
         window.location.href = this.emailLink;
      } else {
        alert("User has no email")
      }
      
    },
    getClientDetails() {
      clientDetailService.getClientDetailsByClientId(this.$route.params.clientId).then((response) => {
        if (response.data.client_id != 0) {
          this.clientDetails = response.data;
          this.$store.commit("SET_CLIENT_DETAILS", response.data);
          if (this.clientDetails.redFlag == true) {
            this.snackBarReconcileWarning = true
          } else {
            this.snackBarReconcileWarning = false;
          }
        }
      });
    },
    checkForEmail() {
      if (this.$store.state.clientDetails.email.length > 0) {
        this.snackBarEmailReset = true
      } else {
        alert("This User Does Not Have An Email")
      }
    },
    allowEmailReset() {
      authService.emailResetLink(this.$store.state.clientDetails.email).then((response)=> {
        if (response.status == 200) {
          this.snackBarEmailReset = false;
          alert("Email Sent to: " + response.data)
          
        } else {
          this.snackBarEmailReset = false;
          alert("Error sending email")
        }
      })
      .catch((error) => {
            const response = error.response;
            this.registrationErrors = true;
            if (response.status === 400 || response.status === 404) {
              alert("Error email not found")
            }
          });
    },
  },
  created() {
    if (this.$store.state.user.username == 'admin') {
    clientDetailService.getClientDetailsByClientId(this.$route.params.clientId).then((response) => {
        if (response.data.client_id != 0) {
          this.clientDetails = response.data;
          this.$store.commit("SET_CLIENT_DETAILS", response.data);
          // alert("view")
          if (this.clientDetails.redFlag == true) {
            this.snackBarReconcileWarning = true
          }
        }
      });

    this.clientDetails = this.$store.state.clientDetails;

    this.$root.$refs.C = this;
    } else {
      this.$router.push("/")
    }
  },
};
</script>

<style>
</style>