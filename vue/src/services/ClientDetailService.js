import axios from 'axios';

export default {

    registerClient(clientDetails) {
        return axios.post('/registerClient', clientDetails)
    },
    
    getClientDetailsOfLoggedInUser() {
        return axios.get('/getClientDetails')
    },

}
