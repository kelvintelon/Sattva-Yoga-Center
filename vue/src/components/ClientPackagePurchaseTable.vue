<template>
  <v-container>
    <v-row><br /></v-row>
    <v-row>
      <v-spacer></v-spacer>
      <h1 style="color: rgba(245, 104, 71, 0.95)">Purchase a Package</h1>
      <v-spacer></v-spacer>
    </v-row>
    <br />

    <v-snackbar
      v-model="snackBarSecondPurchaseWarning"
      color="red darken-2"
      elevation="24"
      :vertical="vertical"
      shaped
    >
      Warning: You Already Have An Active Package

      <template v-slot:action="{ attrs }">
        <v-btn
          color="white"
          text
          v-bind="attrs"
          @click="snackBarSecondPurchaseWarning = false"
          left
        >
          Close
        </v-btn>
        <v-btn color="white" text v-bind="attrs" @click="allowPurchaseProcess">
          Continue
        </v-btn>
      </template>
    </v-snackbar>

    <v-data-table
      :headers="headers"
      :items="packages"
      class="elevation-5"
      sort-by="package_order"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-container>
          <v-row>
          <v-toolbar-title>Available Packages: {{packages.length}}</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          
          <v-spacer v-if="$vuetify.breakpoint.mdAndUp"></v-spacer>
          <router-link to="/shoppingCart">
            <v-btn color="yellow" role="button">
              <v-icon role="button">mdi-cart-check</v-icon>
              {{ returnQuantity }} Shopping Cart
            </v-btn>
          </router-link>
          </v-row>
        </v-container>
          <v-dialog v-model="dialog" max-width="500px" persistent>
            <v-card justify="center">
              <v-card-title>
                <span class="text-h5">Gift Card</span>
              </v-card-title>
              <!-- Gift Card Form -->
              <v-container>
                <v-row justify="center" style="min-height: 160px">
                  <v-col cols="6">
                    <v-form
                      ref="form"
                      height="100"
                      width="500"
                      v-model="valid"
                      lazy-validation
                      class="class-form mx-auto white"
                      @submit.prevent="submitGiftCard"
                      justify="center"
                      align="center"
                    >
                      <v-text-field
                        v-model.number="giftCardCost"
                        class="mt-0 pt-0"
                        type="number"
                        style="width: 300px"
                        label="Gift Card Amount: $"
                        min=10
                      ></v-text-field>
                      <v-row justify="center" align="center">
                        <v-col>
                          <v-btn class="mr-4" type="submit" :disabled="invalid">
                            Purchase
                          </v-btn>
                        </v-col>
                      </v-row>
                    </v-form>
                  </v-col>
                </v-row>
              </v-container>
              <!-- End of Gift Card Form -->
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="blue darken-1" text @click="close">
                  Cancel
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-toolbar>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <!-- <v-btn v-if="subscribeBtn(item)" color="green" small class="mr-2" @click="redirectSubscription(item)">
          Subscribe!
        </v-btn> -->
        <v-btn @click="addToCart(item)">
          <v-icon class="mr-2" justify="center"> mdi-cart-plus </v-icon>Purchase
        </v-btn>
        <!-- <v-icon v-else  class="mr-2" @click="purchasePackage(item)">
          mdi-cart-plus
        </v-icon> -->
      </template>
    </v-data-table>
    <br />
    <br />
  </v-container>
</template>

<script>
import packageDetailService from "../services/PackageDetailService";
// import packagePurchaseService from "../services/PackagePurchaseService";

export default {
  name: "client-package-purchase-table",
  components: {},
  data() {
    return {
      headers: [
        {
          text: "Package Description",
          align: "start",
          value: "description",
        },
        { text: "Cost $", value: "package_cost", sortable: true },
        { text: "Purchase", value: "actions", sortable: false },
      ],
      packages: [],
      newClient: true,
      packagePurchase: {
        package_purchase_id: "",
        client_id: "",
        date_purchased: "",
        package_id: "",
        is_expired: "",
        classes_remaining: "",
        activation_date: "",
        expiration_date: "",
        total_amount_paid: "",
        is_monthly_renew: "",
        discount: "",
        package_description: "",
      },
      purchaseItem: {},
      dialog: false,
      packageName: {},
      giftCardCost: 10,
      snackBarSecondPurchaseWarning: false,
      allowPurchase: false,
      vertical: true,
      lineItems: [],
    };
  },
  created() {
    if (this.$store.state.clientDetails.is_new_client == false) {
      this.newClient = false;
    }
    this.getPublicPackagesTable();
    this.lineItems = this.$store.state.lineItems;
  },
  methods: {
    getPublicPackagesTable() {
      packageDetailService.getAllPublicPackages().then((response) => {
        if (response.status == 200) {
          // adjust this commit in the future perhaps
          this.$store.commit("SET_PACKAGE_LIST", response.data);
          if (this.newClient == false) {
            this.packages = response.data.filter((item) => {
              return !item.description.includes("New Client");
            });
          } else {
            this.packages = response.data;
          }
        } 
      })
      .catch((error) => {
            const response = error.response;
            if (response.status === 400) {
              alert(error.response.data.message)
            } else {
          alert("Error retrieving package information");
        }
      });
    },
    openSnackBarWarning() {
      this.snackBarSecondPurchaseWarning = true;
    },
    allowPurchaseProcess() {
      this.snackBarSecondPurchaseWarning = false;
      this.allowPurchase = true;
      this.purchasePackage(this.purchaseItem);
    },
    /* NOT USING THIS ANYMORE
    purchasePackage(item) {
      // snack bar alert calls this here if they want to proceed

      this.purchaseItem = Object.assign({}, item);

      this.packageName = this.purchaseItem.description.toLowerCase();
      this.$root.$refs.A.getActivePurchaseServerRequest();
      this.$root.$refs.B.getPackageHistoryTable();
      // TODO: handle new client purchase here
      if (
        this.packageName.includes("new") &&
        this.$store.state.clientDetails.is_new_client == false
      ) {
        alert("You are not a new client, please choose a different package");
      } else {
        // GIFT CARD LOGIC
        if (this.packageName.includes("gift")) {
          this.dialog = true;
          this.packagePurchase.client_id =
            this.$store.state.clientDetails.client_id;
          const jsonDate = new Date().toJSON();
          this.packagePurchase.date_purchased = jsonDate;
          this.packagePurchase.package_id = this.purchaseItem.package_id;
          this.packagePurchase.is_expired = false;
          this.allowPurchase = true;
        } else {
          // CURRENTLY ACTIVE PURCHASE PREVENTION SNACK BAR
          if (this.$store.state.activePackageList.length < 1) {
            // if they don't have an active package
            this.allowPurchase = true;
          }
          if (
            this.$store.state.activePackageList.length >= 1 &&
            !this.allowPurchase
          ) {
            this.allowPurchase = false;
            this.openSnackBarWarning();
          }
          // IF PURCHASE IS ALLOWED BY USER
          if (this.allowPurchase) {
            this.packagePurchase.activation_date = "";
            this.packagePurchase.expiration_date = "";
            // SUBSCRIPTION LOGIC
            if (this.purchaseItem.unlimited == true) {
              let latestExpDate = new Date();
              this.$store.state.activePackageList.forEach((item) => {
                let currentIndexExpDate = new Date(
                  item.expiration_date.replaceAll("-", "/")
                );
                if (
                  currentIndexExpDate > latestExpDate &&
                  item.unlimited
                ) {
                  latestExpDate = currentIndexExpDate.setDate(
                    currentIndexExpDate.getDate() + 1
                  );
                }
              });

              this.packagePurchase.activation_date = latestExpDate;

              if (this.purchaseItem.package_duration > 0) {
                // console.log(new Date(this.packagePurchase.activation_date))
                this.packagePurchase.expiration_date = this.addMonths(
                  new Date(this.packagePurchase.activation_date),
                  this.purchaseItem.package_duration
                );
              }

              // if (this.packageName.includes("one month")) {
              //   this.packagePurchase.expiration_date = this.addMonths(
              //     new Date(),
              //     1
              //   );
              // } else if (this.packageName.includes("six month")) {
              //   this.packagePurchase.expiration_date = this.addMonths(
              //     new Date(),
              //     6
              //   );
              // }
            }
            this.packagePurchase.total_amount_paid =
              this.purchaseItem.package_cost;
            this.packagePurchase.is_monthly_renew = false;

            this.packagePurchase.client_id =
              this.$store.state.clientDetails.client_id;
            const jsonDate = new Date().toJSON();
            this.packagePurchase.date_purchased = jsonDate;
            this.packagePurchase.package_id = this.purchaseItem.package_id;
            this.packagePurchase.is_expired = false;
            this.packagePurchase.classes_remaining =
              this.purchaseItem.classes_amount;
            if (this.purchaseItem.unlimited == false) {
              this.packagePurchase.expiration_date = this.addMonths(
                new Date(),
                12
              );
            }
            packagePurchaseService
              .createPackagePurchase(this.packagePurchase)
              .then((response) => {
                if (response.status == 201) {
                  // alert("Succesfully purchased package");
                  // call method that updates the list of active packages
                  this.$root.$refs.A.getActivePurchasePackageTable();
                  this.$root.$refs.B.getPackageHistoryTable();
                  this.allowPurchase = false;
                }
              });
          }
        }
      }
    },
    */
    addToCart(item) {
      if (item.description.includes("Gift")) {
        if (this.$store.state.clientDetails.email == "") {
          alert("Please add an email to your account");
        } else {// check the store and see if there's something in there already
          if (this.$store.state.lineItems.length > 0) {
            

            // check that only one subscription is allowed
           
            let continueWithShopping = true;
            for (
              let index = 0;
              index < this.$store.state.lineItems.length;
              index++
            ) {
              let element = this.$store.state.lineItems[index];
              if (
                element.is_monthly_renew
              ) {
                continueWithShopping = false;
                alert(
                  "Please choose between one subscription or one-time purchase packages"
                );
                break;
              }
            }

            if (continueWithShopping) {
              this.packagePurchase.package_id = item.package_id;
              this.packagePurchase.client_id = this.$store.state.clientDetails.client_id;
              this.packagePurchase.classes_remaining = item.classes_remaining;
              this.packagePurchase.total_amount_paid = item.package_cost;
              this.packagePurchase.is_monthly_renew = false;

              this.dialog = true;
            }
          } else if (this.$store.state.lineItems.length == 0) {
            this.packagePurchase.package_id = item.package_id;
              this.packagePurchase.client_id = this.$store.state.clientDetails.client_id;
              this.packagePurchase.classes_remaining = item.classes_remaining;
              this.packagePurchase.total_amount_paid = item.package_cost;
              this.packagePurchase.is_monthly_renew = false;


            this.dialog = true;
          }
        }
        /// GIFT CARD LOGIC ENDS
      } else if (this.$store.state.lineItems.length > 0) {
        // check the store and see if there's something in there already

        // check that only one subscription is allowed
        let continueWithShopping = true;
        for (
          let index = 0;
          index < this.$store.state.lineItems.length;
          index++
        ) {
          if (
            item.unlimited &&
            item.is_recurring
          ) {
            continueWithShopping = false;
            alert(
              "Please choose between one subscription or one-time purchase packages"
            );
            break;
          }
          let element = this.$store.state.lineItems[index];
           if (
            element.is_monthly_renew
          ) {
            continueWithShopping = false;
            alert(
              "Please choose between one subscription or one-time purchase packages"
            );
            break;
          }
        }

        if (continueWithShopping) {
          let obj = new Object();
          obj.price = item.package_cost;
          obj.quantity = 1;
          obj.productName = item.description;
          obj.client_id = this.$store.state.clientDetails.client_id;
          obj.package_id = item.package_id;
          obj.classes_remaining = item.classes_amount;
          obj.is_monthly_renew = (item.unlimited && item.is_recurring);
          obj.total_amount_paid = item.package_cost;
          this.lineItems.push(obj);
          this.$store.commit("SET_STRIPE_LINE_ITEMS", this.lineItems);
          alert("Added to cart.");
        }
      } else if (this.$store.state.lineItems.length == 0) {
        let obj = new Object();
        obj.price = item.package_cost;
        obj.quantity = 1;
        obj.productName = item.description;
        obj.client_id = this.$store.state.clientDetails.client_id;
        obj.package_id = item.package_id;
        obj.classes_remaining = item.classes_amount;
        obj.is_monthly_renew = (item.unlimited && item.is_recurring);
        obj.total_amount_paid = item.package_cost;
        this.lineItems.push(obj);
        this.$store.commit("SET_STRIPE_LINE_ITEMS", this.lineItems);
        alert("Added to cart.");
      }
    },
    subscribeBtn(item) {
      if (item.description.includes("Month Package")) {
        return true;
      }
    },
    gift(item) {
      if (item.description.includes("Gift")) {
        return true;
      }
    },
    redirectSubscription(item) {
      if (item.description.includes("One Month Package")) {
        this.$router.push({ name: "checkout1Month" });
      } else if (item.description.includes("Six Month Package")) {
        this.$router.push({ name: "checkout6Month" });
      }
    },
    submitGiftCard() {
      // check the store and see if there's something in there already
      if (this.$store.state.lineItems.length > 0) {
        
        // check that only one subscription is allowed
        
        let continueWithShopping = true;
        for (
          let index = 0;
          index < this.$store.state.lineItems.length;
          index++
        ) {
          let element = this.$store.state.lineItems[index];
          if (
            element.is_monthly_renew 
          ) {
            continueWithShopping = false;
            alert(
              "Please choose between one subscription or one-time purchase packages"
            );
            break;
          }
        }

        if (continueWithShopping) {
          let obj = new Object();
          obj.price = this.giftCardCost;
          obj.quantity = 1;
          obj.productName = "Gift Card";
          obj.client_id = this.$store.state.clientDetails.client_id;
          obj.package_id = this.packagePurchase.package_id;
          obj.classes_remaining = this.packagePurchase.classes_remaining;
          obj.is_monthly_renew = false;
          obj.total_amount_paid = this.giftCardCost;
          this.lineItems.push(obj);
          this.$store.commit("SET_STRIPE_LINE_ITEMS", this.lineItems);
          this.close();
          alert("Added to cart.");
        }
      } else if (this.$store.state.lineItems.length == 0) {
        let obj = new Object();
          obj.price = this.giftCardCost;
          obj.quantity = 1;
          obj.productName = "Gift Card";
          obj.client_id = this.$store.state.clientDetails.client_id;
          obj.package_id = this.packagePurchase.package_id;
          obj.classes_remaining = this.packagePurchase.classes_remaining;
          obj.is_monthly_renew = false;
          obj.total_amount_paid = this.giftCardCost;
          this.lineItems.push(obj);
          this.$store.commit("SET_STRIPE_LINE_ITEMS", this.lineItems);
          this.close();
          alert("Added to cart.");
      }

      // old code
      // // for gift card form
      // this.packagePurchase.total_amount_paid = this.giftCardCost;

      // packagePurchaseService
      //   .createPackagePurchase(this.packagePurchase)
      //   .then((response) => {
      //     if (response.status == 201) {
      //       alert("Succesfully purchased gift card");
      //       // call method that updates the list of active packages
      //       this.$root.$refs.A.getActivePurchasePackageTable();
      //       this.$root.$refs.B.getPackageHistoryTable();
      //       this.giftCardCost = 10;
      //       this.allowPurchase = false;
      //       this.close();
      //     }
      //   });
    },
    close() {
      this.dialog = false;
      this.giftCardCost = 10;
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
  },
  computed: {
    returnQuantity() {
      if (this.$store.state.lineItems.length > 0) {
        return this.$store.state.lineItems.length;
      }
      return "";
    },
  },
};
</script>

<style></style>