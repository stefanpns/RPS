<template>
  <div class="submit-form">
    <div>
      <div class="form-group">
        <label for="description">New comment:</label>
        <input
          type="text"
          class="form-control"
          id="commentDescription"
          required
          v-model="comment.description"
          name="commentDescription"
        />
      </div>

      <button @click="saveComment" class="btn btn-success">Submit</button>
    </div>
  </div>
</template>

<script>
import CommentDataService from "../../services/CommentDataService";

export default {
  name: "add-comment",
  props: ['currentShoelastId'],
  data() {
    return {
      comment: {
        id: null,
        description: ""
      }
    };
  },
  methods: {
    saveComment() {


      //console.log("this.currentShoelastId:")
      //console.log(this.currentShoelastId)

      var tutid = this.currentShoelastId;
      var description = this.comment.description;

      var data = {
        "tutid": tutid,
        "description": description
      };

      const $this = this
      CommentDataService.create(data)
        .then(response => {
            $this.comment.description = "";
            $this.comment.id = null;
            console.log("New comment success")
            $this.$emit('clicked', 'someValue')
        })
        .catch(e => {
          
            console.log("New comment not success")
          //console.log(e);
        });
    },
    
    newComment() {
      this.comment = {};
    }
  }
};
</script>

<style>
.submit-form {
  max-width: 300px;
  margin: auto;
}
</style>
