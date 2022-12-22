import axios from 'axios';

export default {

    createPackagePurchase(packagePurchase) {
        return axios.post('/createPackagePurchase', packagePurchase)
    },

}
