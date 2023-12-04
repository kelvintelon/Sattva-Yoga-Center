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
    return axios.post(`createFamily`, newFamilyObj);
  },
  updateFamilyName(updateFamilyObj) {
    return axios.put('updateFamilyName', updateFamilyObj);
  },
  deleteFamily(familyObjId) {
    return axios.delete(`deleteFamily/${familyObjId}`);
  },
  getFamilyDetailsByFamilyId(familyObjId) {
    return axios.get(`getFamilyDetailsByFamilyId/${familyObjId}`)
  },
  removeFamilyMembersFromSelectedClients(listOfClients) {
    return axios.put('removeFamilyMembersFromSelectedClients', listOfClients)
  },
  registerMultipleClientsForFamily(listOfClients) {
    return axios.post('registerMultipleClientsForFamily', listOfClients)
},
}
