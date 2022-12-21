import axios from 'axios';

export default {

    registerClass(clientDetails) {
        return axios.post('/registerClass', clientDetails)
    },

}
