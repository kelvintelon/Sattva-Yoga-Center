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
}
