import axios from 'axios';

export default {

  login(user) {
    return axios.post('/login', user)
  },

  register(user) {
    return axios.post('/register', user)
  },
  emailResetLink(email) {
    return axios.get(`/emailResetLink/${email}`)
  },
  checkIfTokenIsValid(token) {
    return axios.get(`/validateEmailToken/${token}`)
  },
  resetUsernameAndPassword(clientObjectToUpdate) {
    return axios.put('/resetUsernameAndPassword',clientObjectToUpdate)
  },
  resetPassword(passwordObject) {
    return axios.put('resetPassword', passwordObject)
  },
  sendEmailTokenRetrieveUsername(emailToken) {
  return axios.get(`/getUsernameFromEmailToken/${emailToken}`)
  },
}
