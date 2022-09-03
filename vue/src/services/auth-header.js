export default function authHeader() {
  let user = JSON.parse(localStorage.getItem('user'));
  // console.log("trenutni user")
  // console.log(user)

  if (user && user.accessToken) {
    return { 
      Authorization: 'Bearer ' + user.accessToken,
      "Content-type": "application/json" }; // for Spring Boot back-end
    // return { 'x-access-token': user.accessToken };       // for Node.js Express back-end
  } else {
    return {};
  }
}
