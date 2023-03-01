import axios from 'axios';

export default {

    registerClient(clientDetails) {
        return axios.post('/registerClient', clientDetails)
    },
    
    getClientDetailsOfLoggedInUser() {
        return axios.get('/getClientDetails')
    },
    getClientDetailsByClientId(clientId) {
        return axios.get(`/getClientDetailsByClientId/${clientId}`)
    },

    updateClientDetails(clientDetails) {
        return axios.put(`/updateClientDetails`, clientDetails )
    },

    getClientList() {
        return axios.get('/clientList')
    },

    removeClient(clientId) {
        return axios.delete(`/removeClient/${clientId}`)
    },

}
