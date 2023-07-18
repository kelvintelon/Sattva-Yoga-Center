import axios from 'axios';

export default {
    
    getVideoFilenames() {
        return axios.get('/get-all-filenames')
    },

}
