import axios from 'axios';

export default {


    getAllEvents() {
        return axios.get(`/eventList`)
    },
    createEvent(event) {
        return axios.post(`/createEvent`, event)
    },
    get100Events() {
        return axios.get(`/100eventList`)
    },
    deleteEvent(eventID) {
        return axios.delete(`/deleteEvent/${eventID}`)
    },
    updateEvent(event) {
        return axios.put('/updateEvent', event)
    },
    registerForEvent(signup) {
        return axios.post(`/registerForEvent`, signup)
    },
    getAllClientEvents(){
        return axios.get(`/clientEventList`)
    },
    removeEventForClient(eventID){
        return axios.delete(`/removeEventForClient/${eventID}`)
    }


}