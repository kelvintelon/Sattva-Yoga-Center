import axios from 'axios';

export default {
    getUserPurchasedPackages() {
        return axios.get('userPackagePurchaseList')
    },
    getActiveUserPurchasedPackages() {
      return axios.get('activeUserPackagePurchaseList')  
    },
    getUserPurchasedPackagesByClientId(clientId) {
        return axios.get(`userPackagePurchaseListByClientId/${clientId}`)
    },
    getPaginatedUserPurchasedPackages(thisPage, thisPageSize,thisSortBy, thisSortDesc) {
        return axios.get('userPaginatedPackagePurchaseList', {params: {page: thisPage, pageSize: thisPageSize, sortBy: thisSortBy, sortDesc: thisSortDesc} })
    },
    getPaginatedUserPurchasedPackagesByClientId(clientId, thisPage, thisPageSize,thisSortBy, thisSortDesc) {
        return axios.get(`userPaginatedPackagePurchaseListByClientId/${clientId}`, {params: {page: thisPage, pageSize: thisPageSize, sortBy: thisSortBy, sortDesc: thisSortDesc} })
    },
    getActivePaginatedUserPurchasedPackagesByClientId(clientId, thisPage, thisPageSize,thisSortBy, thisSortDesc) {
        return axios.get(`userActivePaginatedPackagePurchaseListByClientId/${clientId}`, {params: {page: thisPage, pageSize: thisPageSize, sortBy: thisSortBy, sortDesc: thisSortDesc} })
    },
    createPackagePurchase(packagePurchase) {
        return axios.post('/createPackagePurchase', packagePurchase)
    },
    resendEmail(resendEmailObj) {
        return axios.post('/resendEmail', resendEmailObj)
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
