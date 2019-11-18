package com.syc.perms.service;


import com.syc.perms.pojo.*;

import java.util.List;

public interface AdminService {

    //获取角色菜单
    List<Menu> selMenus(TbAdmin admin);

    //根据id得到管理员
    TbAdmin selAdminById(Long id);

    //获取所有角色
    List<TbRoles> selRoles();

    //根据email得到管理员
    TbAdmin getAdminByEmail(String eMail, String username);

    //更新管理员信息
    void updAdmin(TbAdmin admin);

    //登陆
    TbAdmin login(String username, String password);

    void updAdminPws(TbAdmin admin);

    //获取所有角色
    R selRoles(Integer page, Integer limit);

    //获取指定角色权限树
    List<TbMenus> getPermissionTreeData(TbAdmin admin);

    //根据角色名查询角色
    TbRoles getRoleByRoleName(String roleName);

    //添加新角色
    void addRole(TbRoles role, String menuId);

    TbRoles getRole(TbRoles role);

    //更新角色信息
    void updateRole(TbRoles role, String m);

    //删除指定角色
    void deleteRole(Long roleId);

    //批量删除指定角色
    void deleteRoles(String rolesId);

    //分页获取所有管理员
    R getAdmins(Integer page, Integer limit);

    //管理员用户名唯一性校验
    TbAdmin getAdminByUserName(String username);

    //新增管理员
    void addAdmin(TbAdmin admin);

    //删除指定管理员
    void deleteAdminById(Long id);

    //批量删除指定管理员
    void deleteAdmins(String adminStr);

    List<TbMenus> selectMenusByParentId();

    List<TbMenus> checkTitleSameLevel(TbMenus menus);

    TbMenus getMenuById(Long menuId);

    void updateMenu(TbMenus menus);

    TbMenus getMenusById(Long menuId);

    void addMenu(TbMenus menus);

    List<TbMenus> getMenus(Long menuId);

    void deleteMenuById(Long menuId);

}
