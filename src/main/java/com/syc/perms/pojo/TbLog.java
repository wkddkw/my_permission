package com.syc.perms.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class TbLog {

    private Long id;

    private String username;

    private String operation;

    private String method;

    private String params;

    private String ip;

    private Date createTime;

}