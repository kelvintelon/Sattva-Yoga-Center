import axios from 'axios';

export default {

    registerClass(classAttendance) {
        return axios.post('/registerForClass', classAttendance)
    },

}
