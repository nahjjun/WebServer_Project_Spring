package com.example.webserver_project.domain.user.Entity;

import com.example.webserver_project.domain.Auth.Dto.request.JoinRequestDto;
import com.example.webserver_project.global.jwt.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "User") // default값은 설정 안하면 Class명이 됨
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 AUTO_INCREMENT와 같은 방식
    private Long id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="password", nullable=false)
    private String password;

    @Column(name="email", length = 50, updatable = false)
    private String email;

    @Enumerated(EnumType.STRING) // Enum 이름 그대로가 DB에 저장되므로, RoleType에서 굳이 따로 변수를 지정해주지 않아도 된다.
    @Column(name = "ROLE", nullable = false)
    private RoleType role;

    public static User from(JoinRequestDto dto){
        return User.builder()
                .name(dto.getName())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .build();
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateName(String name){
        this.name = name;
    }

}
