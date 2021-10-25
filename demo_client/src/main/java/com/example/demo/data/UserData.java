package com.example.demo.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


/**
 * ユーザー情報 Entity
 */
@Entity
@Data
@Table(name = "user_info")
public class UserData {

    /**
     * ID
     */
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    /**
     * 名前
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * E-Mail
     */
    @Column(name = "email")
    private String email;
    
}