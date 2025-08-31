package com.example.webserver_project.domain.user.Repository;
import com.example.webserver_project.domain.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 만약 필요하다면 추가 쿼리 메소드 정의하기

    User findByEmail(String email);
    boolean existsByEmail(String email); // 이메일로 중복 확인하는 함수

    long deleteByEmail(String email);

}
