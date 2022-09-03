<template>
  <div class="list row">
    

    <div class="col-md-12">
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
      <h4>Comments List</h4>
      <ul class="list-group" id="comments-list">
        <li
          class="list-group-item"
          :class="{ active: index == currentIndex }"
          v-for="(comment, index) in comments"
          :key="index"
          @click="setActiveComment(comment, index)"
        >
          {{ comment.description }}
        </li>

        <div>
          <AddComment @clicked="addNewCommentClbc" v-bind:currentShoelastId="$props.currentShoelastId"></AddComment>
        </div>
      </ul>
    </div>

    <div class="col-md-6">
      <div v-if="currentComment && isAdminCurrentUser">
        <button class="m-3 btn btn-sm btn-danger" @click="removeComment">
          Remove comment
        </button>
      </div>
      
    </div>
  </div>
</template>

<script>
import AddComment from "./AddComment.vue"
import CommentDataService from "../../services/CommentDataService";

export default {
  name: "comments-list",
  props: ['currentShoelastId', 'isAdminCurrentUser'],
  components: {
    AddComment
  },
  data() {
    return {
      comments: [],
      currentComment: null,
      currentIndex: -1,

      page: 1,
      count: 0,
      pageSize: 3
    };
  },
  methods: {

    addNewCommentClbc (value) {
      //console.log(value) // someValue
      this.refreshList();
    },
    getRequestParams(tutid, page, pageSize) {
      let params = {};

      params["tutid"] = tutid;

      if (page) {
        params["page"] = page - 1;
      }

      if (pageSize) {
        params["size"] = pageSize;
      }

      return params;
    },

    retrieveComments() {

      const tutid = this.currentShoelastId;
      const params = this.getRequestParams(
        tutid,
        this.page,
        this.pageSize
      );
      //console.log("params:")
      //console.log(params)
      CommentDataService.getAll(params)
        .then((response) => {
          const { comments, totalItems } = response.data;
          this.comments = comments;
          this.count = totalItems;

          //console.log(response.data);
        })
        .catch((e) => {
          console.log(e);
        });
    },

    handlePageChange(value) {
      this.page = value;
      this.retrieveComments();
    },


    refreshList() {
      this.page = 1;
      this.retrieveComments();
      this.currentComment = null;
      this.currentIndex = -1;
    },

    setActiveComment(comment, index) {
      console.log("comment:");
      console.log(comment);
      this.currentComment = comment;
      this.currentIndex = index;
    },

    removeComment() {

      console.log("currentComment:")
      console.log(this.currentComment)
      CommentDataService.delete(this.currentComment.id)
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
    this.retrieveComments();
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
