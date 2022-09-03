import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/test';


class CommentDataService {

  static prepareHttpClient(){
    return axios.create({
      baseURL: API_URL,
      headers: authHeader()
    })
  } 


  getAll(params) {
    const http = CommentDataService.prepareHttpClient()
    return http.get("/comments", { params });
  }

  create(data) {
    const http = CommentDataService.prepareHttpClient()
    return http.post("/comments", data);
  }

  delete(id) {
    const http = CommentDataService.prepareHttpClient()
    return http.delete(`/comments/${id}`);
  }



}


export default new CommentDataService();