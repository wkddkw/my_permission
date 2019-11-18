package com.syc.perms.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TbAdmin implements Serializable {

    private Long id;

    private String username;

    private String password;

    private String salt;

    private String fullname;

    private String eMail;

    private String sex;

    private String birthday;

    private String address;

    private String phone;

    private Long roleId;

    private String roleName;

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail == null ? null : eMail.trim();
    }

}