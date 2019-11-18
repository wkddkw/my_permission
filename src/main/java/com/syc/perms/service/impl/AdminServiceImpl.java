package com.syc.perms.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.syc.perms.mapper.*;
import com.syc.perms.pojo.*;
import com.syc.perms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private TbRolesMenusMapper tbRolesMenusMapper;

    @Autowired
    private AdminMenusMapper adminMenusMapper;

    @Autowired
    private TbAdminMapper tbAdminMapper;

    @Autowired
    private TbRolesMapper tbRolesMapper;

    @Autowired
    private TbMenusMapper tbMenusMapper;

    @Override
    public List<Menu> selMenus(TbAdmin admin) {
        List<Menu> results = new ArrayList<>();
        Long roleId = admin.getRoleId();
        TbRolesMenusExample example = new TbRolesMenusExample();
        TbRolesMenusExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(roleId);
        List<TbRolesMenusKey> list = tbRolesMenusMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            List<TbMenus> menus = adminMenusMapper.getMenus(roleId);
            for (int i = 0; i < menus.size(); i++) {
                if (menus.get(i).getParentId() == 0) {
                    Menu menu = new Menu();
                    menu.setTitle(menus.get(i).getTitle());
                    menu.setIcon(menus.get(i).getIcon());
                    menu.setHref(menus.get(i).getHref());
                    menu.setSpread(menus.get(i).getSpread());
                    List<Menu> menus2 = new ArrayList<>();
                    for (int j = 0; j < menus.size(); j++) {
                        if (menus.get(j).getParentId() == menus.get(i).getMenuId()) {
                            Menu menu2 = new Menu();
                            menu2.setTitle(menus.get(j).getTitle());
                            menu2.setIcon(menus.get(j).getIcon());
                            menu2.setHref(menus.get(j).getHref());
                            menu2.setSpread(menus.get(j).getSpread());
                            menus2.add(menu2);
                        }
                    }
                    menu.setChildren(menus2);
                    results.add(menu);
                }
            }
        }
        return results;
    }

    @Override
    public TbAdmin selAdminById(Long id) {
        TbAdmin admin = tbAdminMapper.selectByPrimaryKey(id);
        //为了安全，密码置空
        admin.setPassword("");
        return admin;
    }

    @Override
    public List<TbRoles> selRoles() {
        TbRolesExample example = new TbRolesExample();
        return tbRolesMapper.selectByExample(example);
    }

    @Override
    public TbAdmin getAdminByEmail(String eMail, String username) {
        TbAdminExample example = new TbAdminExample();
        TbAdminExample.Criteria criteria = example.createCriteria();
        criteria.andEMailEqualTo(eMail);
        if (username != null && !"".equals(username)) {
            criteria.andUsernameNotEqualTo(username);
        }
        List<TbAdmin> admins = tbAdminMapper.selectByExample(example);
        if (admins != null && admins.size() > 0) {
            return admins.get(0);
        }
        return null;
    }

    @Override
    public void updAdmin(TbAdmin admin) {
        TbAdmin a = tbAdminMapper.selectByPrimaryKey(admin.getId());
        admin.setPassword(a.getPassword());
        tbAdminMapper.updateByPrimaryKey(admin);
    }

    @Override
    public TbAdmin login(String username, String password) {
        //对密码加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        TbAdminExample example = new TbAdminExample();
        TbAdminExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(password);
        List<TbAdmin> admin = tbAdminMapper.selectByExample(example);
        if (admin != null && admin.size() > 0) {
            return admin.get(0);
        }
        return null;
    }

    @Override
    public void updAdminPws(TbAdmin admin) {
        admin.setPassword(DigestUtils.md5DigestAsHex(admin.getPassword().getBytes()));
        tbAdminMapper.updateByPrimaryKey(admin);
    }

    @Override
    public R selRoles(Integer page, Integer limit) {
        //分页设置
        PageHelper.startPage(page, limit);

        TbRolesExample example = new TbRolesExample();
        List<TbRoles> list = tbRolesMapper.selectByExample(example);
        PageInfo<TbRoles> pageInfo = new PageInfo<>(list);
        R resultUtil = new R();
        resultUtil.setCode(0);
        resultUtil.setCount(pageInfo.getTotal());
        resultUtil.setData(pageInfo.getList());
        return resultUtil;
    }

    @Override
    public List<TbMenus> getPermissionTreeData(TbAdmin admin) {
        TbMenusExample example = new TbMenusExample();
        List<TbMenus> allMenus = tbMenusMapper.selectByExample(example);
        Long roleId = admin.getRoleId();
        if (!roleId.equals(Long.valueOf("-1"))) {
            TbRolesMenusExample rolesMenusExample = new TbRolesMenusExample();
            TbRolesMenusExample.Criteria criteria = rolesMenusExample.createCriteria();
            criteria.andRoleIdEqualTo(roleId);
            List<TbRolesMenusKey> roleMenus = tbRolesMenusMapper.selectByExample(rolesMenusExample);
            for (TbMenus m : allMenus) {
                for (TbRolesMenusKey tbMenus : roleMenus) {
                    if (tbMenus.getMenuId().equals(m.getMenuId())) {
                        m.setChecked("true");
                    }
                }
            }
        }
        return allMenus;
    }

    @Override
    public TbRoles getRoleByRoleName(String roleName) {
        TbRolesExample example = new TbRolesExample();
        TbRolesExample.Criteria criteria = example.createCriteria();
        criteria.andRoleNameEqualTo(roleName);
        List<TbRoles> roles = tbRolesMapper.selectByExample(example);
        if (roles != null && roles.size() > 0) {
            return roles.get(0);
        }
        return null;
    }

    @Override
    public void addRole(TbRoles role, String menu) {
        // 维护角色表
        tbRolesMapper.insert(role);
        // 维护角色-菜单表
        if (menu != null && menu.length() != 0) {
            String[] result = menu.split(",");
            // 重新赋予权限
            if (result.length > 0) {
                for (String aResult : result) {
                    TbRolesMenus record = new TbRolesMenus();
                    record.setMenuId(Long.parseLong(aResult));
                    record.setRoleId(role.getRoleId());
                    // 维护角色—菜单表
                    tbRolesMenusMapper.insert(record);
                }
            }
        }
    }

    @Override
    public TbRoles getRole(TbRoles role) {
        return tbRolesMapper.selectByPrimaryKey(role.getRoleId());
    }

    @Override
    public void updateRole(TbRoles role, String m) {
        // 更新角色信息
        tbRolesMapper.updateByPrimaryKey(role);
        // 先删除角色所有权限
        TbRolesMenusExample example = new TbRolesMenusExample();
        TbRolesMenusExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(role.getRoleId());
        tbRolesMenusMapper.deleteByExample(example);
        // 维护角色-菜单表
        if (m != null && m.length() != 0) {
            String[] result = m.split(",");
            // 重新赋予权限
            if (result.length > 0) {
                for (String aResult : result) {
                    TbRolesMenus record = new TbRolesMenus();
                    record.setMenuId(Long.parseLong(aResult));
                    record.setRoleId(role.getRoleId());
                    // 维护角色—菜单表
                    tbRolesMenusMapper.insert(record);
                }
            }
        }
    }

    @Override
    public void deleteRole(Long roleId) {
        tbRolesMapper.deleteByPrimaryKey(roleId);
    }

    @Override
    public void deleteRoles(String rolesId) {
        String[] rids = rolesId.split(",");
        for (String id : rids) {
            tbRolesMapper.deleteByPrimaryKey(Long.parseLong(id));
        }
    }

    @Override
    public R getAdmins(Integer page, Integer limit) {

        PageHelper.startPage(page, limit);

        TbAdminExample example = new TbAdminExample();
        List<TbAdmin> list = tbAdminMapper.selectByExample(example);
        // 将roleName写进TbAdmin
        for (TbAdmin tbAdmin : list) {
            // tbAdmin.setRoleName();
            List<TbRoles> roles = selRoles();
            for (TbRoles tbRole : roles) {
                if (tbRole.getRoleId().equals(tbAdmin.getRoleId())) {
                    tbAdmin.setRoleName(tbRole.getRoleName());
                }
            }
        }

        PageInfo<TbAdmin> pageInfo = new PageInfo<>(list);
        R resultUtil = new R();
        resultUtil.setCode(0);
        resultUtil.setCount(pageInfo.getTotal());
        resultUtil.setData(pageInfo.getList());
        return resultUtil;
    }

    @Override
    public TbAdmin getAdminByUserName(String username) {
        TbAdminExample example = new TbAdminExample();
        TbAdminExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbAdmin> admins = tbAdminMapper.selectByExample(example);
        if (admins != null && admins.size() > 0) {
            return admins.get(0);
        }
        return null;
    }

    @Override
    public void addAdmin(TbAdmin admin) {
        //对密码md5加密
        admin.setPassword(DigestUtils.md5DigestAsHex(admin.getPassword().getBytes()));
        tbAdminMapper.insert(admin);
    }

    @Override
    public void deleteAdminById(Long id) {
        tbAdminMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteAdmins(String adminStr) {
        String[] adminIds = adminStr.split(",");
        if (adminIds.length > 0) {
            for (String str : adminIds) {
                tbAdminMapper.deleteByPrimaryKey(Long.parseLong(str));
            }
        }
    }

    @Override
    public List<TbMenus> selectMenusByParentId() {
        TbMenusExample example = new TbMenusExample();
        example.setOrderByClause("sorting DESC");
        return tbMenusMapper.selectByExample(example);
    }

    @Override
    public List<TbMenus> checkTitleSameLevel(TbMenus menus) {
        TbMenusExample example = new TbMenusExample();
        TbMenusExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(menus.getParentId());
        criteria.andTitleEqualTo(menus.getTitle());
        return tbMenusMapper.selectByExample(example);
    }

    @Override
    public TbMenus getMenuById(Long menuId) {
        return tbMenusMapper.selectByPrimaryKey(menuId);
    }

    @Override
    public void updateMenu(TbMenus menus) {
        tbMenusMapper.updateByPrimaryKey(menus);
    }

    @Override
    public TbMenus getMenusById(Long menuId) {
        TbMenusExample example = new TbMenusExample();
        TbMenusExample.Criteria criteria = example.createCriteria();
        criteria.andMenuIdEqualTo(menuId);
        List<TbMenus> data = tbMenusMapper.selectByExample(example);
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    @Override
    public void addMenu(TbMenus menus) {
        tbMenusMapper.insert(menus);
    }

    @Override
    public List<TbMenus> getMenus(Long menuId) {
        TbMenusExample example = new TbMenusExample();
        TbMenusExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(menuId);
        return tbMenusMapper.selectByExample(example);
    }

    @Override
    public void deleteMenuById(Long menuId) {
        tbMenusMapper.deleteByPrimaryKey(menuId);
    }

}
