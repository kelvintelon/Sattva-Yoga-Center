import axios from 'axios';

export default {


    getAllEvents() {
        return axios.get(`/hundredEventList`)
    },
    createEvent(event) {
        return axios.post(`/createEvent`, event)
    },
    get100Events() {
        return axios.get(`/100eventList`)
    },
    

}