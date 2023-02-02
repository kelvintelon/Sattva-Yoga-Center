import axios from 'axios';

export default {


    getAllEvents() {
        return axios.get(`/eventList`)
    },
    createEvent(event) {
        return axios.post(`/createEvent`, event)
    },
    deleteEvent(eventID) {
        return axios.delete(`/deleteEvent/${eventID}`)
    },
    

}