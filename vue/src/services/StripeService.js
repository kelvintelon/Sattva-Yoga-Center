import axios from 'axios';

export default {

  purchaseLocalStorageItems(lineItems){
    return axios.post(`/stripe/purchaseLocalStorageItems`, lineItems)
  },

  purchaseClientCheckout(clientCheckout) {
    return axios.post(`/stripe/purchaseTerminal`, clientCheckout)
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