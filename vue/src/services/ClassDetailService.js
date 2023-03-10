import axios from 'axios';

export default {

    createClass(classDetails) {
        return axios.post('/createClass', classDetails)
    },

    getAllClasses() {
        return axios.get(`/classList`)
    },
    
    updateClass(editedClass) {
        return axios.put(`/updateClass`, editedClass)
    },
    deleteClass(classID) {
        return axios.delete(`/deleteClass/${classID}`)
    },

    getAllClientClasses() {
        return axios.get(`/clientClassList`)
    },

    registerForClass(clientClass) {
        return axios.post(`/registerForClass`, clientClass)
    },

    removeClassForClient(classId) {
        return axios.delete(`/removeClassForClient/${classId}`)
    },

}
