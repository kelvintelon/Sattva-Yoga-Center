import Vue from 'vue'
import Router from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Logout from '../views/Logout.vue'
import Register from '../views/Register.vue'
import store from '../store/index'
import ClientRegistration from '../views/ClientRegistration.vue'
import ClassRegistration from '../views/ClassRegistration.vue'
import Checkout from '../views/Checkout.vue'
import ProfilePage from '../views/ProfilePage.vue'
import LoginLoader from '../views/LoginLoader.vue'
import EditProfilePage from '../views/EditProfilePage.vue'
import TeacherManagementPage from '../views/TeacherManagementPage.vue'
import ClassManagement from '../views/ClassManagement.vue'
import ClientManagement from '../views/ClientManagement.vue'
import PackageManagement from '../views/PackageManagement.vue'
import ClientPackageManagement from '../views/ClientPackageManagement.vue'
import EventAttendanceDetails from '../views/EventAttendanceDetails.vue'

Vue.use(Router)

/**
 * The Vue Router is used to "direct" the browser to render a specific view component
 * inside of App.vue depending on the URL.
 *
 * It also is used to detect whether or not a route requires the user to have first authenticated.
 * If the user has not yet authenticated (and needs to) they are redirected to /login
 * If they have (or don't need to) they're allowed to go about their way.
 */

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
      meta: {
        requiresAuth: false
      }
    },
    {
      path: "/login",
      name: "login",
      component: Login,
      meta: {
        requiresAuth: false
      }
    },
    {
      path: "/logout",
      name: "logout",
      component: Logout,
      meta: {
        requiresAuth: false
      }
    },
    {
      path: "/register",
      name: "register",
      component: Register,
      meta: {
        requiresAuth: false
      }
    },
    {
      path:"/clientRegistration",
      name:"clientRegistration",
      component: ClientRegistration,
      meta: {
        requiresAuth: false
      }
    },
    {
      path:"/registerForClass",
      name:"class-registration",
      component: ClassRegistration,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/checkout",
      name:"checkout",
      component: Checkout,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/myProfile",
      name:"profile-page",
      component: ProfilePage,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/loading",
      name:"login-loader",
      component: LoginLoader,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/editProfile",
      name:"edit-profile-page",
      component: EditProfilePage,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/teacherManagement",
      name:"teacher-management",
      component: TeacherManagementPage,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/classManagement",
      name:"class-management",
      component: ClassManagement,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/clientManagement",
      name:"client-management",
      component: ClientManagement,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/packageManagement",
      name:"package-management",
      component: PackageManagement,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/clientPackageManagement",
      name:"client-package-management",
      component: ClientPackageManagement,
      meta: {
        requiresAuth: true
      }
    },
    {
      path:"/eventDetails/:eventId",
      name:"event-attendance-details",
      component: EventAttendanceDetails,
      meta: {
        requiresAuth: true
      }
    },
  ]
})

router.beforeEach((to, from, next) => {
  // Determine if the route requires Authentication
  const requiresAuth = to.matched.some(x => x.meta.requiresAuth);

  // If it does and they are not logged in, send the user to "/login"
  if (requiresAuth && store.state.token === '') {
    next("/login");
  } else {
    // Else let them go to their next destination
    next();
  }
});

export default router;``