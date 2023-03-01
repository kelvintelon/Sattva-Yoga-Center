<template>
  <v-container>
    <top-header></top-header>

    <v-container>
      <v-row justify="center" align="center">
        <v-col cols="3">
          <v-card align="center" justify="center" >
            {{ clientDetails.first_name }} {{ clientDetails.last_name }} ||
            <v-btn @click.prevent="showEditForm = !showEditForm" outlined color="primary" class="mx-2"
              ><v-icon dark>
        mdi-pencil
      </v-icon></v-btn
            ></v-card
          ></v-col
        >
      </v-row>
    </v-container>
    <v-container class="edit-profile" fill-height fluid v-if="showEditForm">
      <v-row justify="center" align="center">
        <v-spacer></v-spacer>
        <v-col cols="4" justify="center" align="center">
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
    };
  },
  created() {
    this.clientDetails = this.$store.state.clientDetails;
  },
};
</script>

<style>
</style>