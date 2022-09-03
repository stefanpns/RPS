<template>
  <div class="submit-form">
    <div v-if="!submitted">
      <div class="form-group">
        <label for="title">Title</label>
        <input
          type="text"
          class="form-control"
          id="title"
          required
          v-model="shoelast.title"
          name="title"
        />
      </div>

      <div class="form-group">
        <label for="description">Description</label>
        <input
          class="form-control"
          id="description"
          required
          v-model="shoelast.description"
          name="description"
        />
      </div>

      
      <div class="form-group">
        <label for="shoesize">Shoe size</label>
        <input
          class="form-control"
          id="shoesize"
          required
          v-model="shoelast.shoesize"
          name="shoesize"
        />
      </div>
      
      <div class="form-group">
        <label><strong>Picture:</strong></label>

        <input type="file" class="form-control"  id="file" ref="myFiles" @change="previewFiles" >

      </div>



      <button @click="saveShoelast" class="btn btn-success">Submit</button>
    </div>

    <div v-else>
      <h4>You submitted successfully!</h4>
      <button class="btn btn-success" @click="newShoelast">Add more</button>
      
      <button class="btn btn-success" @click="listAll">List all</button>
    </div>
  </div>
</template>

<script>
import ShoelastDataService from "../../services/ShoelastDataService";

export default {
  name: "add-shoelast",
  data() {
    return {
      shoelast: {
        id: null,
        title: "",
        description: "",
        shoesize: null
      },
      submitted: false,
      image: null
    };
  },
  methods: {
    saveShoelast() {
      var data = {
        "shoelast": {
          title: this.shoelast.title,
          description: this.shoelast.description,
          shoesize: this.shoelast.shoesize
        },
        "image": this.image
      };


      // console.log("saving the shoelast, data:")
      // console.log(data)
      
      let formData = new FormData();
      formData.append('title',data.shoelast.title);
      formData.append('description',data.shoelast.description);
      formData.append('shoesize',data.shoelast.shoesize); 
      formData.append('file', data.image);
      // console.log("formData")
      // console.log(formData.keys().next())

      ShoelastDataService.create(formData)
        .then(response => {
          //this.shoelast.id = response.data.id;
          //console.log(response.data);
          this.submitted = true;
          
          console.log("F response")
          console.log(response)
        })
        .catch(error => {
          //console.log(e);
          
          
          
          console.log("S response.message")
          console.log(error)
        });
    },
    
    newShoelast() {
      this.submitted = false;
      this.shoelast = {};
    },
    
    listAll() {
      this.$router.push('/shoelasts');
    },
    
    
    previewFiles() {
      this.files = this.$refs.myFiles.files
      this.image = this.files[0]
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
