<template>
  <v-container fill-height fluid>
    <v-row justify="center" align="center">
      <v-spacer></v-spacer>
      <v-col cols="4" justify="center" align="center">
        <header-logo></header-logo>
        <v-form class="form-signin" @submit.prevent="login">
          <h1 class="h3 mb-3 font-weight-normal">Please Sign In</h1>
          <div
            class="alert alert-danger"
            role="alert"
            v-if="invalidCredentials"
          >
            Invalid username and password!
          </div>
          <div
            class="alert alert-success"
            role="alert"
            v-if="this.$route.query.registration"
          >
            Thank you for registering, please sign in.
          </div>
          <v-text-field
            v-model="user.username"
            id="username"
            :rules="userNameRules"
            label="Username"
            required
          ></v-text-field>
          <v-text-field
            v-model="user.password"
            :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
             :type="show1 ? 'text' : 'password'"            
            @click:append="show1 = !show1"
            id="password"
            :rules="passwordRules"
            label="Password"
            required
          ></v-text-field>
          <v-btn v-on:click="goToLogout()"> Register </v-btn>
          <br />
          <v-btn type="submit">Sign in</v-btn>
          <div>
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
            <br />
          </div>
        </v-form>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
  </v-container>
</template>

<script>
import HeaderLogo from "../components/HeaderLogo.vue";
import authService from "../services/AuthService";


export default {
  name: "login",
  components: { HeaderLogo },
  data() {
    return {
      user: {
        username: "",
        password: "",
      },
      show1: false,
      userNameRules: [
        (v) => !!v || "Username is required",
        (v) =>
          (v && v.length <= 30) || "Username must be less than 30 characters",
      ],
      passwordRules: [
        (v) => !!v || "Password is required",
        (v) =>
          (v && v.length <= 30) || "Password must be less than 30 characters",
      ],
      invalidCredentials: false,
      clientProfile: {},
    };
  },
  created() {
    
  },
  methods: {
    login() {
      authService
        .login(this.user)
        .then((response) => {
          if (response.status == 200) {
            this.$store.commit("SET_AUTH_TOKEN", response.data.token);
            this.$store.commit("SET_USER", response.data.user);
            this.$router.push("/loading");

          }
        })
        .catch((error) => {
          const response = error.response;

          if (response.status === 401) {
            this.invalidCredentials = true;
          }
        });
    },
    goToLogout() {
      this.$router.push({ name: "register" });
    },
  },
};
</script>

<style>
.form-signin {
  display: flex;
  flex-direction: column;
}
</style>