import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';
import Login from './views/Login.vue';
import Register from './views/Register.vue';
import ResetFS from './views/ResetFS.vue';
import ResetTS from './views/ResetTS.vue';

Vue.use(Router);

export const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/home',
      component: Home
    },
    {
      path: '/login',
      component: Login
    },
    {
      path: '/resetfs',
      component: ResetFS
    },
    
    {
      path: '/resetts',
      component: ResetTS
    },
    {
      path: '/register',
      component: Register
    },
    {
      path: '/profile',
      name: 'profile',
      // lazy-loaded
      component: () => import('./views/Profile.vue')
    },
      {
        path: "/shoelasts",
        name: "shoelasts",
        component: () => import("./views/components/ShoelastList")
      },
      {
        path: "/shoelasts/:id",
        name: "shoelast-details",
        component: () => import("./views/components/Shoelast")
      },
      {
        path: "/add",
        name: "add",
        component: () => import("./views/components/AddShoelast")
      }
  ]
});

// router.beforeEach((to, from, next) => {
//   const publicPages = ['/login', '/register', '/home'];
//   const authRequired = !publicPages.includes(to.path);
//   const loggedIn = localStorage.getItem('user');

//   // trying to access a restricted page + not logged in
//   // redirect to login page
//   if (authRequired && !loggedIn) {
//     next('/login');
//   } else {
//     next();
//   }
// });
