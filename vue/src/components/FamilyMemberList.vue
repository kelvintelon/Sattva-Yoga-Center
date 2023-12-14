<template>
  <v-container>
    <v-data-table
      v-model="selectedClientsFromList"
      :headers="headers"
      :items="returnListOfFamilyMembers"
      item-key="client_id"
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
                      <v-autocomplete
                    v-model="selectedClients"
                    :disabled="isUpdating"
                    :items="returnCorrectClientListToChoose"
                    :loading="loadingFirstClientList"
                    filled
                    color="blue-grey lighten-2"
                    label="Choose one or more"
                    item-text="quick_details"
                    ref="autocompleteFamily"
                    cache-items  
                    return-object
                    multiple
                    @keypress="getSearchedFirstClientTableForAutocomplete"         
                  >
                  <template v-slot:append-item>
                      <div v-intersect="endFirstIntersect" />
                    </template>
                  </v-autocomplete>
                      
     
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
        <v-icon small @click.prevent="RemoveClientForFamily(item)" color="#933">
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
import clientDetailService from "../services/ClientDetailService";

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
            autocompleteFirstClientList: [],
            firstAutocompletePage: 1,
            pageSize: 20,
            firstAutocompleteSearch: "",
            paginatedObject: {},
            selectedClients: [],
            loadingFirstClientList: false,
            dialog: false,
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
                    this.getAutoCompletedFirstClientTable();
                }
            })
        },
        sendToFamilyPage() {
            this.$router.push("/familyManagement");
        },
        sendToUserPageAdminView(object) {
            this.$store.commit("SET_CLIENT_DETAILS", object);
            this.$router.push({
                name: "client-details-admin-view",
                params: { clientId: object.client_id },
            });
        },
        RemoveClientForFamily(client) {
            let foundClientObject = false;
            // see if the client object is already selected
            for (let j = 0; j < this.selectedClientsFromList.length; j++) {
                this.selectedClientsFromList[j].family_id = this.$route.params.familyId;
                if (this.selectedClientsFromList[j].client_id == client.client_id) {
                foundClientObject = true;
                }
            }
            // if it's not selected already then add the client object to the list of selected clients
            if (!foundClientObject) {
                client.family_id = this.$route.params.familyId;
                this.selectedClientsFromList.push(client);
            }
            // create a temp array to hold the list of clients selected, not necessary
            let temporaryList = this.selectedClientsFromList;
           
            FamilyService
                .removeFamilyMembersFromSelectedClients(temporaryList)
                .then((response) => {
                if (response.status === 200) {
                    this.overlay = false;
                    alert("Successfully deleted clients from family");
                    this.getFamilyDetails();
                    this.selectedClientsFromList = [];
                } else {
                    alert("Error deleting clients from roster");
                }
                }); // END OF REMOVING CLIENT FROM LIST
        },
        getAutoCompletedFirstClientTable() {
            clientDetailService
                .getPaginatedClientsForFamily(parseInt(this.$route.params.familyId),
                 parseInt(this.firstAutocompletePage), parseInt(this.pageSize), this.firstAutocompleteSearch)
                .then((response) => {
                if (response.status == 200) {
                    this.paginatedObject = response.data;

                    this.autocompleteFirstClientList = this.paginatedObject.listOfClients;
                    this.loadingFirstClientList = false;
                    
                } else {
                    alert("Error retrieving client information");
                }
                })
                .catch((error) => {
                const response = error.response;
                if (response.status === 401) {
                    this.$router.push("/login");
                }
                });
        },
        getSearchedFirstClientTableForAutocomplete(event){
            this.firstAutocompletePage = 1;
            var charTyped = String.fromCharCode(event.which);
            if (/[a-z\d]/i.test(charTyped)) {
            
            
                setTimeout(
                () =>
                
                    ( this.setFirstAutocompleteSearch(event.target.value) 
                    ),
                    
                250
                );
                
            }
        },
        setFirstAutocompleteSearch(search) {
            if (search != undefined && search.length > 0) {
                this.firstAutocompleteSearch = search;
                this.firstAutocompletePage = 1;
                // alert(this.firstAutocompleteSearch)
            } else {
                this.firstAutocompleteSearch = "";
            }
            this.loadingFirstClientList = true;
            this.getAutoCompletedFirstClientTable();
        },
        endFirstIntersect(entries, observer, isIntersecting) {
            if (isIntersecting && this.firstAutocompleteSearch == "") {
                // alert("intersected")

            
                this.firstAutocompletePage++;

                setTimeout(
                () =>
                    // alert(this.search)
                    ( this.getAutoCompletedFirstClientTable()
                    ),
                    // this.getPaginatedClientTable(),

                    // logic goes in here
                250
                );
                
            }
        },
        close() {
            this.dialog = false;
        },
        save() {
            
            for (let index = 0; index < this.selectedClients.length; index++) {
                this.selectedClients[index].family_id = this.$route.params.familyId;
                // END OF LOOP BLOCK
            }
            if (this.selectedClients.length > 0) {
                FamilyService
                .registerMultipleClientsForFamily(this.selectedClients)
                .then((response) => {
                    if (response.status == 201) {
                      
                      this.autocompleteFirstClientList = [];
                      this.firstAutocompletePage = 1;
                      this.$refs.autocompleteFamily.cachedItems = [];
                      alert("Successfully added clients to family");
                      this.getFamilyDetails();
                      this.selectedClients = [];
                    } else {
                      alert("Error adding clients to family");
                    }
                });
                this.close();
            } else {
                alert("Please select at least one client")
            }
        },
    },  
    computed: {
        returnListOfFamilyMembers() {
            return this.listOfFamilyMembers;
        },
        returnCorrectClientListToChoose() {
            return this.autocompleteFirstClientList
        },
    },
    watch: {
    dialog(val) {
      val || this.close();
    },
    dialogDelete(val) {
      val || this.closeDelete();
    },
  },
}
</script>

<style>

</style>