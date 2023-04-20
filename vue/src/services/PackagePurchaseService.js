import axios from 'axios';

export default {

    createPackagePurchase(packagePurchase) {
        return axios.post('/createPackagePurchase', packagePurchase)
    },
    getUserPurchasedPackages() {
        return axios.get('userPackagePurchaseList')
    },
    expirePackage(packagePurchaseObject) {
        return axios.put(`/expirePackage`, packagePurchaseObject)
    },
    decrementByOne(packageId){
        return axios.put(`/decrement/${packageId}`)
    },
    incrementByOne(packageId){
        return axios.put(`/increment/${packageId}`)
    },
    getUserPurchasedPackagesByUserId(userId) {
        return axios.get(`userPackagePurchaseListByUserId/${userId}`)
    },
    getUserPurchasedPackagesByClientId(clientId) {
        return axios.get(`userPackagePurchaseListByClientId/${clientId}`)
    },
    updatePackagePurchase(object){
        return axios.put(`/updatePackagePurchase`, object)
    },
    getAllSharedActiveQuantityPackages(){
        return axios.get('getAllSharedActiveQuantityPackages')
    },
    getAllSharedActiveQuantityPackagesByClientId(clientId){
        return axios.get(`getAllSharedActiveQuantityPackagesByClientId/${clientId}`)
    },

}
