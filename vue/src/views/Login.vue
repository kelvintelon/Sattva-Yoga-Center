<template>
  <div id="login" class="text-center">
    <v-container>
      <header-logo></header-logo>
      <v-row>
        <v-spacer></v-spacer>
        <v-col cols="4" justify="center" align="center">
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
              :rules="nameRules"
              label="Username"
              required
            ></v-text-field>
            <v-text-field
              v-model="user.password"
              id="password"
              :rules="nameRules"
              label="Password"
              required
            ></v-text-field>
            <!-- <label for="username" class="sr-only">Username</label>
            <input
              type="text"
              id="username"
              class="form-control"
              placeholder="Username"
              v-model="user.username"
              required
              autofocus
            /> -->
            <!-- <label for="password" class="sr-only">Password</label>
            <input
              type="password"
              id="password"
              class="form-control"
              placeholder="Password"
              v-model="user.password"
              required
            /> -->
            <!-- <router-link :to="{ name: 'register' }"
              >Need an account?</router-link
            > -->
            <v-btn v-on:click="goToLogout()">Need an account?        </v-btn>
            <br>
            <v-btn type="submit">Sign in</v-btn>
          </v-form>
        </v-col>
        <v-spacer></v-spacer>
      </v-row>
    </v-container>
  </div>
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
      invalidCredentials: false,
    };
  },
  methods: {
    login() {
      authService
        .login(this.user)
        .then((response) => {
          if (response.status == 200) {
            this.$store.commit("SET_AUTH_TOKEN", response.data.token);
            this.$store.commit("SET_USER", response.data.user);
            this.$router.push("/clientRegistration");
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
      this.$router.push({ name: "profile-page" });
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