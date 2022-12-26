import axios from 'axios';

export default {

    createClass(classDetails) {
        return axios.post('/createClass', classDetails)
    },
    
}
