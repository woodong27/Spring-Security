# Spring Security

Spring Security 연습(Jwt 발급 및 OAuth2 인증)

## Index

[1. Spring Security - JWT](./jwt)  
[2. Spring Security - OAuth2 + JWT](./oauth2)

## JWT 심화

jwt 프로젝트를 활용해서 진행

JWT는 많은 요청을 위해 Http 통신으로 서버에 전달되며, 이 과정에서 탈취될 수 있음  
-> 탈취되는 경우를 방지하기 위한 여러 방식이 존재  

### 다중 토큰(Access, Refresh)

* Access : 약 10분 주기의 짧은 생명주기  
* Refresh : Access가 만료되었을 때 재발급을 위한 용도로 24시간 이상의 긴 생명주기  

Access 토큰의 권한이 알맞은데 만료된 경우 Refresh토큰으로 Access 토큰을 재발급

1. 로그인이 완료되면 successHandler에서 Access/Refresh 토큰을 발급    
    (각 토큰은 다른 payload와 생명주기를 가져야 함)  
2. JwtFilter에서 Access가 만료된 경우 FE와 합의된 상태 코드와 메시지를 전달  
3. FE에서 Access 만료된 것을 확인하면 예외처리를 통해 Refresh 토큰을 서버측으로 전송하고 Access 토큰을 재발급
4. 서버측에서는 Refresh를 받을 엔드포인트를 구현하여 검증 후 Access를 발급

Access는 생명주기가 짧아 탈취되더라도 비교적 안전하지만, Refresh는 위험함  
-> 각 토큰마다 다른 저장소를 설정하고, Access를 재발급 하는 경우 Refresh도 함께 재발급(기존 토큰은 삭제)  

* 로컬 스토리지 : XSS에 취약, Access 토큰 저장
* 쿠키 : CSRF에 취약, Refresh 저장  
(위 조건들은 꼭 따라야 하는 것은 아님 -> 개인 판단에 따라서 결정)

만약 로그아웃을 하는 경우 서버는 해당 정보를 알 수가 없음(stateless)  
-> Refresh를 서버측에도 저장하여 FE에서 로그아웃 하는 경우 토큰을 삭제하여 위 경우를 방지

네이버 로그인처럼 신규 IP나 장치에서 로그인하는 경우 메일 등의 알림을 보내줌  
이때 '아니요'를 누르면 서버의 토큰 저장소에서 Refresh 토큰을 삭제하여 이후의 인증을 막을 수 있음
