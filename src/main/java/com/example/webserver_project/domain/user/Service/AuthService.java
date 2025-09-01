package com.example.webserver_project.domain.user.Service;

import com.example.webserver_project.domain.user.Dto.JoinRequestDto;
import com.example.webserver_project.domain.user.Dto.JoinResponseDto;
import com.example.webserver_project.domain.user.Dto.RefreshRequestDto;
import com.example.webserver_project.domain.user.Dto.RefreshResponseDto;
import com.example.webserver_project.domain.user.Entity.User;
import com.example.webserver_project.domain.user.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 진행 함수
    @Transactional
    public JoinResponseDto join(JoinRequestDto userRequestDto) {
        // 1. 회원가입하려는 회원 이메일로 중복확인
        if(userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다!");
        }

        // 2. 중복이 안된 이메일이라면, 해당 User Entity 객체를 생성해서 repository로 save한다,
        User user = User.from(userRequestDto);
        // PasswordEncoder로 비밀번호 인코딩
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword())); // user 객체에 인코딩된 비밀번호 저장
        User savedUser = userRepository.save(user); // save 성공 시, 인자로 넣은 객체와 동일한 데이터를 갖고 있는 객체를 다시 반환함

        // 3. save 성공 시 ResponseDto에 해당 객체의 데이터 담는다.
        // Service는 비즈니스 로직에만 집중한다.
        return JoinResponseDto.of(user);
    }

    // 토큰 재발급 함수
    @Transactional
    public RefreshResponseDto refresh(RefreshRequestDto refreshRequestDto) {


    }

}
