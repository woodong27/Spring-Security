import React from "react";
import axios from "axios";

const handleNaverLogin = () => {
  window.location.href = "http://localhost:8080/oauth2/authorization/naver";
};

const handleGoogleLogin = () => {
  window.location.href = "http://localhost:8080/oauth2/authorization/google";
};

const handleMainPage = () => {
  axios
    .get("http://localhost:8080/api/main", { withCredentials: true })
    .then((res) => {
      alert(JSON.stringify(res.data));
    })
    .catch((e) => {
      console.log(e.response.data);
      alert(e.response.data.message);
    });
};

const LoginPage = () => {
  return (
    <div>
      <h1>Social Login</h1>
      <button onClick={handleNaverLogin}>Naver</button>
      <button onClick={handleGoogleLogin}>Google</button>
      <button onClick={handleMainPage}>Main Page</button>
    </div>
  );
};

export default LoginPage;
