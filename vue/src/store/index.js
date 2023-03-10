import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

/*
 * The authorization header is set for axios when you login but what happens when you come back or
 * the page is refreshed. When that happens you need to check for the token in local storage and if it
 * exists you should set the header so that it will be attached to each request
 */
const currentToken = localStorage.getItem('token')
const currentUser = JSON.parse(localStorage.getItem('user'));
const currentClient = JSON.parse(localStorage.getItem('clientDetails'))

if(currentToken != null) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${currentToken}`;
}

export default new Vuex.Store({
  state: {
    token: currentToken || '',
    user: currentUser || {},
    // clientId: 0,
    clientDetails: currentClient || {},
    classList: [],
    clientEventList: [],
    teacherList: [],
    packageList: [],
    activePackageList: [],
    packageHistoryList: [],
    eventList: [],
  },
  mutations: {
    SET_AUTH_TOKEN(state, token) {
      state.token = token;
      localStorage.setItem('token', token);
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    },
    SET_USER(state, user) {
      state.user = user;
      localStorage.setItem('user',JSON.stringify(user));
    },
    LOGOUT(state) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('clientDetails')
      state.token = '';
      state.user = {};
      state.clientDetails = {};
      axios.defaults.headers.common = {};
    },
    SET_CLIENT_DETAILS(state, clientDetailsObject) {
      state.clientDetails = clientDetailsObject;
      localStorage.setItem('clientDetails', JSON.stringify(clientDetailsObject));
    },
    SET_CLASS_LIST(state, retrievedClassList) {
      state.classList = retrievedClassList;
    },
    SET_CLIENT_EVENT_LIST(state, retrievedClientList) {
      state.clientEventList = retrievedClientList;
    },
    SET_TEACHER_LIST(state, retrievedTeacherList) {
      state.teacherList = retrievedTeacherList;
    },
    SET_PACKAGE_LIST(state, retrievedPackageList) {
      state.packageList = retrievedPackageList;
    },
    SET_ACTIVE_PACKAGE_LIST(state, retrievedActivePackageList) {
      state.activePackageList = retrievedActivePackageList;
    },
    SET_PACKAGE_HISTORY_LIST(state, retrievedPackageHistoryList) {
      state.packageHistoryList = retrievedPackageHistoryList;
    },
    SET_CLIENT_DETAILS_NEW_CLIENT(state, newInformation) {
      state.clientDetails.is_new_client = newInformation;
    },
    SET_CLIENT_DETAILS_RED_FLAG(state, newInformation) {
      state.clientDetails.redFlag = newInformation;
    },
    SET_EVENT_LIST(state, retrievedEventList) {
      state.eventList = retrievedEventList;
    }
  }
})
