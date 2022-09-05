<template>
  <div v-if="currentShoelast" class="edit-form">
    <h4>Shoelast</h4>
    <form>
      <div class="form-group">
        <label for="title">Title</label>
        <input type="text" class="form-control" id="title"
          v-model="currentShoelast.title"
        />
      </div>
      <div class="form-group">
        <label for="description">Description</label>
        <input type="text" class="form-control" id="description"
          v-model="currentShoelast.description"
        />
      </div>

      <div class="form-group">
        <label><strong>Shoe size:</strong></label>
        <input type="text" class="form-control" id="description"
          v-model="currentShoelast.shoesize"
        />
      </div>

      
      <div v-if="imgurl" class="form-group">
        <img v-bind:src="imgurl"/>
      </div>
      


      
    </form>

    <button class="badge badge-danger mr-2"
      @click="deleteShoelast"
    >
      Delete
    </button>

    <button type="submit" class="badge badge-success"
      @click="updateShoelast"
    >
      Update
    </button>
    <p>{{ message }}</p>
  </div>

</template>

<script>
import ShoelastDataService from "../../services/ShoelastDataService";

export default {
  name: "shoelast",
  data() {
    return {
      currentShoelast: null,
      message: '',
      imgurl: null
    };
  },
  methods: {
    getShoelast(id) {
      ShoelastDataService.get(id)
        .then(response => {
          this.currentShoelast = response.data;
          console.log(response.data);
          
        })
        .catch(e => {
          console.log(e);
        });

      
      
    },

    updateShoelast() {
      ShoelastDataService.update(this.currentShoelast.id, this.currentShoelast)
        .then(response => {
          console.log(response.data);
          this.message = 'The shoelast was updated successfully!';
        })
        .catch(e => {
          console.log(e);
        });
    },

    deleteShoelast() {
      ShoelastDataService.delete(this.currentShoelast.id)
        .then(response => {
          console.log(response.data);
          this.$router.push({ name: "shoelasts" });
        })
        .catch(e => {
          console.log(e);
        });
    }
  },
  mounted() {
    this.message = '';
    this.getShoelast(this.$route.params.id);

  }
};
</script>

<style>
.edit-form {
  max-width: 300px;
  margin: auto;
}
</style>
