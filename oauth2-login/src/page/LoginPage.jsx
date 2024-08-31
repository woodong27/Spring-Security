import React from 'react';

const handleKakaoLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/naver'
}

const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}

const LoginPage = () => {
    return (
        <div>
            <h1>Social Login</h1>
            <button onClick={handleKakaoLogin}>
                Kakao
            </button>
            <button onClick={handleGoogleLogin}>
                Google
            </button>
        </div>
    )
}

export default LoginPage;