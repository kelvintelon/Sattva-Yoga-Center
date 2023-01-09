import axios from 'axios';

export default {

    createPackagePurchase(packagePurchase) {
        return axios.post('/createPackagePurchase', packagePurchase)
    },
    getUserPurchasedPackages() {
        return axios.get('userPackagePurchaseList')
    }

}
