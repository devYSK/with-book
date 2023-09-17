package com.ys.practice.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "CT_USERS")
@RequiredArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "Firstname can't be empty")
    private String firstName;
    @NotEmpty(message = "Lastname can't be empty")
    private String lastName;
    @NotEmpty(message = "Username can't be empty")
    private String username;
    @NotEmpty(message = "Email can't be empty")
    private String email;
    @NotEmpty(message = "Password can't be empty")
    private String password;

    @NotNull
    private boolean totpEnabled;

    private boolean verified;
}
