import axios from 'axios';

export default {

    uploadClients(clientCSV) {
        return axios.post(`/uploadClients`, clientCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },

    

}
