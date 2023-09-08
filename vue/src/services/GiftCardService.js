import axios from 'axios';

export default {

    retrieveGiftCard(giftCardObject) {
        return axios.get(`/retrieveGiftCard/${giftCardObject.client_id}/${giftCardObject.code}`)
    },

}