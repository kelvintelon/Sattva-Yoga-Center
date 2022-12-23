import axios from 'axios';

export default {

    registerTeacher(teacher) {
        return axios.post('/registerTeacher', teacher)
    },
    
    getTeacherList() {
        return axios.get('/getTeacherList')
    },

    updateTeacher(teacher) {
        return axios.put(`/updateTeacherDetails`, teacher )
    },

    deleteTeacher(teacherId) {
        return axios.delete(`/deleteTeacher/${teacherId}`)
    }

}