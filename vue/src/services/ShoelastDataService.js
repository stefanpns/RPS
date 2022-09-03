import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/test';


class ShoelastDataService {

  static prepareHttpClient(hc){
    var standardHeader = authHeader()

    if (hc != null) {
      const array = hc;
      for(let index = 0; index < array.length; index++) {
        const element = array[index];
        const headerKey = element[0]
        const headerValue = element[1]
        standardHeader[headerKey] = headerValue
      }
    }

    console.log("standardHeader:")
    console.log(standardHeader)
    return axios.create({
      baseURL: API_URL,
      headers: standardHeader
    })
  } 


  getAll(params) {
    const http = ShoelastDataService.prepareHttpClient()
    return http.get("/shoelasts", { params });
  }

  get(id) {
    const http = ShoelastDataService.prepareHttpClient()
    return http.get(`/shoelasts/${id}`);
  }

  create(data) {

    const hc = [ ['Content-type', 'multipart/form-data'] ]

    const http = ShoelastDataService.prepareHttpClient(hc)
    return http.post("/shoelasts", data);
  }

  update(id, data) {
    const http = ShoelastDataService.prepareHttpClient()
    return http.put(`/shoelasts/${id}`, data);
  }

  delete(id) {
    const http = ShoelastDataService.prepareHttpClient()
    return http.delete(`/shoelasts/${id}`);
  }

  deleteAll() {
    const http = ShoelastDataService.prepareHttpClient()
    return http.delete(`/shoelasts`);
  }

  getImage(id) {

    let user = JSON.parse(localStorage.getItem('user'));
    
    if (user && user.accessToken) {
    } else {
      console.log("NEMA GA USER")
    }

    
    var standardHeader = authHeader()

    console.log("THE IMAGE IS PENDING")
    console.log("")
    return axios({
      url: API_URL + '/getimage/' + id,
      method: 'GET',
      responseType: 'blob',
      headers: standardHeader
        });


  }

}


export default new ShoelastDataService();