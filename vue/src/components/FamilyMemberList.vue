<template>
  <v-container>
    <v-data-table
      v-model="selectedClientsFromList"
      :headers="headers"
      :items="returnListOfFamilyMembers"
      item-key="family_id"
      sort-by="family_name"
      show-select
      class="elevation-1"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-btn
            color="primary"
            dark
            class="mb-2"
            v-bind="attrs"
            v-on="on"
            @click.prevent="sendToFamilyPage"
          >
            <v-icon> mdi-keyboard-return</v-icon>
          </v-btn>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-toolbar-title>{{ family.family_name }}</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-btn
            color="primary"
            dark
            class="mb-2 mr-3"
            v-bind="attrs"
            v-on="on"
            @click.prevent="emailRecipients"
            title="Email Selected Client(s)"
          >
            <v-icon>mdi-order-bool-ascending-variant</v-icon>
            <v-icon>mdi-email</v-icon>
          </v-btn>
          
          <v-btn
            color="#9948B6ED"
            dark
            class="mb-2"
            v-bind="attrs"
            v-on="on"
            @click.prevent="emailRecipientsFromEmailList"
            title="Email List"
          >
            <v-icon>mdi-email-plus</v-icon>
          </v-btn>
          <v-divider class="mx-4" inset vertical></v-divider>
          Member Count: {{ family.listOfFamilyMembers.length }}
          <v-spacer></v-spacer>
          <v-dialog v-model="dialog" max-width="500px" persistent>
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
                Add Member
              </v-btn>
            </template>
            <v-card>
              <!-- Add a Client Form starts here -->
              <v-card-title>
                <span class="text-h5">Add Client To Family</span>
                <v-spacer></v-spacer>
              </v-card-title>

              <v-card-text>
                <v-container>
                  <v-row>
                    <v-col>
                      <!-- <v-autocomplete
                    v-model="selectedClients"
                    :disabled="isUpdating"
                    :items="returnCorrectClientListToChoose"
                    :loading="loadingFirstClientList"
                    filled
                    color="blue-grey lighten-2"
                    label="Choose one or more"
                    item-text="quick_details"
                    return-object
                    multiple
                    @keypress="getSearchedFirstClientTableForAutocomplete"         
                  >
                  <template v-slot:append-item>
                      <div v-intersect="endFirstIntersect" />
                    </template>
                  </v-autocomplete> -->
                      
     
                    </v-col>
                  </v-row>
                </v-container>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <v-btn color="blue darken-1" text @click="save" > Save Client(s)</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
          <!-- For Deleting a client from the roster -->
          <v-dialog v-model="dialogDelete" max-width="500px">
            <v-card>
              <v-card-title class="text-h5"
                >Are you sure you want to remove this client?</v-card-title
              >
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="closeDelete"
                  >Cancel</v-btn
                >
                <v-btn color="blue darken-1" text @click="deleteItemConfirm"
                  >OK</v-btn
                >
                <v-spacer></v-spacer>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <!-- <template v-slot:[`item.client_id`]="{ item }">
        <v-chip :color="getColor(item)" dark>
          {{ item.client_id }}
        </v-chip>
      </template>
      <template v-slot:[`item.first_name`]="{ item }">
        <v-chip :color="getColor(item)" dark>
          {{ item.first_name }}
        </v-chip>
      </template>
      <template v-slot:[`item.last_name`]="{ item }">
        <v-chip :color="getColor(item)" dark>
          {{ item.last_name }}
        </v-chip>
      </template> -->
      <!-- <template v-slot:[`item.is_on_email_list`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_on_email_list"
          disabled
        ></v-simple-checkbox>
      </template> -->
      <template v-slot:[`item.is_on_email_list`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_on_email_list"
          disabled
        ></v-simple-checkbox>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-icon
          small
          class="mr-2"
          @click.prevent="sendToUserPageAdminView(item)"
        >
          mdi-account-search
        </v-icon>
        <v-icon small @click.prevent="RemoveClassForClient(item)" color="#933">
          mdi-close-thick
        </v-icon>
      </template>
    </v-data-table>
    <!-- <v-overlay :value="overlay">
      <v-progress-circular
        indeterminate
        size="70"
      ></v-progress-circular>
    </v-overlay> -->
  </v-container>
</template>

<script>
import FamilyService from '../services/FamilyService';

export default {
    name: "family-member-list",
    data() {
        return {
            headers: [
            { text: "Client ID", align: "start", value: "client_id" },
            {
            text: "First Name",
            value: "first_name",
            },
            { text: "Last Name", value: "last_name" },
            { text: "Phone Number", value: "phone_number", sortable: false },
            { text: "Email List", value: "is_on_email_list", sortable: true },
            { text: "Email", value: "email", sortable: false },
            { text: "Actions", value: "actions", sortable: false },
      ],
            listOfFamilyMembers: [],
            selectedClientsFromList: [],
            dialogDelete: false,
            family: {},
        }
    },
    created() {
        this.getFamilyDetails()
    },
    methods: {
        getFamilyDetails() {
            FamilyService.getFamilyDetailsByFamilyId(this.$route.params.familyId)
            .then((response) => {
                if (response.status == 200) {
                    this.family = response.data;
                    this.listOfFamilyMembers = this.family.listOfFamilyMembers;

                }
            })
        },
        sendToFamilyPage() {
      this.$router.push("/familyManagement");
    },
    },
    computed: {
        returnListOfFamilyMembers() {
            return this.listOfFamilyMembers;
        }
    }
}
</script>

<style>

</style>