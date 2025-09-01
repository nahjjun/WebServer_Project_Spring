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





    // 회원 탈퇴 함수
    @Transactional // 이 어노테이션을 붙여주지 않으면 오류 발생함
    public void delete(DeleteRequestDto deleteRequest) {


        // 전부 맞은 경우, 해당 사용자를 삭제한다.
        userRepository.deleteByEmail(deleteRequest.getEmail());
    }
}
