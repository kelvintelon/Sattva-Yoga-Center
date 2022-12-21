import axios from 'axios';

export default {

    registerClient(clientDetails) {
        return axios.post('/registerClient', clientDetails)
    },

    // register(user) {
    //     return axios.post('/register', user)
    // }

}
