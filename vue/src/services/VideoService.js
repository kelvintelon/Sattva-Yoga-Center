import axios from 'axios';

export default {
    
    getVideoFile() {
        return axios.get('/get-file')
    },

}
