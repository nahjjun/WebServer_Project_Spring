package com.example.webserver_project.domain.user.Service;


import com.example.webserver_project.domain.user.Dto.CustomUserDetails;
import com.example.webserver_project.domain.user.Dto.JwtUserInfoDto;
import com.example.webserver_project.domain.user.Entity.User;
import com.example.webserver_project.domain.user.Repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Builder
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // username 파라미터를 "email"로 사용한다
    // AuthenticationProvider가 해당 메서드를 호출해 DB 사용자를 로딩함 => UserDetails 반환
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        // 현재 로직에서는 username을 사용자 email로 가정
        String email = username;

        // DB에서 email로 사용자 정보 가져옴
        User user = userRepository.findByEmail(email);

        // 이제 해당 사용자 정보를 CustomUserDetails 객체로 return해줄 것이다.
        // 이때, CustomUserDetails를 생성할 때 사용되는 DTO인 JwtUserInfoDto를 생성한다.
        JwtUserInfoDto dto = JwtUserInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .role(user.getRole())
                .build();

        // JWTUserInfoDto를 사용해서 Security가 이해할 수 있는 형태인 UserDetails로 래핑한다.
        return new CustomUserDetails(dto);
    }




}
