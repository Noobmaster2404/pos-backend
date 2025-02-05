package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.increff.server.model.Role;

@Entity
@Getter
@Setter
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = {"email"})
)
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_generator")
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}