package com.example.webserver_project.domain.user.Entity;

import com.example.webserver_project.domain.user.Dto.JoinRequestDto;
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

    @Column(name="name")
    private String name;
    private String password;
    private String email;

    public static User from(JoinRequestDto dto){
        return User.builder()
                .name(dto.getName())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .build();
    }
}
