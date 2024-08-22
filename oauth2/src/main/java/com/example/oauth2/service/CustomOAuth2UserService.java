package com.example.oauth2.service;

import com.example.oauth2.dto.oauth2.CustomOAuth2User;
import com.example.oauth2.dto.oauth2.GoogleResponse;
import com.example.oauth2.dto.oauth2.NaverResponse;
import com.example.oauth2.dto.oauth2.OAuth2Response;
import com.example.oauth2.entity.User;
import com.example.oauth2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println(oAuth2User);

        OAuth2Response response;
        if (registrationId.equals("naver")) {
            response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String username = String.format("%s %s", response.getProvider(), response.getProviderId());
        User user = userRepository.findByUsername(username)
                .orElse(userRepository.save(User.builder()
                        .username(username)
                        .name(response.getName())
                        .email(response.getEmail())
                        .role("ROLE_USER")
                        .build()));

        return new CustomOAuth2User(user);
    }
}
