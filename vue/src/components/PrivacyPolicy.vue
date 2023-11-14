<template>
  <v-container>
    <v-row justify="space-around">
    <v-col cols="auto">
      <v-dialog
        transition="dialog-bottom-transition"
        max-width="600"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn
            color="primary"
            outlined
            x-small
            v-bind="attrs"
            v-on="on"
          >Privacy Policy</v-btn>
        </template>
        <template v-slot:default="dialog">
          <v-card>
            <v-toolbar
              color="primary"
              dark
            >Privacy Policy</v-toolbar>
            <v-card-text>
                <section class="mt-3">
        <p>
            This Privacy Policy outlines how we collect, use, and protect your personal information in compliance with
             the ePrivacy Directive 2002/58/EC and other applicable data protection laws. By engaging in the following 
             actions on our website, including registering as a user, booking a class, or purchasing a class or package, 
             you explicitly consent to the practices described in this policy.
        </p>
    </section>
                <section>
        <h2 >1. Data Collection:</h2>
        <p>
            When you register as a user, book a class, or make a purchase, we collect the necessary information
            to provide you with the requested services. This may include personal details such as your name, email
            address, contact information, and transaction data.
        </p>
    </section>

    <section>
        <h2>2. Cookies:</h2>
        <p>
            We utilize essential cookies to store information that enables core website functionality. These cookies
            are necessary for the proper functioning of our platform and may include session cookies that are deleted
            when you close your browser and persistent cookies that remain on your device for a specified period. By
            using our website, you agree to the placement of these cookies on your device.
        </p>
    </section>

    <section>
        <h2>3. Purpose of Data Processing:</h2>
        <p>
            The information collected is used for the following purposes:
        </p>
        <ul>
            <li>To facilitate your registration and provide access to our services.</li>
            <li>To process and fulfill your class bookings or purchases.</li>
            <li>To communicate essential information related to your account or transactions.</li>
            <li>To improve our website's functionality and user experience.</li>
        </ul>
    </section>

    <section>
        <h2>4. Data Security:</h2>
        <p>
            We implement security measures to protect your personal information from unauthorized access, disclosure,
            alteration, and destruction. Despite our efforts, no data transmission over the internet or electronic
            storage method can be guaranteed as 100% secure.
        </p>
    </section>

    <section>
        <h2>5. Third-Party Disclosure:</h2>
        <p>
            We do not sell, trade, or otherwise transfer your personal information to external parties. However,
            non-personally identifiable information may be provided to trusted third parties for the purpose of
            website analytics and improvement.
        </p>
    </section>

    <section>
        <h2>6. Your Rights:</h2>
        <p>
            You have the right to access, correct, update, or delete your personal information. To exercise these rights
            or if you have any questions regarding our privacy practices, please contact us at
            <a href="mailto:info@sattva-yoga-center.com">info@sattva-yoga-center.com</a>.
        </p>
    </section>

    <section>
        <h2>7. Changes to the Privacy Policy:</h2>
        <p>
            We reserve the right to update or modify this Privacy Policy at any time. Any changes will be posted on this
            page, and the effective date will be indicated at the bottom of the policy. By continuing to use our website
            after changes to this policy, you acknowledge and agree to the updated terms.
        </p>
        <p>
            <em>
                Effective Date: 11/13/2023
            </em>
            
        </p>
    </section>
            </v-card-text>
            <v-card-actions class="justify-end">
              <v-btn
                text
                @click="dialog.value = false"
              >Close</v-btn>
            </v-card-actions>
          </v-card>
        </template>
      </v-dialog>
    </v-col>
  </v-row>
  </v-container>
</template>

<script>
export default {
    name: "PrivacyPolicy"

}
</script>

<style>

</style>