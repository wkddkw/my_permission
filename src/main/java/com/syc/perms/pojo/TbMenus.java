package com.syc.perms.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TbMenus implements Serializable {

    private Long menuId;

    private String title;

    private String icon;

    private String href;

    private String perms;

    private String spread;

    private Long parentId;

    private Long sorting;

    private String checked;

    private Boolean isOpen=false;

}