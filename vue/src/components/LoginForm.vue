<!-- <template>
  <v-card class="mx-auto my-12">
    <v-card-title>member login</v-card-title>
    <v-form v-model="valid">
      <v-row align="center" class="mx-auto">
        <v-col>
          <v-text-field
            v-model="username"
            :rules="nameRules"
            label="username"
            autofocus
            required
          >
          </v-text-field>
          <v-text-field
            v-model="user.password"
            :rules="[(v) => !!v || 'You must enter your password.']"
            required
          >
          </v-text-field>
          <v-btn color="amber">
            <v-icon> mdi-login-variant </v-icon>log in</v-btn
          >
        </v-col>
      </v-row>
    </v-form>
  </v-card>
</template>

<script>
import authService from "../services/AuthService";

export default {
  name: "login-form",
  components: {},
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
  },
};
</script>

<style></style> -->
