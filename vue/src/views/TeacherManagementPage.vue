<template>
<div>
    <top-header></top-header>
  <v-container>
          <v-row>
        <v-col cols="12"> <create-teacher-form></create-teacher-form></v-col>
      </v-row>
    <v-simple-table>
    <template v-slot:default>
      <thead>
        <tr>
          <th class="text-left">
            Name
          </th>
          <th class="text-left">
            Is Active
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in desserts"
          :key="item.name"
        >
          <td>{{ item.name }}</td>
          <td>{{ item.calories }}</td>
        </tr>
      </tbody>
    </template>
  </v-simple-table>
    
  </v-container>
  </div>
</template>

<script>
import TopHeader from "../components/TopHeader2.vue";
import CreateTeacherForm from "../components/CreateTeacherForm.vue"

export default {
    name: "teacher-management",
    components: {
    TopHeader,
    CreateTeacherForm
  },
}
</script>

<style>

</style>