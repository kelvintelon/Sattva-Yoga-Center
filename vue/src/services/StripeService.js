import axios from 'axios';

export default {

  purchaseLocalStorageItems(lineItems){
    return axios.post(`/stripe/purchaseLocalStorageItems`, lineItems)
  },

  purchaseClientCheckout(clientCheckout) {
    return axios.post(`/stripe/purchaseTerminal`, clientCheckout)
  },

  returnPaymentMethodOptions(clientId) {
    return axios.get(`/stripe/returnPaymentMethodOptions/${clientId}`)
  },

  addPaymentMethodThroughReader(clientId) {
    return axios.post(`/stripe/addPaymentMethodThroughReader/${clientId}`)
  },
  
  addPaymentMethodManually(clientId, cardObject) {
    return axios.post(`/stripe/addPaymentMethodManually/${clientId}`, cardObject)
  }


  // updateOneMonthDb(oneMonth){
  //   return axios.post(`/stripe/purchaseOneMonth`, oneMonth)
  // },

  // updateSixMonthDb(sixMonth){
  //   return axios.post(`/stripe/purchaseSixMonth`, sixMonth)
  // }

  // getFamilyList() {
  //   return axios.get(`/getFamilyList`)
  // },

  // addMultipleClientsForFamily(listOfClients){
  //   return axios.post('addMultipleClientsForFamily', listOfClients)
  // },

  // addMultipleClientsToNewFamily(listOfClients){
  //   return axios.post('addMultipleClientsToNewFamily', listOfClients);
  // },

}