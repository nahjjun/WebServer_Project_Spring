package com.example.webserver_project.domain.user.Service;

import com.example.webserver_project.domain.user.Dto.*;
import com.example.webserver_project.domain.user.Entity.User;
import com.example.webserver_project.domain.user.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 진행 함수
    @Transactional
    public UserResponseDto register(JoinRequestDto userRequestDto) {
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
        return UserResponseDto.of(user);
    }



    // 회원 탈퇴 함수
    @Transactional // 이 어노테이션을 붙여주지 않으면 오류 발생함
    public void delete(DeleteRequestDto deleteRequest) {

        if(!userRepository.existsByEmail(deleteRequest.getEmail())) {
            throw new IllegalArgumentException("이메일이 옳지 않습니다!");
        }
        if(!userRepository.existsByEmailAndPassword(deleteRequest.getEmail(), deleteRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 옳지 않습니다!");
        }
        // 해당 사용자의 이름이 틀렸는지 확인
        if(!userRepository.existsByNameAndEmailAndPassword(deleteRequest.getName(), deleteRequest.getEmail(), deleteRequest.getPassword())) {
            throw new IllegalArgumentException("이름이 옳지 않습니다!");
        }
        // 전부 맞은 경우, 해당 사용자를 삭제한다.
        userRepository.deleteByEmail(deleteRequest.getEmail());
    }
}
