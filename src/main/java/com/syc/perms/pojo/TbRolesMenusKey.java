package com.syc.perms.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TbRolesMenusKey implements Serializable {

    private Long menuId;

    private Long roleId;

}