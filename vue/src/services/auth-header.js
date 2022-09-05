export default function authHeader() {
  let user = JSON.parse(localStorage.getItem('user'));
  // console.log("trenutni user")
  // console.log(user)

  if (user && user.accessToken) {
    return { 
      Authorization: 'Bearer ' + user.accessToken,
      "Content-type": "application/json" }; 
  } else {
    return {};
  }
}
