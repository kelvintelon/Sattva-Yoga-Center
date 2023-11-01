<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1
        v-if="$store.state.user.username != 'admin'"
        style="color: rgba(245, 104, 71, 0.95)"
      >
        Active Packages
      </h1>
      <v-spacer></v-spacer
    ></v-row>
    <br />
    <v-snackbar
      v-if="$store.state.user.username == 'admin'"
      v-model="snackBarReconcilePackages"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
      :timeout="timeout"
    >
      Would You Like To Reconcile Missing Payments With Current Active Package
      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarReconcilePackages = false"
          left
        >
          Close
        </v-btn>
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click.prevent="allowClientReconcile"
        >
          Continue
        </v-btn>
      </template>
    </v-snackbar>
    <v-snackbar
      v-if="$store.state.user.username == 'admin'"
      v-model="snackBarReconcilePackagesSuccessful"
      color="green darken-2"
      elevation="24"
      :vertical="vertical"
      pill
    >
      Successfully Reconciled For Missing Payments
    </v-snackbar>
    <v-data-table
      :headers="headers"
      :items="packages"
      class="elevation-5"
      :sort-by.sync="sortBy"
      :sort-desc.sync="sortDesc"
      @update:sort-by="sortTable"
      @update:sort-desc="sortTable"
      :loading="loading"
      loading-text="Loading... Please wait"
      :options.sync="options"
      :server-items-length="totalPackagesPurchased"
      hide-default-footer
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Active Packages</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
          <v-dialog
            v-model="dialog"
            max-width="500px"
            persistent
            v-if="$store.state.user.username == 'admin'"
          >
            <template v-slot:activator="{ on, attrs }">
              <v-btn color="primary" dark class="mb-2" v-bind="attrs" v-on="on">
                Add a package
              </v-btn>
            </template>
            <v-card>
              <!-- Add a Package Form starts here -->
              <v-card-title>
                <span class="text-h5" style="color: rgba(245, 104, 71, 0.95)"
                  >Add a Package</span
                >
              </v-card-title>

              <v-card-title>
                <v-container>
                  <v-row>
                    <v-col>
                      <v-select
                        label="Choose"
                        :items="availablePackages"
                        v-model="selectedPackages"
                        item-text="description"
                        multiple
                        return-object
                      ></v-select>
                      <v-row v-if="showGiftCardForm">
                        <v-col>
                          <v-text-field
                            v-model="clientCheckout.emailForGift"
                            :counter="30"
                            label="Email for Gift Card"
                          ></v-text-field>
                        </v-col>
                        <v-checkbox
                        class="mt-4"
                          v-if="saveEmailForGiftCardCheckbox"
                          @click="saveEmail"
                          v-model="clientCheckout.saveEmailGiftCardPurchase"
                          label="Save?"
                        ></v-checkbox>
                      </v-row>
                      <v-row v-if="showGiftCardForm">
                        <v-col>
                          <v-text-field
                            v-model.number="
                              selectedPackages[giftCardIndex].package_cost
                            "
                            class="mt-0 pt-0"
                            type="number"
                            label="Gift Card: $"
                            min="10"
                          ></v-text-field>
                        </v-col>
                      </v-row>
                      <v-row v-if="showRenewalDatePicker">
                        <v-col>
                          <v-menu
                            ref="menu"
                            v-model="menu"
                            :close-on-content-click="false"
                            :nudge-right="40"
                            transition="scale-transition"
                            offset-y
                            min-width="auto"
                          >
                            <template v-slot:activator="{ on, attrs }">
                              <v-text-field
                                v-model="clientCheckout.renewalDate"
                                label="Renewal Date"
                                prepend-icon="mdi-calendar"
                                readonly
                                v-bind="attrs"
                                v-on="on"
                              ></v-text-field>
                            </template>
                            <v-date-picker
                              v-model="clientCheckout.renewalDate"
                              @input="menu = false"
                            ></v-date-picker>
                          </v-menu>
                        </v-col>
                        <v-col>
                          <v-text-field
                                v-model.number="clientCheckout.iterations"
                                label="Iterations"
                                type="number"
                                min="1"
                                max="24"
                                
                              ></v-text-field>
                        </v-col>
                      </v-row>
                      <v-row>
                        <!-- Gift Card Code -->
                        
                        <v-col v-if="!showGiftCodeInput && !showGiftCardResponse">
                          <v-btn
                            class=""
                            outlined
                            color="indigo"
                            @click="showGiftCodeInput = true"
                          >
                            GiftCard Code
                          </v-btn>
                        </v-col>
                        <v-col  v-if="showGiftCodeInput" sm="8" lg="6" md="6">
                          <div>
                            <v-text-field
                              v-model="giftCardCodeObject.code"
                              label="Gift Code"
                              outlined
                            >
                            </v-text-field>
                          </div>
                        </v-col>
                        <v-col v-if="showGiftCardResponse" sm="6" md="6" lg="6">
                            <div>
                              {{ giftCardResponse.message }}
                            </div>
                        </v-col>
                        <v-col v-if="showGiftCodeInput">
                          <v-btn depress color="primary" @click="checkGiftCard">
                            Check
                          </v-btn>
                        </v-col>
                        <v-col v-if="showGiftCardResponse">
                          <v-text-field
                            v-model.number="giftCardRedeemObject.amount"
                            class="pt-0"
                            type="number"
                            label="Amount"
                            min="0"
                            prefix="$"
                          ></v-text-field>
                        </v-col>
                        <v-col v-if="showGiftCodeInput || showGiftCardResponse">
                          <v-btn
                            class=""
                            fab
                            outlined
                            small
                            @click="function() { showGiftCodeInput = false; showGiftCardResponse = false; giftCardResponse = {} }"
                          >
                            <v-icon dark> mdi-close </v-icon>
                          </v-btn>
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col>
                          <v-text-field
                            v-if="!showPercentDiscount"
                            v-model.number="currentDiscount"
                            class="mt-0 pt-0"
                            type="number"
                            label="Discount: $"
                            min="0"
                          ></v-text-field>
                          <v-text-field
                            v-if="showPercentDiscount"
                            v-model="percentDiscount"
                            class="mt-0 pt-0"
                            type="number"
                            label="Discount: %"
                            min="0"
                          ></v-text-field>
                        </v-col>
                        <v-col>
                          <v-btn
                            outlined
                            @click="showPercentDiscount = true"
                            v-if="!showPercentDiscount"
                            ><v-icon>mdi-percent</v-icon></v-btn
                          >
                          <v-btn
                            outlined
                            @click="showPercentDiscount = false"
                            v-if="showPercentDiscount"
                            ><v-icon>mdi-currency-usd</v-icon></v-btn
                          >
                        </v-col></v-row
                      >

                      <!-- <div class="text--primary">
                        Package Cost: ${{ selectedPackage.package_cost }}
                      </div> -->
                      <div class="text--primary">
                        Package Discount: -${{ returnDiscount }}
                      </div>
                      <v-row>
                           <!-- balance cash -->
                          <v-col sm="4" v-if="showCashInput">
                          <v-text-field
                            v-model.number="balanceCash"
                            class="mt-6 pt-0"
                            type="number"
                            label="Cash: $"
                            min="0"
                          ></v-text-field>
                        </v-col>
                        <!-- balance check -->
                        <v-col sm="4" v-if="showCheckInput">
                          <v-text-field
                            v-model.number="balanceCheck"
                            class="mt-6 pt-0"
                            type="number"
                            label="Check: $"
                            min="0"
                          ></v-text-field>
                        </v-col>
                        <!-- balance cost -->
                        <v-col sm="4">
                        
                          <v-text-field
                            v-model.number="totalCost"
                            class="mt-6 pt-0"
                            type="number"
                            label="Balance: $"
                            min="0"
                          ></v-text-field>
                        </v-col>
                        <v-spacer></v-spacer>
                        <v-spacer></v-spacer>
                      </v-row>
                       <v-btn
                          v-if="!showPaymentMethodOptions"
                          class="mb-3"
                          outlined
                          color="red"
                          @click="preparePaymentMethods"
                          >
                            Pay Methods
                        </v-btn>
                        <v-row v-else>
                          <v-col sm="7" md="7" lg="7">
                            <v-select 
                            label="Choose Pay Method"
                            :items="availablePaymentMethods"
                            v-model="selectedPaymentMethod"
                            item-text="cardDescription"
                            return-object
                          ></v-select>
                        </v-col>
                        <v-col>
                          <!-- <v-btn depress color="primary" @click="addPaymentMethodOption">
                            Add
                          </v-btn> -->
                          <!-- ADD PAYMENT METHOD SEPARATE DIALOG -->
                           <v-dialog 
                              v-model="addPaymentMethodDialog"
                              persistent
                              max-width="600px"
                            >
                              <template v-slot:activator="{ on, attrs }">
                                <v-btn
                                  color="primary"
                                  dark
                                  v-bind="attrs"
                                  v-on="on"
                                >
                                  Add
                                </v-btn>
                              </template>
                              <v-card>
                                <v-card-title>
                                  <span class="text-h5" style="color: rgba(245, 104, 71, 0.95)">Choose a Method</span>
                                </v-card-title>
                                <v-row justify="center" align="center">
                                  <v-spacer></v-spacer>
                                  <v-col v-if="!showCardForm" @click="processSetupIntentThroughReader">
                                    <v-btn  color="primary">
                                  Reader
                                </v-btn>
                                  </v-col>
                                
                                <v-col v-if="!showCardForm" >
                                <v-btn @click="showCardForm = true"  color="primary">
                                  Manual
                                </v-btn>
                                </v-col>
                                <v-spacer></v-spacer>
                              </v-row>
                              <!-- STRIPE ELEMENT0 -->
                              <!-- <stripe-element-card v-if="showCardForm"
                              ref="elementRef"
                              :pk="publishableKey"

                              /> -->
                                <v-card-text v-if="showCardForm">
                                  <v-container>
                                    <v-row>
                                      <v-col
                                        cols="12" class="mt-2"
                                        sm="8"
                                        md="8">
                                        <v-text-field
                                        v-model="newCardObject.cardNumber"
                                          label="Number*"
                                          required
                                        ></v-text-field>
                                      </v-col>
                                      <v-col cols="12">
                                        <v-text-field
                                        v-model.number="newCardObject.expirationMonth"
                                          label="Expiration Month*"
                                          type="number"
                                          max="12"
                                          required
                                        ></v-text-field>
                                      </v-col>
                                      <v-col cols="12">
                                        <v-text-field
                                        v-model.number="newCardObject.expirationYear"
                                          label="Expiration Year*"
                                          type="number"
                                          required
                                        ></v-text-field>
                                      </v-col>
                                      <v-col cols="12">
                                        <v-text-field
                                        v-model="newCardObject.cvc"
                                          label="CVC*"
                                          required
                                        ></v-text-field>
                                      </v-col>
                                    </v-row>
                                  </v-container>
                                  <small>*indicates required field</small>
                                </v-card-text>
                                <v-card-actions>
                                  <v-btn v-if="showCardForm"
                                    color="blue darken-1"
                                    text
                                    @click="closeCardForm"
                                  >
                                    Close Form
                                  </v-btn>
                                  <v-spacer></v-spacer>
                                  <v-btn v-if="showCardForm"
                                    color="blue darken-1"
                                    text
                                    @click="saveNewCard"
                                  >
                                    Save Card
                                  </v-btn> 
                                  <v-btn v-if="!showCardForm"
                                    color="blue darken-1"
                                    text
                                    @click="addPaymentMethodDialog = false"
                                  >
                                    Close
                                  </v-btn>               
                                </v-card-actions>
                              </v-card>
                            </v-dialog>
                        </v-col>
                        <v-col>
                          <v-btn
    
                            class=""
                            fab
                            outlined
                            small
                            @click="function() { showPaymentMethodOptions = false; selectedPaymentMethod = {};}"
                          >
                            <v-icon dark> mdi-close </v-icon>
                          </v-btn>
                        </v-col>
                        </v-row>
                        
                      <div class="text--primary" style="border-top: 1px solid">
                        Balance: ${{ totalCost }}
                      </div>
                      <v-checkbox v-if="showSaveCardCheckbox"
                        v-model="clientCheckout.saveCard" label="Save Payment Method?">
                      </v-checkbox>
                      <v-checkbox
                        v-model="clientCheckout.compFree" label="Comp/Free?">
                      </v-checkbox>
                      <!-- <v-checkbox v-if="showSaveRecurringPaymentCheckbox"
                        v-model="clientCheckout.saveAsRecurringPayment"
                        label="Save As Recurring Payment?"
                      ></v-checkbox> -->
                    </v-col>
                  </v-row>
                  <v-row justify="center">
                    <v-btn small outlined color="indigo" v-if="!showCashInput" @click="showCashInput = true">Cash</v-btn>
                    <v-btn small color="primary" v-if="showCashInput" @click="toggleCashForm">Cash</v-btn>
                    <v-spacer></v-spacer>
                    <v-btn small outlined color="indigo" v-if="!showCheckInput" @click="showCheckInput = true">Check</v-btn>
                    <v-btn small color="primary" v-if="showCheckInput" @click="toggleCheckForm">Check</v-btn>
                    <v-spacer></v-spacer>
                    <v-btn small outlined color="indigo" v-if="!showEmailForm" @click="showEmailForm = true">E-Receipt</v-btn>
                  </v-row>
                  <v-row v-if="showEmailForm">
                    <v-text-field class="mt-4"
                      v-model="clientCheckout.emailForReceipt"
                      :counter="30"
                      label="Email for Receipt"
                    ></v-text-field>
                    <v-checkbox
                      class="mt-4"
                          v-if="saveEmailForReceiptCheckbox"
                          v-model="clientCheckout.saveEmailReceiptPurchase"
                          @click="saveEmail"
                          label="Save?"
                        ></v-checkbox>
                    <v-btn
                    class="mt-4"
                      fab
                      outlined
                      small
                        @click="function() { showEmailForm = false; clientCheckout.saveEmailReceiptPurchase = false; }"
                      >
                        <v-icon dark> mdi-close </v-icon>
                    </v-btn>
                  </v-row>
                  <v-row>
                      
                    </v-row>
                </v-container>
              </v-card-title>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
                <v-btn
                  color="blue darken-1"
                  text
                  @click.prevent="submitClientCheckout"
                >
                  Save
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <template v-slot:[`item.is_monthly_renew`]="{ item }">
        <v-simple-checkbox
          v-model="item.is_monthly_renew"
          disabled
        ></v-simple-checkbox>
      </template>
      <template
        v-slot:[`item.actions`]="{ item }"
        v-if="$store.state.user.username == 'admin'"
      >
        <v-icon small class="mr-2" @click="Remove(item)">
          mdi-close-thick
        </v-icon>
      </template>
    </v-data-table>
    <v-row>
      <v-col lg="10" md="9" sm="9">
        <v-pagination
          v-model="page"
          :length="Math.ceil(totalPackagesPurchased / pageSize)"
          @input="temporaryPageMethod"
          total-visible="8"
        ></v-pagination>
      </v-col>
      <v-col lg="2" md="3" class="mt-2" sm="3">
        <v-select
          v-model="pageSize"
          :items="[10, 20, 30, 40, 50]"
          outlined
          filled
          @change="temporaryPageSizeMethod"
        >
        </v-select>
      </v-col>
      <v-spacer></v-spacer>
    </v-row>
    <br />
    <br />
    <!-- <v-overlay :value="overlay">
      <v-progress-circular indeterminate size="70"></v-progress-circular>
    </v-overlay> -->
  </v-container>
</template>

<script>
import packagePurchaseService from "../services/PackagePurchaseService";
import packageDetailService from "../services/PackageDetailService";
import eventService from "../services/EventService";
import clientDetailService from "../services/ClientDetailService";
import stripeService from "../services/StripeService";
import giftCardService from '../services/GiftCardService.js'
// import { StripeElementCard } from '@vue-stripe/vue-stripe';

export default {
  name: "client-active-package-table",
  // components: {StripeElementCard},
  data() {
    return {
      publishableKey: "pk_test_51NEabUBV0tnIJdW6JIy49Ky1uilERTHoouGeS6ySxpMsLiSwuehx2qo04plqxcFuVk7M5DYIJXXZ532bONj0iXbI00qVJtVHbn",
      headers: [
        {
          text: "Package Description",
          align: "start",
          value: "package_description",
          sortable: false,
        },
        { text: "Purchase Date", value: "date_purchased", sortable: true },
        {
          text: "Cost",
          value: "total_amount_paid",
        },
        {
          text: "Activation Date",
          value: "activation_date",
          sortable: true,
        },
        {
          text: "Expiration Date",
          value: "expiration_date",
          sortable: true,
        },
        {
          text: "Classes Remaning",
          value: "classes_remaining",
          sortable: true,
        },
        {
          text: "Monthly Renewal?",
          value: "is_monthly_renew",
        },
      ],
      page: 1,
      pageSize: 10,
      sortBy: "date_purchased",
      sortDesc: false,
      totalPackagesPurchased: 0,
      paginatedObject: {},
      packages: [],
      packagePurchase: {
        package_purchase_id: "",
        client_id: "",
        date_purchased: "",
        package_id: "",
        classes_remaining: "",
        activation_date: "",
        expiration_date: "",
        total_amount_paid: 0,
        is_monthly_renew: "",
        discount: 0,
        package_description: "",
      },
      newCardObject: {
        cardNumber: "",
        expirationMonth: 1,
        expirationYear: 1,
        cvc: ""
      },
      availablePackages: [],
      dialog: false,
      selectedPackages: [],
      selectedPackage: {},
      timeout: -1,
      snackBarReconcilePackages: false,
      showPercentDiscount: false,
      percentDiscount: 0,
      snackBarReconcilePackagesSuccessful: false,
      overlay: false,
      loading: true,
      sharedPackages: [],
      currentDiscount: 0,
      clientCheckout: {
        client_id: 0,
        emailForGift: "",
        emailForReceipt: "",
        saveEmailGiftCardPurchase: false,
        saveEmailReceiptPurchase: false,
        discount: 0,
        selectedCheckoutPackages: [],
        balance: 0,
        cash: 0,
        check: 0,
        compFree: false,
        paymentMethodId: "",
        iterations: 1,
        saveCard: false,
        saveAsRecurringPayment: false,
        renewalDate: new Date(
          Date.now() - new Date().getTimezoneOffset() * 60000
        )
          .toISOString()
          .substr(0, 10),
      },
      giftCardCost: 0,
      giftCardIndex: 0,
      saveEmailForGiftCardCheckbox: false,
      saveEmailForReceiptCheckbox: false,
      totalCost: 0,
      balanceCash: 0,
      balanceCheck: 0,
      showGiftCardForm: false,
      showEmailForm: false,
      showGiftCodeInput: false,
      showGiftCardResponse: false,
      giftCardCodeObject: {
        client_id: parseInt(this.$route.params.clientId) ,
        code: "",
      },
      giftCardResponse: {},
      showRenewalDatePicker: false,
      menu: false,
      giftCardRedeemObject: {
        amount: 0,
        code: "",
        client_id: parseInt(this.$route.params.clientId),
      },
      showPaymentMethodOptions: false,
      availablePaymentMethods: [],
      selectedPaymentMethod: {},
      addPaymentMethodDialog: false,
      showCardForm: false,
      showSaveCardCheckbox: false,
      showSaveRecurringPaymentCheckbox: false,
      showCashInput: false,
      showCheckInput: false,
    };
  },
  watch: {
    options: {
      handler() {
        this.getActivePurchaseServerRequest();
      },
      deep: true,
    },
    returnTotal: function (val) {
      this.totalCost = val;
    },
    totalCost: function (val) {
      this.returnTotal = val;
      
      let runningTotal = 0;
    
      for (let index = 0; index < this.selectedPackages.length; index++) {
        let element = this.selectedPackages[index];
        runningTotal += element.package_cost;
  
      }
      if (runningTotal > val) {
        this.showSaveRecurringPaymentCheckbox = true;
      } else {
        this.showSaveRecurringPaymentCheckbox = false;
      }
    },
    returnCash: function (val) {
      this.balanceCash = val;
    },
    balanceCash: function (val) {
      this.returnCash = val;
    },
    returnCheck: function(val) {
      this.balanceCheck = val;
    },
    balanceCheck: function (val) {
      this.returnCheck = val;
    },
    returnDiscount: function (val) {
      if (val > 0) {
        this.showSaveRecurringPaymentCheckbox = true
      } else {
        this.showSaveRecurringPaymentCheckbox = false
      }
    },
    clientCheckout: {
      handler: function () {
        if (
          this.clientCheckout.emailForGift.length == 0 ||
          this.clientCheckout.emailForGift != this.$store.state.clientDetails.email
        ) {
          this.saveEmailForGiftCardCheckbox = true;
        } else {
          this.saveEmailForGiftCardCheckbox = false;
        }
        if (
          this.clientCheckout.emailForReceipt != this.$store.state.clientDetails.email
          ) {
          this.saveEmailForReceiptCheckbox = true;
        } else {
          this.saveEmailForReceiptCheckbox = false;
        }
      },
      deep: true,
    },
    selectedPaymentMethod: {
      handler: function () {
        if (Object.keys(this.selectedPaymentMethod).length === 0) {
          this.showSaveCardCheckbox = true;
        } else {
          this.showSaveCardCheckbox = false;
        }
      },
      deep: true,
    },
    giftCardResponse: {
      handler: function() {
        if (this.giftCardResponse.message.length > 0) {
          this.showGiftCodeInput = false;
          this.showGiftCardResponse = true;
          this.giftCardRedeemObject.code = this.giftCardCodeObject.code
        }
      },
      deep: true,
    },
    giftCardRedeemObject: {
      handler: function() {
        let runningbalance = 0;
        // My attempt at capping the balance cash
        for (let index = 0; index < this.selectedPackages.length; index++) {
            let element = this.selectedPackages[index];
            runningbalance += element.package_cost;
        }
        if (this.showCashInput) {
          runningbalance -= this.balanceCash;
          }
          if (this.showCheckInput) {
          runningbalance -= this.balanceCheck;
          }
          if (this.returnDiscount > 0) {
        runningbalance -= this.returnDiscount
      }

        if (this.giftCardRedeemObject.amount > this.giftCardResponse.amountAvailable) {
          this.giftCardRedeemObject.amount = this.giftCardResponse.amountAvailable
        }

        if (this.giftCardRedeemObject.amount > runningbalance) {
          this.giftCardRedeemObject.amount = runningbalance;
        }
      },
      deep: true,
    },
    showSaveCardCheckbox: {
      handler() {
        if (this.selectedPackages[0].is_recurring) {
          this.showSaveCardCheckbox = false;
        }
      },
      immediate: true
    },
    selectedPackages: function () {
      // next loop prevents stacking subscriptions
      for (let index = this.selectedPackages.length - 1; index > 0; index--) {
        let element = this.selectedPackages[index];
        let firstIndex = 0;
        let firstElement = this.selectedPackages[firstIndex];
        if (
          element.is_subscription &&
          element.is_recurring &&
          index == this.selectedPackages.length - 1
        ) {
          this.selectedPackages = [element];
          break;
        } else if (
          firstElement.is_subscription &&
          firstElement.is_recurring &&
          this.selectedPackages.length == 2
        ) {
          this.selectedPackages = [element];
        }
      }

      // this loop checks for giftcard or subscriptions
      let runningTotal = 0;
      let foundGiftCard = false;
      let foundSubscription = false;
      for (let index = 0; index < this.selectedPackages.length; index++) {
        let element = this.selectedPackages[index];
        runningTotal += element.package_cost;
        if (element.description.includes("Gift")) {
          this.showGiftCardForm = true;
          this.clientCheckout.emailForGift = this.$store.state.clientDetails.email;
          if (
            this.clientCheckout.emailForGift.length == 0 ||
            this.clientCheckout.emailForGift != this.$store.state.clientDetails.email
          ) {
            this.saveEmailForGiftCardCheckbox = true;
          }
          foundGiftCard = true;
          this.giftCardIndex = index;
        }
        if (element.is_subscription && element.is_recurring) {
          foundSubscription = true;
          this.showRenewalDatePicker = true;
          this.showSaveCardCheckbox = false;
        }
      }
      if (runningTotal > this.returnTotal()) {
        this.showSaveRecurringPaymentCheckbox = true;
      } else {
        this.showSaveRecurringPaymentCheckbox = false;
      }
      if (!foundGiftCard) {
        this.showGiftCardForm = false;
        this.saveEmailForGiftCardCheckbox = false;
      }
      if (!foundSubscription) {
        this.showRenewalDatePicker = false;
        this.showSaveCardCheckbox = true;
      }
    },
  },
  beforeCreate() {
    // clientDetailService.getClientDetailsByClientId(this.$route.params.clientId).then((response) => {
    //     if (response.data.client_id != 0) {
    //       this.clientDetails = response.data;
    //       this.$store.commit("SET_CLIENT_DETAILS", response.data);
    //       // alert("active package table client details")
    //       if (this.clientDetails.redFlag == true) {
    //         this.snackBarReconcileWarning = true
    //       }
    //     }
    //   });
    // if (this.$store.state.user.username == "admin") {
    //   this.retrieveClientDetailsForAdmin();
    // }
    // TODO: CAREFUL DELETING THIS BECAUSE WE FORGOT WHAT IT DOES
    this.$root.$on("getActivePurchasePackageTable", () => {
      this.getActivePurchaseServerRequest();
    });
  },
  created() {
    if (this.$store.state.user.username == "admin") {
      this.getSharedActivePackages();
      this.retrieveClientDetailsForAdmin();
    }
    setTimeout(() => {
      this.getActivePurchaseServerRequest();
    }, 1500);

    this.$root.$refs.A = this;

    if (this.$store.state.user.username == "admin") {
      this.headers.unshift({
        text: "Package ID",
        value: "package_purchase_id",
        sortable: true,
      });
      this.headers.push({ text: "Cancel", value: "actions", sortable: false });
      this.getPublicPackagesTable();

      if (Object.keys(this.selectedPaymentMethod).length === 0) {
          this.showSaveCardCheckbox = true;
        }
    }

    
  },
  mounted() {
    // const publishableKey = this.publishableKey;
    // const stripe = Stripe(publishableKey);

    // const element = stripe.elements(0;
    // const paymentElement = element.create("payment"),{});

// const stripe = Stripe(this.publishableKey);

//     stripe
//   .createPaymentMethod({
//     type: 'card',
//     card: cardElement,
//     billing_details: {
//       name: 'Jenny Rosen',
//     },
//   })
//   .then(function(result) {
//     // Handle result.error or result.paymentMethod
//     this.selectedPaymentMethod = result.paymentMethod
//   });


  },
  methods: {
    toggleCashForm() {
      
      this.showCashInput = false;
      this.balanceCash = 0;
    
  },
  toggleCheckForm() {
    
      this.showCheckInput = false;
      this.balanceCheck = 0;
    
  },
    closeCardForm() {
      this.showCardForm = false;
      this.newCardObject = {
        cardNumber: "",
        expirationMonth: 1,
        expirationYear: 1,
        cvc: ""
      }
    },
    retrieveClientDetailsForAdmin() {
      clientDetailService
        .getClientDetailsByClientId(parseInt(this.$route.params.clientId))
        .then((response) => {
          if (response.data.client_id != 0) {
            this.clientDetails = response.data;
            this.$store.commit("SET_CLIENT_DETAILS", response.data);
            this.clientCheckout.emailForGift = this.$store.state.clientDetails.email;
            this.clientCheckout.emailForReceipt = this.$store.state.clientDetails.email;
            // alert("active package table client details")
            if (this.clientDetails.redFlag == true) {
              this.snackBarReconcileWarning = true;
            }
          }
        });
    },
    // snackbars not showing and not unshowing
    allowClientReconcile() {
      this.loading = true;
      this.overlay = !this.overlay;
      eventService
        .reconcileClassesForClient(parseInt(this.$route.params.clientId))
        .then((response) => {
          if (response.status == 200) {
            this.$root.$refs.C.getClientDetails();
            this.loading = false;
            this.overlay = false;
            alert("Success");
            this.getSharedActivePackages();
            this.getActivePurchaseServerRequest();
            this.$root.$refs.B.getPackageHistoryTable();

            this.$root.$refs.D.getClientEventTable();
            this.$root.$refs.E.getEventDetailsCall();
            this.snackBarReconcilePackagesSuccessful = true;
            // setTimeout(() => {
            //   this.closeReconcile();
            // }, 2500);
            this.snackBarReconcilePackages = false;
          } else {
            this.snackBarReconcilePackages = false;
            alert("Error Reconciling Classes");
            this.getActivePurchaseServerRequest();
          }
        });
    },
    getPublicPackagesTable() {
      packageDetailService.getAllPublicPackages().then((response) => {
        if (response.status == 200) {
          // adjust this commit in the future perhaps
          this.$store.commit("SET_PACKAGE_LIST", response.data);

          this.availablePackages = response.data;
        } else {
          alert("Error retrieving package information");
        }
      });
    },
    checkGiftCard() {
      if (this.giftCardCodeObject.code.length == 7) {
        giftCardService.retrieveGiftCard(this.giftCardCodeObject).then((response => {
          if (response.status == 200) {
            this.giftCardResponse = response.data;
          }
        })).catch(e => {
          alert(e.response.data.message)
        })
      } else {
        alert("Please enter a valid Gift Card Code");
      }
    },
    preparePaymentMethods() {
      this.showPaymentMethodOptions = true;
      // Call a method that retrieves 
      // a list of payment methods by this client's ID
      this.retrievePaymentMethodsFromStripe(this.$route.params.clientId);
      
      // 
    },
    retrievePaymentMethodsFromStripe(clientId) {
      
      stripeService.returnPaymentMethodOptions(clientId).then((response) => {
        // set it to availablePaymentMethods
        if (response.status == 200) {
          // make sure there is a cardDescription string that
          // makes the card identifiable 
          this.availablePaymentMethods = response.data;
        }

     
      })

    },
    processSetupIntentThroughReader() {
      stripeService.addPaymentMethodThroughReader(this.$route.params.clientId).then((response) => {
        if (response.status == 201) {
          alert("Tap card and refresh payment methods.")
          this.addPaymentMethodDialog = false;
          this.retrievePaymentMethodsFromStripe();
          setTimeout(() => {
            this.retrievePaymentMethodsFromStripe();
          }, 5000);
        } else {
          alert("Error with SetupIntent through Reader")
        }
      })
    },
    saveNewCard() {
      if (this.newCardObject.cardNumber.length == 0 || this.newCardObject.expirationMonth == 0 || this.newCardObject.expYear == 0 || this.newCardObject.cvc == 0) {
        alert ("Please fill in the card form")
      } else {
        stripeService.addPaymentMethodManually(this.$route.params.clientId, this.newCardObject).then((response) => {
          if (response.status == 201) {
            this.showCardForm = false;
            this.addPaymentMethodDialog = false;
            this.retrievePaymentMethodsFromStripe(this.$route.params.clientId)
          }
        })
      }
    },
    close() {
      this.dialog = false;
      this.percentDiscount = 0;
      this.currentDiscount = 0;
      this.totalCost = 0;
      this.balanceCash = 0;
      this.balanceCheck = 0;
      this.giftCardCost = 0;
      this.giftCardIndex = 0;
      this.showGiftCardForm = false;
      this.showEmailForm = false;
      this.saveEmailForGiftCardCheckbox = false;
      this.saveEmailForReceiptCheckbox = false;
      this.showGiftCodeInput = false;
      this.giftCardCodeObject.code = "";
      this.showGiftCardResponse = false;
      this.giftCardResponse = {};
      this.giftCardRedeemObject = {
        amount: 0,
        code: "",
        client_id: parseInt(this.$route.params.clientId),
      }
      this.clientCheckout = {
        client_id: 0,
        emailForGift: this.$store.state.clientDetails.email,
        emailForReceipt: this.$store.state.clientDetails.email,
        saveEmailGiftCardPurchase: false,
        saveEmailReceiptPurchase: false,
        discount: 0,
        selectedCheckoutPackages: [],
        iterations: 1,
        saveAsRecurringPayment: false,
        balance: 0,
        cash: 0,
        check: 0,
        compFree: false,
        giftCard: {},
        renewalDate: new Date(
          Date.now() - new Date().getTimezoneOffset() * 60000
        )
          .toISOString()
          .substr(0, 10),
        paymentMethodId: "",
        saveCard: false,
      };
      this.selectedPackages = [];
      this.selectedPaymentMethod = {};
      this.showPaymentMethodOptions = false;
      this.showSaveRecurringPaymentCheckbox = false;
      this.toggleCashForm();
      this.toggleCheckForm();
    },
    // closeReconcile(){
    //   this.snackBarReconcilePackages = false;
    // },
    temporaryPageMethod() {
      this.getActivePurchaseServerRequest();
    },
    temporaryPageSizeMethod() {
      if (this.page == 1) {
        this.getActivePurchaseServerRequest();
      } else {
        this.page = 1;
        this.getActivePurchaseServerRequest();
      }
    },
    sortTable() {
      if (this.sortDesc == undefined) {
        this.sortDesc = false;
      }
      this.getActivePurchaseServerRequest();
    },
    getActivePurchaseServerRequest() {
      this.loading = true;
      this.overlay = !this.overlay;
      if (this.$store.state.user.username == "admin") {
        packagePurchaseService
          .getActivePaginatedUserPurchasedPackagesByClientId(
            parseInt(this.$route.params.clientId),
            this.page,
            this.pageSize,
            this.sortBy,
            this.sortDesc
          )
          .then((response) => {
            if (response.status == 200) {
              this.loading = false;
              this.overlay = false;
              // focus on if it's expired or not
              var today = new Date();
              var dd = String(today.getDate()).padStart(2, "0");
              var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
              var yyyy = today.getFullYear();
              today = yyyy + "-" + mm + "-" + dd;
              this.paginatedObject = response.data;
              this.totalPackagesPurchased = this.paginatedObject.totalRows;
              this.packages = this.paginatedObject.listOfPurchasedPackages;
              // this.packages = this.paginatedObject.listOfPurchasedPackages.filter((item) => {
              //   return (
              //     (item.is_subscription && item.expiration_date >= today) ||
              //     (!item.is_subscription &&
              //       item.expiration_date >= today &&
              //       item.classes_remaining > 0)
              //   );
              // });
              this.packages.forEach((item) => {
                item.date_purchased = new Date(
                  item.date_purchased).toLocaleString();
              });
              // console.log(this.$store.state.clientDetails.redFlag);
              // console.log(this.packages.length > 0);
              // console.log(this.$store.state.sharedPackages.length > 0);
              // console.log(
              //   this.$store.state.clientDetails.redFlag &&
              //     (this.packages.length > 0 || this.sharedPackages.length > 0)
              // );
              // alert(this.$store.state.clientDetails.redFlag)
              // console.log(this.$store.state.clientDetails.redFlag)
              // console.log(this.packages.length > 0)
              // console.log(this.$store.state.sharedPackages.length > 0)
              // console.log(this.$store.state.clientDetails.redFlag &&
              // (this.packages.length > 0 ||
              // this.sharedPackages.length > 0);
              // alert(this.$store.state.clientDetails.redFlag)
              if (
                this.$store.state.clientDetails.redFlag &&
                (this.packages.length > 0 || this.sharedPackages.length > 0)
              ) {
                this.snackBarReconcilePackages = true;
              }
              this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
            } else {
              alert("Error retrieving package information");
            }
          });
      } else {
        packagePurchaseService
          .getActivePaginatedUserPurchasedPackagesByClientId(
            this.$store.state.clientDetails.client_id,
            this.page,
            this.pageSize,
            this.sortBy,
            this.sortDesc
          )
          .then((response) => {
            if (response.status == 200) {
              this.loading = false;
              this.overlay = false;
              // focus on if it's expired or not
              var today = new Date();
              var dd = String(today.getDate()).padStart(2, "0");
              var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
              var yyyy = today.getFullYear();
              today = yyyy + "-" + mm + "-" + dd;
              this.paginatedObject = response.data;
              this.totalPackagesPurchased = this.paginatedObject.totalRows;
              this.packages =
                this.paginatedObject.listOfPurchasedPackages.filter((item) => {
                  // return (item.expiration_date >= today) || (item.expiration_date == null && item.classes_remaining > 0) || (item.expiration_date >= today && item.classes_remaining > 0);
                  return (
                    (item.is_subscription && item.expiration_date) ||
                    (!item.is_subscription &&
                      item.expiration_date >= today &&
                      item.classes_remaining > 0)
                  );
                });
              this.packages.forEach((item) => {
                item.date_purchased = new Date(item.date_purchased).toLocaleString();
              });
              if (
                this.$store.state.clientDetails.redFlag &&
                this.packages.length > 0
              ) {
                this.snackBarReconcilePackages = true;
              }
              this.$store.commit("SET_ACTIVE_PACKAGE_LIST", this.packages);
            } else {
              alert("Error retrieving package information");
            }
          });
      }
    },
    getSharedActivePackages() {
      packagePurchaseService
        .getAllSharedActiveQuantityPackagesByClientId(
          parseInt(this.$route.params.clientId)
        )
        .then((response) => {
          if (response.status == 200) {
            this.sharedPackages = response.data;
          }
          this.$store.commit("SET_SHARED_PACKAGE_LIST", this.sharedPackages);
        });
    },
    Remove(item) {
      // this will be an update
      packagePurchaseService.expirePackage(item).then((response) => {
        if (response.status == 200) {
          alert("You have canceled this package");
          this.getActivePurchaseServerRequest();

          // call the method to update the purchase history table so it updates the expired column
          this.$root.$refs.B.getPackageHistoryTable();
        } else {
          alert("Error canceling class");
        }
      });
    },
    addPackageForClient() {
      this.loading = true;
      this.overlay = !this.overlay;
      this.packagePurchase.client_id = parseInt(this.$route.params.clientId);
      const jsonDate = new Date().toJSON();
      this.packagePurchase.date_purchased = jsonDate;
      this.packagePurchase.package_id = this.selectedPackage.package_id;
      this.packagePurchase.is_expired = false;

      // if you're buying a subscription and there's a subscription that's active

      if (this.packages.length > 0) {
        let foundSubscription = false;
        // loop to find another package that is a subscription
        for (let index = 0; index < this.packages.length; index++) {
          if (
            this.packages[index].is_subscription &&
            this.selectedPackage.is_subscription
          ) {
            foundSubscription = true;
            // use that expiration date to create the activation date and expiration date of the new subscription
            let formatDate = this.packages[index].expiration_date;
            formatDate = formatDate.replaceAll("-", "/");

            let newActivationDate = new Date(formatDate);

            newActivationDate = newActivationDate.setDate(
              newActivationDate.getDate() + 1
            );
            this.packagePurchase.activation_date = new Date(newActivationDate);

            this.packagePurchase.expiration_date = this.addMonths(
              new Date(newActivationDate),
              this.selectedPackage.subscription_duration
            );
          }
        }

        // what to do if you don't find a subscription
        if (!this.selectedPackage.is_subscription) {
          this.packagePurchase.expiration_date = this.addMonths(new Date(), 12);
        } else if (!foundSubscription && this.selectedPackage.is_subscription) {
          this.packagePurchase.activation_date = new Date();
          if (this.selectedPackage.subscription_duration > 0) {
            this.packagePurchase.expiration_date = this.addMonths(
              new Date(),
              this.selectedPackage.subscription_duration
            );
          }
        }
      }

      if (this.selectedPackage.is_subscription && this.packages.length == 0) {
        this.packagePurchase.activation_date = new Date();
        if (this.selectedPackage.subscription_duration > 0) {
          this.packagePurchase.expiration_date = this.addMonths(
            new Date(),
            this.selectedPackage.subscription_duration
          );
        }
      }
      if (!this.selectedPackage.is_subscription && this.packages.length == 0) {
        this.packagePurchase.expiration_date = this.addMonths(new Date(), 12);
      }

      if (this.showPercentDiscount) {
        // if it's a percent
        let num =
          this.selectedPackage.package_cost * (1 - this.percentDiscount / 100);
        this.packagePurchase.discount = this.selectedPackage.package_cost - num;
        this.packagePurchase.total_amount_paid = Math.round(num * 100) / 100;
      } else if (
        this.selectedPackage.discount >= 0 &&
        this.selectedPackage.package_cost >= 0 &&
        !this.showPercentDiscount
      ) {
        // if it's in dollars
        this.packagePurchase.discount = this.selectedPackage.discount;
        this.packagePurchase.total_amount_paid =
          this.selectedPackage.package_cost - this.selectedPackage.discount;
      } else {
        this.packagePurchase.total_amount_paid =
          this.selectedPackage.package_cost;
      }

      //  this.selectedPackage.package_cost;
      this.packagePurchase.is_monthly_renew = false;
      this.packagePurchase.classes_remaining =
        this.selectedPackage.classes_amount;

      packagePurchaseService
        .createPackagePurchase(this.packagePurchase)
        .then((response) => {
          if (response.status == 201) {
            this.loading = false;
            this.overlay = false;
            alert("Succesfully purchased package");
            // call method that updates the list of active packages
            this.getActivePurchaseServerRequest();
            this.$root.$refs.B.getPackageHistoryTable();
            this.$root.$refs.D.getClientEventTable();

            this.selectedPackage = {};
            this.packagePurchase = {};
            this.close();
          }
        });
      this.close();
    },
    addMonths(date, months) {
      var d = date.getDate();
      date.setMonth(date.getMonth() + months);
      if (date.getDate() != d) {
        date.setDate(0);
      }
      // fixing  fix: (3/3 → 4/2) NOT (3/3 → 4/3)
      date.setDate(date.getDate() - 1);
      return date;
    },
    saveEmail() {
      // clientCheckout.emailForReceipt or clientCheckout.emailForGift
      
      let email = ""
      let giftCardSave = false;
      if (this.clientCheckout.saveEmailGiftCardPurchase && this.saveEmailForGiftCardCheckbox) {
        giftCardSave = true;
        email = this.clientCheckout.emailForGift;
      } else if (this.clientCheckout.saveEmailReceiptPurchase && this.saveEmailForReceiptCheckbox) {
        email = this.clientCheckout.emailForReceipt;
       
      }
      let clientId = parseInt(this.$route.params.clientId);
      let ClientDetails = {
        client_id: clientId,
        email: email
      };
      clientDetailService.updateEmailForClient(ClientDetails)
      .then((response) => {
        if (response.status == 200) {
         
        if (response.data === "Successful") {
          
              if (giftCardSave) {
                this.saveEmailForGiftCardCheckbox = false;
                this.$store.state.clientDetails.email = this.clientCheckout.emailForGift;
                this.clientCheckout.emailForReceipt = this.clientCheckout.emailForGift;
                alert("Email saved")
              } else {
                this.saveEmailForReceiptCheckbox = false;
                this.$store.state.clientDetails.email = this.clientCheckout.emailForReceipt;
            
                alert("Email saved")
              }
          } else if (response.data == "Email in use") {
            alert("Email in use")
          }
        }
      })
    },
    submitClientCheckout() {
      if (this.selectedPackages.length != 0) {
        this.clientCheckout.client_id =
          this.$store.state.clientDetails.client_id;

          if ((this.clientCheckout.emailForGift.length == 0 || this.clientCheckout.emailForReceipt == 0) && (this.$store.state.clientDetails.email.length >= 1)) {
          this.clientCheckout.emailForGift = this.$store.state.clientDetails.email;
          this.clientCheckout.emailForReceipt = this.$store.state.clientDetails.email;
        } else if (this.clientCheckout.emailForGift.length == 0 && this.showGiftCardForm) {
          alert("We need an email for Gift")
          return
        }
        this.clientCheckout.balance = this.totalCost;
        this.clientCheckout.cash = this.balanceCash;
        this.clientCheckout.check = this.balanceCheck;
        this.clientCheckout.selectedCheckoutPackages = this.selectedPackages;
        this.clientCheckout.discount = this.returnDiscount;
        if (this.showGiftCardResponse) {
          this.clientCheckout.giftCard = this.giftCardRedeemObject
        }
        this.clientCheckout.paymentMethodId = this.selectedPaymentMethod.paymentMethodId;
        if (this.clientCheckout.selectedCheckoutPackages[0].is_recurring && (this.clientCheckout.paymentMethodId == undefined || this.clientCheckout.paymentMethodId == "" )) {
          alert("Please Choose a Payment Method For Subscription")
        } else {
          if (this.clientCheckout.balance == 0 && this.clientCheckout.selectedCheckoutPackages.length == 0) {
            alert("Price cannot be zero")
          } else {

          
          stripeService
          .purchaseClientCheckout(this.clientCheckout)
          .then((response) => {
            if (response.status == 201) {
              // this.retrieveClientDetailsForAdmin();
              // if (this.clientCheckout.saveEmailGiftCardPurchase) {
              //   this.$store.state.clientDetails.email = this.clientCheckout.emailForGift;
              // }
              // if (this.clientCheckout.saveEmailReceiptPurchase) {
              //   this.$store.state.clientDetails.email = this.clientCheckout.emailForReceipt;
              // }
              alert("Submitted");
              this.close();

              setTimeout(() => {
              this.getActivePurchaseServerRequest();

              this.$root.$refs.B.getPackageHistoryTable();
              this.$root.$refs.D.getClientEventTable();
              }, 5000);

              setTimeout(() => {
              this.getActivePurchaseServerRequest();

              this.$root.$refs.B.getPackageHistoryTable();
              this.$root.$refs.D.getClientEventTable();
              }, 10000);

              setTimeout(() => {
              this.getActivePurchaseServerRequest();

              this.$root.$refs.B.getPackageHistoryTable();
              this.$root.$refs.D.getClientEventTable();
              }, 15000);

              // setTimeout(() => {
              // this.getActivePurchaseServerRequest();

              // this.$root.$refs.B.getPackageHistoryTable();
              // this.$root.$refs.D.getClientEventTable();
              // }, 20000);

              this.loading = false;
            }
          });
        }
      }
      } else {
        alert("Select at least one item");
      }
    },
  },

  computed: {
    returnCash() {
      let runningbalance = 0;
      for (let index = 0; index < this.selectedPackages.length; index++) {
        runningbalance += this.selectedPackages[index].package_cost;
      }
      if (this.showGiftCardResponse) {
        runningbalance -= this.giftCardRedeemObject.amount;
      }
      if (this.showCheckInput) {
        runningbalance -= this.balanceCheck;
      }
      if (this.returnDiscount > 0) {
        runningbalance -= this.returnDiscount
      }
      if (this.showCashInput && runningbalance >= 0) {
        if (this.balanceCash < runningbalance) {
          return this.balanceCash;
        } else {
          return runningbalance;
        }
      }  
        return 0;
    },
    returnCheck() {
      let runningbalance = 0;
      for (let index = 0; index < this.selectedPackages.length; index++) {
        runningbalance += this.selectedPackages[index].package_cost;
      }
      if (this.showGiftCardResponse) {
        runningbalance -= this.giftCardRedeemObject.amount;
      }
      if (this.showCashInput) {
        runningbalance -= this.balanceCash;
      }
      if (this.returnDiscount > 0) {
        runningbalance -= this.returnDiscount
      }
      if (this.showCheckInput && runningbalance >= 0) {
        if (this.balanceCheck < runningbalance) {
          return this.balanceCheck;
        } else {
          return runningbalance;
        }
      }  
        return 0;
    },
    returnDiscount() {
      let runningTotal = 0;
      for (let index = 0; index < this.selectedPackages.length; index++) {
        runningTotal += this.selectedPackages[index].package_cost;
      }
      if (this.showGiftCardResponse) {
          runningTotal -= this.giftCardRedeemObject.amount;
          }
          if (this.showCashInput) {
          runningTotal -= this.balanceCash;
          }
          if (this.showCheckInput) {
          runningTotal -= this.balanceCheck;
          }

      if (this.showPercentDiscount && runningTotal >= 0) {
        // this.selectedPackage.discount = this.selectedPackage.package_cost * (1-this.percentDiscount);
        let num =
          runningTotal - runningTotal * (1 - this.percentDiscount / 100);
        let math = Math.round(num * 100) / 100;
        if (runningTotal > math) {
          return math;
        }
        return 0;
      } else if (this.currentDiscount >= 0 && runningTotal >= 0) {
        if (this.currentDiscount < runningTotal) {
          return this.currentDiscount;
        } else {
          return runningTotal;
        }
      }
      return 0;
    },
    returnTotal() {
      let runningTotal = 0;
      for (let index = 0; index < this.selectedPackages.length; index++) {
        runningTotal = runningTotal + this.selectedPackages[index].package_cost;
      }
      if (this.showGiftCardResponse) {
        runningTotal -= this.giftCardRedeemObject.amount;
      }
      if (this.showCashInput) {
        runningTotal -= this.balanceCash;
      }
      if (this.showCheckInput) {
        runningTotal -= this.balanceCheck;
      }
      if (this.showPercentDiscount && runningTotal >= 0) {
        let num = runningTotal * (1 - this.percentDiscount / 100);
        if (num > 0) {
          return Math.round(num * 100) / 100;
        }
        return 0;
      } else if (this.currentDiscount >= 0 && runningTotal >= 0) {
        let difference = runningTotal - this.currentDiscount;
        if (difference > runningTotal || difference < 0) {
          return 0;
        }

        return difference;
      } else if (runningTotal >= 0) {
        return runningTotal;
      } else {
        return 0;
      }
    },
  },
};
</script>

<style>
</style>