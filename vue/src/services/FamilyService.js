import axios from 'axios';

export default {

  getFamilyList() {
    return axios.get(`/getFamilyList`)
  },

  addMultipleClientsForFamily(listOfClients){
    return axios.post('addMultipleClientsForFamily', listOfClients)
  },

  addMultipleClientsToNewFamily(listOfClients){
    return axios.post('addMultipleClientsToNewFamily', listOfClients);
  }



}
