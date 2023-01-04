import axios from 'axios';

export default {

    createPackage(packageObject) {
        return axios.post(`/createPackage`, packageObject)
    },
    getAllPackages() {
        return axios.get(`/packageList`)
    },
    updatePackage(packageObject) {
        return axios.put(`/updatePackage`, packageObject)
    },
    deletePackage(packageId) {
        return axios.delete(`/deletePackage/${packageId}`)
    },


}