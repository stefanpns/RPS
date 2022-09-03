<template>
  <div class="list row">


    <div class="col-md-8">
      <div class="input-group mb-3">
        <input
          type="text"
          class="form-control"
          placeholder="Search by shoe size"
          v-model="searchSize"
        />
        <div class="input-group-append">
          <button
            class="btn btn-outline-secondary"
            type="button"
            @click="page = 1; searchedSize = searchSize; retrieveShoelasts();"
          >
            Search
          </button>
        </div>
        <div class="input-group-append">
          <button
            class="btn btn-outline-secondary"
            type="button"
            @click="resetFilter"
          >
            Reset filter
          </button>
        </div>

      </div>
    </div>

    <div class="col-md-12">
      <div class="mb-3">
        Items per Page:
        <select v-model="pageSize" @change="handlePageSizeChange($event)">
          <option v-for="size in pageSizes" :key="size" :value="size">
            {{ size }}
          </option>
        </select>
      </div>

      <b-pagination
        v-model="page"
        :total-rows="count"
        :per-page="pageSize"
        prev-text="Prev"
        next-text="Next"
        @change="handlePageChange"
      ></b-pagination>
    </div>

    <div class="col-md-6">
      <h4>Shoelasts list </h4> 
      <h4 v-if="searchedSize" style="color:#007bff">by number {{ searchedSize }} </h4>
      <h4 v-else style="color:#007bff">all numbers</h4>

      <div v-if="shoelasts.length">
        <ul class="list-group" id="shoelasts-list">
          <li
            class="list-group-item"
            :class="{ active: index == currentIndex }"
            v-for="(shoelast, index) in shoelasts"
            :key="index"
            @click="setActiveShoelast(shoelast, index)"
          >
            {{ shoelast.title }}
          </li>
        </ul>
      </div>
      <div v-else><h4>No entries.</h4></div>

      
      <div v-if="currentShoelast && isAdminCurrentUser">
      
        <button class="m-3 btn btn-sm btn-danger" @click="removeShoelast">
          Remove shoelast
        </button>
      </div>
      
      <div v-if="currentShoelast">
      
        <div v-if="imgurl" class="col-md-12">
          <img v-bind:src="imgurl" class="img-fluid" alt="no preview"/>
        </div>
      </div>



    </div>

    <div class="col-md-6">
      <div v-if="currentShoelast">
      
      
        <h4>Shoelast</h4>
        <div>
          <label><strong>Title:</strong></label> {{ currentShoelast.title }}
        </div>
        
        <div>
          <label><strong>Description:</strong></label> {{ currentShoelast.description }}
        </div>
        
        <div>
          <label><strong>Size:</strong></label> {{ currentShoelast.shoesize }}
        </div>

        
      

        <CommentsList :key="currentShoelast.id" v-bind:currentShoelastId="currentShoelast.id" :isAdminCurrentUser="isAdminCurrentUser"/>
      </div>
      <div v-else>
        <br />
        <p>You can click on a Shoelast.</p>
      </div>
    </div>
  </div>
</template>

<script>
import ShoelastDataService from "../../services/ShoelastDataService";
import CommentsList from './CommentsList.vue'


export default {
  name: "shoelasts-list",
  components: {
    CommentsList
  },
  
  props: ['isAdminCurrentUser'],
  data() {
    return {
      shoelasts: [],
      currentShoelast: null,
      currentIndex: -1,
      searchSize: null,
      searchedSize: null,
      imgurl: null,

      page: 1,
      count: 0,
      pageSize: 3,

      pageSizes: [3, 6, 9],
      
      componentKey: 0
    };
  },
  methods: {
    forceRerender() {
      this.componentKey += 1;
    },
    getRequestParams(searchSize, page, pageSize) {
      let params = {};

      if (searchSize) {
        params["shoesize"] = searchSize;
      }

      if (page) {
        params["page"] = page - 1;
      }

      if (pageSize) {
        params["size"] = pageSize;
      }

      return params;
    },

    resetFilter() {
      
      this.searchedSize = null;
      this.refreshList()
    },

    retrieveShoelasts() {
      const params = this.getRequestParams(
        this.searchedSize,
        this.page,
        this.pageSize
      );

      ShoelastDataService.getAll(params)
        .then((response) => {
          
          //this.searchedSize = this.searchSize;
          this.searchSize = null;
          this.currentShoelast = null;
          this.currentIndex = -1;
          const { shoelasts, totalItems } = response.data;
          this.shoelasts = shoelasts;
          this.count = totalItems;

          //console.log(response.data);
        })
        .catch((e) => {
          console.log(e);
        });
    },

    handlePageChange(value) {
      this.page = value;
      this.retrieveShoelasts();
    },

    handlePageSizeChange(event) {
      this.pageSize = event.target.value;
      this.page = 1;
      this.retrieveShoelasts();
    },

    refreshList() {
      
      this.searchSize = null;
      this.retrieveShoelasts();
      this.currentShoelast = null;
      this.currentIndex = -1;
    },

    setActiveShoelast(shoelast, index) {
      // console.log("shoelast:");
      // console.log(shoelast);
      this.imgurl = null;
      this.currentShoelast = shoelast;
      this.currentIndex = index;


      ShoelastDataService
      .getImage(this.currentShoelast.id)
      .then((response) => {
          console.log("THE IMAGE IS DOWN")
          this.imgurl = window.URL.createObjectURL(new Blob([response.data]));
            
            //const image = document.createElement('img');
            //image.src = url;
            // link.setAttribute('download', 'file.pdf'); //or any other extension
            //document.body.appendChild(image);
            // link.click();
        });
      

    },

    removeShoelast() {

      console.log("removing shoelast with id:");
      console.log(this.currentShoelast);
      ShoelastDataService.delete(this.currentShoelast.id)
        .then((response) => {
          console.log(response.data);
          this.refreshList();
        })
        .catch((e) => {
          console.log(e);
        });
    },
  },
  mounted() {
    this.retrieveShoelasts();
  },
};
</script>

<style>
.list {
  text-align: left;
  max-width: 750px;
  margin: auto;
}
</style>
