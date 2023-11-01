import axios from 'axios';

export default {
    getClientList() {
        return axios.get('/clientList')
    },

    getPaginatedClients(thisPage, thisPageSize, thisSearch, thisSortBy, thisSortDesc) {
        // , {params: {page: 1, pageSize: 20} }
        return axios.get('/getPaginatedClients', {params: {page: thisPage, pageSize: thisPageSize, search: thisSearch, sortBy: thisSortBy, sortDesc: thisSortDesc} })
    },
    getPaginatedDuplicateClients(thisPage, thisPageSize, thisSearch) {
        // , {params: {page: 1, pageSize: 20} }
        return axios.get('/getPaginatedDuplicateClients', {params: {page: thisPage, pageSize: thisPageSize, search: thisSearch} })
    },

    registerClient(clientDetails) {
        return axios.post('/registerClient', clientDetails)
    },
    updateEmailForClient(clientDetails) {
        return axios.put(`/updateEmailForClient`, clientDetails)
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

    removeClient(clientId) {
        return axios.delete(`/removeClient/${clientId}`)
    },
    getDuplicateClients(){
        return axios.get('/duplicateList')
    },
    mergeClients(listOfClients) {
        return axios.put('/mergeClients', listOfClients)
    },
    registerNewClient(newClient) {
        return axios.post('/registerNewClient', newClient)
    }

}
