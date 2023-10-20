<template>
  <div>
    <upload-client-component></upload-client-component>
    <upload-teacher-component></upload-teacher-component>
    <upload-event-component></upload-event-component>
  </div>
</template>
  
  <script>
import uploadClientComponent from "../components/uploadClientComponent.vue";
import uploadTeacherComponent from "../components/uploadTeacherComponent.vue";
import uploadEventComponent from "../components/uploadEventComponent.vue"

export default {
  name: "database-upload",
  components: { uploadClientComponent, uploadTeacherComponent, uploadEventComponent },
  data() {
    return {
    };
  },
  created() {
    if (this.$store.state.user.username != "admin") {
      this.$router.push({ name: "home" });
    }
  },
  methods: {},
};
</script>
  
  <style>
</style>