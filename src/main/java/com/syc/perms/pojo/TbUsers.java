package com.syc.perms.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class TbUsers {

    private Long uid;

    private String eMail;

    private String nickname;

    private String password;

    private String sex;

    //与前端页码传递的数据格式不一致,导致400异常
    //private Date birthday;
    private String birthday;

    private String address;

    private String phone;

    private String eCode;

    private String status;

    private Date createTime;

}