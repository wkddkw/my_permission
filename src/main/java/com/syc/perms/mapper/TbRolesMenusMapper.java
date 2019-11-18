package com.syc.perms.mapper;

import com.syc.perms.pojo.TbRolesMenus;
import com.syc.perms.pojo.TbRolesMenusExample;
import com.syc.perms.pojo.TbRolesMenusKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbRolesMenusMapper {

    long countByExample(TbRolesMenusExample example);

    int deleteByExample(TbRolesMenusExample example);

    int deleteByPrimaryKey(TbRolesMenusKey key);

    //TODO: 修改了该方法的参数类型
    int insert(TbRolesMenus record);

    int insertSelective(TbRolesMenusKey record);

    List<TbRolesMenusKey> selectByExample(TbRolesMenusExample example);

    int updateByExampleSelective(@Param("record") TbRolesMenusKey record, @Param("example") TbRolesMenusExample example);

    int updateByExample(@Param("record") TbRolesMenusKey record, @Param("example") TbRolesMenusExample example);
}