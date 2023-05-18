<template>
  <v-card class="mx-auto my-12 pb-12">
    <v-row justify="center" align="center">
      <v-spacer></v-spacer>
      <v-col lg="4" sm="10" justify="center" align="center">
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        
        <v-form class="form-signin" @submit.prevent="login">
          <h1 class="h3 mb-3 font-weight-normal">Please sign in.</h1>
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
            Thank you for registering! Please sign in.
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
          <v-btn type="submit">Sign in</v-btn> 
          <br>
          <a v-on:click="goToLogout()"> Don't have an account? Click here to register. </a>
          <br />
         
          <v-row justify="center">
    <v-dialog
      v-model="dialog"
      persistent
      max-width="600px"
    >
  
      <v-card>
        <v-card-title>
          <span class="text-h5">Enter Email For Reset Link</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>

              <v-col cols="12">
                <v-text-field
                  label="Email*"
                  v-model="emailToSend"
                  required
                  :rules="emailRules"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="blue darken-1"
            text
            @click="dialog = false"
          >
            Close
          </v-btn>
          <v-btn
            color="blue darken-1"
            text
            @click="sendEmailLink"
          >
            Send
          </v-btn>
        </v-card-actions>
      </v-card>
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          color="deep-orange lighten-2"
          dark
          v-bind="attrs"
          v-on="on"
          text
        >
          Forgot LOGIN/PASSWORD
        </v-btn>
      </template>
    </v-dialog>
  </v-row>
          <br>
          <br>
          <br>
          <br>
          <br>
          <br>
          <br>
          <br>
          <br>
          <br>
          <br>

          <div></div>
        </v-form>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
  </v-card>
</template>

<script>
// import HeaderLogo from "../components/HeaderLogo.vue";
import authService from "../services/AuthService";

export default {
  name: "login",
  components: {  },
  data() {
    return {
      user: {
        username: "",
        password: "",
      },
      emailToSend: '',
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
      emailRules: [
      (v) => !!v || "E-mail is required",
      (v) => /.+@.+\..+/.test(v) || "E-mail must be valid",
    ],
      invalidCredentials: false,
      clientProfile: {},
      dialog: false,
    };
  },
  created() {},
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
    sendEmailLink() {
      if (this.emailToSend.length == 0) {
        alert("Please Include Your Email")
      } else {
        authService.emailResetLink(this.emailToSend).then((response)=> {
        if (response.status == 200) {
          
          alert("Email Reset Link Sent to: " + response.data + "\n" + "Please check Your Spam" + "\n" + "Contact Owner If You Did Not Receive Email")
          this.dialog = false;
        } else {
          
          alert("Error sending email")
        }
      })
      .catch((error) => {
            const response = error.response;
            this.registrationErrors = true;
            if (response.status === 400 || response.status === 404) {
              alert("Error email not found")
            }
          });
      }

    } 
  },
};
</script>

<style>
.form-signin {
  display: flex;
  flex-direction: column;
}
</style>
