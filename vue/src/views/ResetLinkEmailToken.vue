<template>
  <v-container fill-height fluid>
    <v-row justify="center" align="center">
      <v-spacer></v-spacer>
      <v-col cols="4" justify="center" align="center">
        <header-logo></header-logo>
        <h1 class="h3 mb-3 font-weight-normal">Please Choose What To Reset</h1>
        <br />
        <v-row>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            @click.prevent="checkTokenForResetUsernameAndPassword"
            v-if="!dialog"
          >
            Reset Login
          </v-btn>
          <v-row justify="center" align="center" v-if="dialog"
            ><v-col justify="center" align="center">
              <v-card class="px-4 pa-4">
                <v-form
                  class="form-register"
                  @submit.prevent="resetUsernameAndPassword"
                  lazy-validation
                  v-model="valid"
                  v-if="dialog"
                >
                  <h2 class="h3 mb-3 font-weight-normal">
                    Create New Account Details
                  </h2>
                  <div
                    class="alert alert-danger"
                    role="alert"
                    v-if="resetPasswordErrors"
                    style="color: red"
                  >
                    {{ resetPasswordErrorsMsg }}
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
                  <br />
                  <v-btn
                    color="primary"
                    class="btn btn-lg btn-primary btn-block"
                    type="submit"
                  >
                    Reset Account
                  </v-btn>
                  <br />
                  <v-btn color="red" block @click="dialog = false"
                    >Cancel</v-btn
                  >
                  <div></div>
                </v-form>
              </v-card> </v-col
          ></v-row>
          <v-spacer></v-spacer></v-row
        ><v-spacer></v-spacer>
        <br />
        <br />
        <v-row>
          <v-spacer></v-spacer>
          <v-btn
            color="primary"
            @click.prevent="checkTokenForResetPassword"
            v-if="!dialog2"
          >
            Reset Password</v-btn
          >
          <v-row justify="center" align="center" v-if="dialog2"
            ><v-col justify="center" align="center">
              <v-card class="px-4 pa-4">
                <v-form
                  class="form-register"
                  @submit.prevent="resetPassword"
                  lazy-validation
                  v-model="valid"
                  v-if="dialog2"
                >
                  <h2 class="h3 mb-3 font-weight-normal">Reset Password</h2>
                  <div
                    class="alert alert-danger"
                    role="alert"
                    v-if="resetPasswordErrors"
                     style="color: red"
                  >
                    {{ resetPasswordErrorsMsg }}
                  </div>
                  <v-text-field
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    @click:append="show1 = !show1"
                    v-model="passwordObject.password"
                    id="password"
                    label="Password"
                    :rules="passwordRules"
                    required
                  ></v-text-field>
                  <v-text-field
                    :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show1 ? 'text' : 'password'"
                    @click:append="show1 = !show1"
                    v-model="passwordObject.confirmPassword"
                    id="confirmPassword"
                    label="Confirm Password"
                    :rules="passwordRules"
                    required
                  ></v-text-field>
                  <br />
                  <v-btn
                    color="primary"
                    class="btn btn-lg btn-primary btn-block"
                    type="submit"
                  >
                    RESET
                  </v-btn>
                  <br />
                  <v-btn color="red" block @click="dialog2 = false"
                    >Cancel</v-btn
                  >
                  <div></div>
                </v-form>
              </v-card> </v-col
          ></v-row>
          <v-spacer></v-spacer
        ></v-row>
        <br><br>
          <v-row justify="center">
    <v-dialog
      v-model="dialog3"
      persistent
      max-width="600px"
    >
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          color="deep-orange lighten-2"
          dark
          v-bind="attrs"
          v-on="on"
          @click="sendEmailTokenRetrieveUsername"
        >
          Retrieve Username
        </v-btn>
      </template>
      <v-card>
        <v-card-title>
          <span class="text-h5">Please Remember This Username</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>

              <v-col cols="12">
                 <span class="text-h4" style="color: blue">{{this.retrievedUsername}}</span>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="blue darken-1"
            text
            @click="dialog3 = false"
          >
            Close
          </v-btn>
          <v-btn
            color="blue darken-1"
            text
            @click="showResetPassword"
          >
            Would You Like To Reset password?
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
    <v-row></v-row>
    <br>
    <br>
  </v-container>
</template>

<script>
import HeaderLogo from "../components/HeaderLogo.vue";
import authService from "../services/AuthService";

export default {
  name: "email-link-reset-token",
  components: { HeaderLogo },
  data() {
    return {
      user: {
        username: "",
        password: "",
        confirmPassword: "",
        token: "",
      },
      passwordObject: {
        password: "",
        confirmPassword: "",
        token: "",
      },
      retrievedUsername: "",
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
      dialog: false,
      dialog2: false,
      dialog3: false,
      resetPasswordErrors: false,
      resetPasswordErrorsMsg: "",
    };
  },
  created() {
    this.$router.replace(`${this.$route.path}?token=${this.$route.query.token}`)
    // call method to validate token
    this.checkTokenForValidation();
  },
  methods: {
    showResetPassword() {
      this.dialog3 = false;
      this.dialog2 = true;
    },
    sendEmailTokenRetrieveUsername() {
      authService.sendEmailTokenRetrieveUsername(this.$route.query.token)
      .then((response) => {
        if (response.status == 200) {
          this.retrievedUsername = response.data;
        }
      })
      .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            }
          });
    },
    checkTokenForValidation() {
      authService
        .checkIfTokenIsValid(this.$route.query.token)
        .then((response) => {
          if (response.status == 200) {
            if (response.data == "Invalid JWT") {
              confirm("Invalid Reset Link. Please contact your manager");
              this.$router.push("/login");
            }
          } else {
            ("Error validating link");
          }
        })
        .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            }
          });
    },
    checkTokenForResetUsernameAndPassword() {
      authService
        .checkIfTokenIsValid(this.$route.query.token)
        .then((response) => {
          if (response.status == 200) {
            if (response.data == "Invalid JWT") {
              confirm("Invalid Reset Link. Please contact the owner.");
              this.$router.push("/login");
            }
            this.dialog = true;
            this.dialog2 = false;
          } else {
            ("Error validating link");
          }
        })
        .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            }
          });
    },
    checkTokenForResetPassword() {
      authService
        .checkIfTokenIsValid(this.$route.query.token)
        .then((response) => {
          if (response.status == 200) {
            if (response.data == "Invalid JWT") {
              confirm("Invalid Reset Link. Please contact the owner.");
              this.$router.push("/login");
            }
            this.dialog2 = true;
            this.dialog = false;
          } else {
            ("Error validating link");
          }
        })
        .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            }
          });
    },
    resetUsernameAndPassword() {
      if (this.user.password != this.user.confirmPassword) {
        this.resetPasswordErrors = true;
        this.resetPasswordErrorsMsg =
          "Password & Confirm Password do not match.";
      } else {
        this.user.token = this.$route.query.token;
        authService
          .resetUsernameAndPassword(this.user)
          .then((response) => {
            if (response.status == 200) {
              alert("Successfully reset username and password");
              this.$router.push("/login");
            }
          })
          .catch((error) => {
            const response = error.response;
            this.resetPasswordErrors = true;
            if (response.status === 400) {
              this.resetPasswordErrorsMsg = "Bad Request: Validation Errors";
              alert(error.response.data.message)
            }
          });
      }
    },
    resetPassword() {
      if (this.passwordObject.password != this.passwordObject.confirmPassword) {
        this.resetPasswordErrors = true;
        this.resetPasswordErrorsMsg =
          "Password & Confirm Password do not match.";
      } else {
        this.passwordObject.token = this.$route.query.token;
        authService
          .resetPassword(this.passwordObject)
          .then((response) => {
            if (response.status == 200) {
              alert("Successfully reset password");
              this.$router.push("/login");
            }
          })
          .catch((error) => {
            const response = error.response;
            this.resetPasswordErrors = true;
            if (response.status === 400) {
              this.resetPasswordErrorsMsg = "Bad Request: Validation Errors";
              alert(error.response.data.message)
            }
          });
      }
    },
  },
};
</script>

<style>
</style>