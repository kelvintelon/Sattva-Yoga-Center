<template>
  <div>
    <top-header></top-header>
    <v-container>
      <v-row>
        <v-col cols="6"> <teacher-table-list></teacher-table-list></v-col>
        <!-- <v-col cols="6"> <create-teacher-form></create-teacher-form></v-col> -->
      </v-row>
      <!--
      <v-simple-table>
        <template v-slot:default>
          <thead>
            <tr>
              <th class="text-left">Name</th>
              <th class="text-left">Is Active</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in desserts" :key="item.name">
              <td>{{ item.name }}</td>
              <td>{{ item.calories }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      -->
    </v-container>
  </div>
</template>

<script>
import TopHeader from "../components/TopHeader.vue";
// import CreateTeacherForm from "../components/CreateTeacherForm.vue";
import TeacherTableList from "../components/TeacherTableList.vue"

export default {
  name: "teacher-management",
  components: {
    TopHeader,
    // CreateTeacherForm,
    TeacherTableList
  },
};
</script>

<style>
</style>