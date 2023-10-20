import axios from 'axios';

export default {

    uploadClients(clientCSV) {
        return axios.post(`/uploadClients`, clientCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },

    uploadTeachers(teacherCSV) {
        return axios.post(`/uploadTeachers`, teacherCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },

    uploadEvents(eventCSV) {
        return axios.post(`/uploadEvents`, eventCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },

    

}
