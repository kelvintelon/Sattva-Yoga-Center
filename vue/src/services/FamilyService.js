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
  },
  createFamily(newFamilyObj) {
    return axios.post('createFamily', newFamilyObj);
  },
  updateFamilyName(updateFamilyObj) {
    return axios.post('updateFamilyName', updateFamilyObj);
  },

}
