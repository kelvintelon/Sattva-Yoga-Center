import axios from 'axios';

export default {

    getNewsAndEvents() {
        return axios.get(`/getNewsAndEventsDescription`)
    },
    getClassSchedule() {
        return axios.get(`/getClassScheduleDescription`)
    },
    updateClassScheduleDescription(editedDescription) {
        return axios.put(`/updateClassScheduleDescription`, editedDescription)
    },
    updateNewsAndEventsDescription(editedDescription) {
        return axios.put(`/updateNewsAndEventsDescription`, editedDescription)
    }
}
