import axios from 'axios';

export default {

    registerClient(clientDetails) {
        return axios.post('/registerClient', clientDetails)
    },
    
    getClientDetailsOfLoggedInUser() {
        return axios.get('/getClientDetails')
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
