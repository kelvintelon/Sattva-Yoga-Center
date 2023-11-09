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

    uploadPackages(packagesCSV) {
        return axios.post(`/uploadPackages`, packagesCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },
    uploadSales(salesCSV) {
        return axios.post(`/uploadSales`, salesCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },
    uploadGiftReport(giftCardCSV) {
        return axios.post(`/uploadGiftCardReport`, giftCardCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },
    uploadAttendance(attendanceCSV) {
        return axios.post(`/uploadAttendance`, attendanceCSV, {headers: {Accept: 'application/json', 'Content-Type': 'multipart/form-data'} })
    },

    

}
