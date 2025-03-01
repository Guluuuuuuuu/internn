package com.gulu.internship07.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")  // 映射到数据库中的 admin表
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 主键

    @Column(nullable = false, unique = true)  // 确保用户名是唯一且不能为空
    private String username;  // 用户名

    @Column(nullable = false)  // 密码不能为空
    private String password;  // 密码

    @Column(nullable = false)  // 姓名不能为空
    private String name;  // 姓名

    @Column  // 头像可以为空
    private String image;  // 头像

    // 获取 id
    public Long getId() {
        return id;
    }

    // 设置 id
    public void setId(Long id) {
        this.id = id;
    }

    // 获取 username
    public String getUsername() {
        return username;
    }

    // 设置 username
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取 password
    public String getPassword() {
        return password;
    }

    // 设置 password
    public void setPassword(String password) {
        this.password = password;
    }

    // 获取 name
    public String getName() {
        return name;
    }

    // 设置 name
    public void setName(String name) {
        this.name = name;
    }

    // 获取 image
    public String getImage() {
        return image;
    }

    // 设置 image
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Admin{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", password='" + password + '\'' +
               ", name='" + name + '\'' +
               ", image='" + image + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Admin admin = (Admin) obj;
        return id.equals(admin.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}