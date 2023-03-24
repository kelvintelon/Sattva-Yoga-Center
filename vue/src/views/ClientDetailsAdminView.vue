<template>
  <v-container>
    <top-header></top-header>

    <v-container>
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
        <v-col cols="5">
          <v-card align="center" justify="center" >
            {{ clientDetails.first_name }} {{ clientDetails.last_name }} ||
            <v-btn @click.prevent="showEditForm = !showEditForm" outlined color="primary" class="mx-2"
              >Edit<v-icon dark>
        mdi-pencil
      </v-icon></v-btn
            >
            <v-btn @click.prevent="checkForEmail" outlined color="red" class="mx-2"
              >Reset <v-icon dark>
        mdi-email-lock
      </v-icon></v-btn></v-card
          ></v-col
        >
      </v-row>
    </v-container>
    <v-container class="edit-profile" fill-height fluid v-if="showEditForm">
      <v-row justify="center" align="center">
        <v-spacer></v-spacer>
        <v-col cols="5" justify="center" align="center">
          <v-card
            class="mx-auto my-12 rounded-xl pb-12 pt-12"
            color="gray lighten-4"
            min-height="100%"
          >
            <edit-profile-form></edit-profile-form>
          </v-card>
        </v-col>
        <v-spacer></v-spacer>
      </v-row>
    </v-container>
    <client-event-table></client-event-table>
    <client-active-package-table></client-active-package-table>
    <client-purchase-history-table></client-purchase-history-table>
  </v-container>
</template>

<script>
import TopHeader from "../components/TopHeader.vue";
import ClientActivePackageTable from "../components/ClientActivePackageTable.vue";
import ClientPurchaseHistoryTable from "../components/ClientPurchaseHistoryTable.vue";
import ClientEventTable from "../components/ClientEventTable.vue";
import EditProfileForm from "../components/EditProfileForm.vue";
import clientDetailService from "../services/ClientDetailService";
import authService from "../services/AuthService";


export default {
  name: "client-details-admin-view",
  components: {
    TopHeader,
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
    };
  },
  methods: {
    getClientDetails() {
      clientDetailService.getClientDetailsByClientId(this.$route.params.clientId).then((response) => {
        if (response.data.client_id != 0) {
          this.clientDetails = response.data;
          this.$store.commit("SET_CLIENT_DETAILS", response.data);
          if (this.clientDetails.redFlag == true) {
            this.snackBarReconcileWarning = true
          }
        }
      });
    },
    checkForEmail() {
      if (this.$store.state.clientDetails.email != null) {
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
    clientDetailService.getClientDetailsByClientId(this.$route.params.clientId).then((response) => {
        if (response.data.client_id != 0) {
          this.clientDetails = response.data;
          this.$store.commit("SET_CLIENT_DETAILS", response.data);
          if (this.clientDetails.redFlag == true) {
            this.snackBarReconcileWarning = true
          }
        }
      });

    this.clientDetails = this.$store.state.clientDetails;

    this.$root.$refs.C = this;
  },
};
</script>

<style>
</style>