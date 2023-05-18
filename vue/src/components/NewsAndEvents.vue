<template>
   <v-card class="mx-auto my-4 rounded-xl" max-width="900px" min-width="200px" id="first-tab-id" >
      <v-card-title style="color: rgba(245, 104, 71, 0.95)"><strong>News and Events</strong></v-card-title>
      <v-textarea class="mx-4" readonly :value="newsAndEventsDescription"  v-if="$store.state.user.username != 'admin'">
      </v-textarea>
      <v-textarea class="mx-4" @keyup="updateNewsAndEventsDescription($event)" :value="newsAndEventsDescription" v-if="$store.state.user.username == 'admin'">
      </v-textarea>
    </v-card>  
</template>

<script>
 import websiteDescriptionService from "../services/WebsiteDescriptionService";

export default {
  name: "news-and-events",
  components: {},
  data() {
    return {
      selection: 0,
    packageSelection: 3,
    newsAndEventsDescription: '',
    };
  },
  methods: {
    getNewsAndEventsDescription() {
      websiteDescriptionService.getNewsAndEvents().then((response) => {
        if (response.status == 200) {
           this.newsAndEventsDescription = response.data;
        }
      })
    },
    updateNewsAndEventsDescription(event) {
      websiteDescriptionService.updateNewsAndEventsDescription({description: event.target.value}).then((response) => {
        if (response.status == 200) {
          
          // this.readOnlyDescription = newDescription.description;
        }
      });
    },
  },
  created() {
    this.getNewsAndEventsDescription();
  },
};
</script>

<style></style>
