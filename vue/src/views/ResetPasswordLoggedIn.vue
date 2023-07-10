<template>
    <v-container>
    <v-card>
        <v-card-title>
            <span class="text-h5" style="color: rgba(245, 104, 71, 0.95)">Enter Email For Reset Link</span>
        </v-card-title>
        <v-card-text>
            <v-container>
                <v-row>

                    <v-col cols="12">
                        <v-text-field label="Email*" v-model="emailToSend" required :rules="emailRules"></v-text-field>
                    </v-col>
                </v-row>
            </v-container>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="resetField">
                Cancel
            </v-btn>
            <v-btn color="blue darken-1" text @click="sendEmailLink">
                Send
            </v-btn>
        </v-card-actions>
    </v-card>
</v-container>
</template>
  
<script>
// import HeaderLogo from "../components/HeaderLogo.vue";
import authService from "../services/AuthService";

export default {
    name: "reset-password-logged-in",
    components: {},
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
    created() { },
    methods: {
        resetField() {
            this.emailToSend = ""
        },
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
                authService.emailResetLink(this.emailToSend).then((response) => {
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
  