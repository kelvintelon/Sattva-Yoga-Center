import axios from 'axios';

export default {
    
    getVideoFile() {
        return axios.get('/get-file', {
            headers: { rangeStartValue: 0, rangeEndValue: 1024 }
        }, {  responseType: 'arraybuffer' })
    },

}
