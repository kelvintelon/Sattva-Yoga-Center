import axios from 'axios';

export default {


    getAllEvents() {
        return axios.get(`/eventList`)
    },
    getSignUpAggregate() {
        return axios.get(`/getSignUpAggregate`)
    },
    createEvent(event) {
        return axios.post(`/createEvent`, event)
    },
    get100Events() {
        return axios.get(`/100eventList`)
    },
    get100EventsForClient(clientId) {
        return axios.get(`/100eventList/${clientId}`)
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
    getAllClientEventsByClientId(clientId){
        return axios.get(`/clientEventListByClientId/${clientId}`)
    },
    removeEventForClient(eventID){
        return axios.delete(`/removeEventForClient/${eventID}`)
    },
    removeEventForClientByClientId(eventID, clientID){
        return axios.delete(`/removeEventForClient/${eventID}/${clientID}`)
    },
    getEventDetailsByEventId(eventID){
        return axios.get(`getEventDetailsByEventId/${eventID}`)
    },
    retrievePackagePurchaseId(eventId, clientId) {
        return axios.get(`/retrievePackagePurchaseId/${eventId}/${clientId}`)
    },
    removeEventForSelectedClients(listOfClients) {
        return axios.put('removeEventForSelectedClients', listOfClients)
    },
    registerMultipleClientsForEvent(listOfClients) {
        return axios.post('registerMultipleClientsForEvent', listOfClients)
    },
    registerNewClientForEvent(newClient) {
        return axios.post('registerNewClientToEvent',newClient)
    },  
    reconcileClassesForClient(clientId) {
        return axios.put(`reconcileClassesForClient/${clientId}`)
    },


}