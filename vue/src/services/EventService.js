import axios from 'axios';

export default {


    getAllEvents() {
        return axios.get(`/eventList`)
    },
    
    

}