import axios from 'axios';

export default {

  
    getMap() {
        return axios.get('/getMap')
    },

   

}