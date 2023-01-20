<template>
  <v-container fill-height fluid>
    <v-row justify="center" align="center">
      <v-spacer></v-spacer>
      <v-col cols="4" justify="center" align="center">
        <header-logo></header-logo>
        <v-form
          class="form-register"
          @submit.prevent="register"
          lazy-validation
          v-model="valid"
        >
          <h1 class="h3 mb-3 font-weight-normal">Create Account</h1>
          <div
            class="alert alert-danger"
            role="alert"
            v-if="registrationErrors"
          >
            {{ registrationErrorMsg }}
          </div>
          <v-text-field
            v-model="user.username"
            id="username"
            label="Username"
            :rules="userNameRules"
            required
          ></v-text-field>
          <v-text-field
          :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
             :type="show1 ? 'text' : 'password'"            
            @click:append="show1 = !show1"
            v-model="user.password"
            id="password"
            label="Password"
            :rules="passwordRules"
            required
          ></v-text-field>
          <v-text-field
          :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
             :type="show1 ? 'text' : 'password'"            
            @click:append="show1 = !show1"
            v-model="user.confirmPassword"
            id="confirmPassword"
            label="Confirm Password"
            :rules="passwordRules"
            required
          ></v-text-field>
          <v-btn
            class="btn btn-lg btn-primary btn-block"
            v-on:click="goToPage()"
          >
            Have an existing account?
          </v-btn>
          <br />
          <v-btn class="btn btn-lg btn-primary btn-block" type="submit">
            Create New Account
          </v-btn>
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
import authService from '../services/AuthService';
import HeaderLogo from "../components/HeaderLogo.vue";

export default {
  name: "register",
  components: {
    HeaderLogo,
  },
  data() {
    return {
      valid: true,
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
      user: {
        username: '',
        password: '',
        confirmPassword: '',
        role: 'user',
      },
      registrationErrors: false,
      registrationErrorMsg: 'There were problems registering this user.',
    };
  },
  methods: {
    goToPage() {
      this.$router.push({ name: "login" });
    },
    register() {
      if (this.user.username == "admin") {
        this.user.role = "admin"
      }

      if (this.user.password != this.user.confirmPassword) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password & Confirm Password do not match.';
      } else {
        authService
          .register(this.user)
          .then((response) => {
            if (response.status == 201) {
              this.$router.push({
                path: '/login',
                query: { registration: 'success' },
              });
            }
          })
          .catch((error) => {
            const response = error.response;
            this.registrationErrors = true;
            if (response.status === 400) {
              this.registrationErrorMsg = 'Bad Request: Validation Errors';
            }
          });
      }
    },
    clearErrors() {
      this.registrationErrors = false;
      this.registrationErrorMsg = 'There were problems registering this user.';
    },
  },
};
</script>

<style>
.form-register {
  display: flex;
  flex-direction: column;
}

.text-center {
  display: flex;
  align-content: center;
}
</style>
