package com.aaron.cas.model;

import java.io.Serializable;

/**
 * @author Aaron
 * @description 用户信息实体
 * @date 2020/9/16
 */
public class UserDto implements Serializable {
    private static final long serialVersionUID = 4200323231412431233L;
    private Integer uid;
    private String username;
    private String password;
    private String name;
    private String id_card_num;
    private Integer state;

    public UserDto() {
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId_card_num() {
        return id_card_num;
    }

    public void setId_card_num(String id_card_num) {
        this.id_card_num = id_card_num;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
