package com.syc.perms.web;

import com.syc.perms.pojo.R;
import com.syc.perms.pojo.TbAdmin;
import com.syc.perms.pojo.TbMenus;
import com.syc.perms.pojo.TbRoles;
import com.syc.perms.service.AdminService;
import com.syc.perms.util.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("/sys")
public class AdminController {

    @Autowired
    private AdminService adminServiceImpl;

    @RequestMapping("/roleList")
    @RequiresPermissions("sys:role:list")
    public String roleList() {
        return "page/admin/roleList";
    }

    /**
     * 管理员列表
     */
    @RequestMapping("/getRoleList")
    @RequiresPermissions("sys:role:list")
    @ResponseBody
    public R getRoleList(Integer page, Integer limit) {
        return adminServiceImpl.selRoles(page, limit);
    }

    @RequestMapping(value = "/addRole", method = RequestMethod.GET)
    @RequiresPermissions("sys:role:save")
    public String addRole() {
        return "page/admin/addRole";
    }

    /**
     * 得到指定角色权限树
     */
    @RequestMapping(value = "/getRolePermissionTree", produces = {"text/json;charset=UTF-8"})
    @ResponseBody
    public String getPermissionTreeData(@RequestParam(value = "roleId", defaultValue = "-1") Long roleId) {
        TbAdmin admin = new TbAdmin();
        admin.setRoleId(roleId);
        System.out.println(JsonUtils.objectToJson(adminServiceImpl.getPermissionTreeData(admin)));
        return JsonUtils.objectToJson(adminServiceImpl.getPermissionTreeData(admin));
    }

    /**
     * 角色名唯一性检查
     */
    @RequestMapping("/checkRoleName/{roleName}")
    @ResponseBody
    public R checkRoleName(Long roleId, @PathVariable("roleName") String roleName) {
        TbRoles role = adminServiceImpl.getRoleByRoleName(roleName);
        if (role == null) {
            return new R(0);
        } else if (role.getRoleId().equals(roleId)) {
            return new R(0);
        } else {
            return new R(500, "角色名已存在！");
        }
    }

    /**
     * 添加新角色
     */
    //@SysLog(value="添加角色信息")
    @RequestMapping(value = "/addRole", method = RequestMethod.POST)
    @RequiresPermissions("sys:role:save")
    @ResponseBody
    public R addRole(TbRoles role, @RequestParam("m") String menuId) {
        TbRoles roles = adminServiceImpl.getRoleByRoleName(role.getRoleName());
        if (roles != null) {
            return new R(500, "角色名已存在,请重试！");
        }
        //角色信息保存
        adminServiceImpl.addRole(role, menuId);
        return R.ok();
    }

    /**
     * 跳转编辑角色页面
     */
    @RequestMapping("/editRole")
    @RequiresPermissions("sys:role:update")
    public String editRole(TbRoles role, Model model) {
        role = adminServiceImpl.getRole(role);
        model.addAttribute("role", role);
        return "page/admin/editRole";
    }

    /**
     * 更新角色信息
     */
    //@SysLog(value="更新角色信息")
    @RequestMapping("/updRole")
    @RequiresPermissions("sys:role:update")
    @ResponseBody
    public void updRole(TbRoles role, String m) {
        //角色信息保存
        adminServiceImpl.updateRole(role, m);
    }

    /**
     * 删除指定角色信息
     */
    //@SysLog(value="删除指定角色信息")
    @RequestMapping("/delRole/{roleId}")
    @RequiresPermissions("sys:role:delete")
    @ResponseBody
    public R delRole(@PathVariable("roleId") Long roleId) {
        R resultUtil = new R();
        try {
            adminServiceImpl.deleteRole(roleId);
            resultUtil.setCode(0);
        } catch (Exception e) {
            resultUtil.setCode(500);
            e.printStackTrace();
        }
        return resultUtil;
    }

    /**
     * 批量删除指定角色信息
     */
    //@SysLog(value="批量删除指定角色信息")
    @RequestMapping("/delRoles/{rolesId}")
    @RequiresPermissions("sys:role:delete")
    @ResponseBody
    public R delRoles(@PathVariable("rolesId") String rolesId) {
        R resultUtil = new R();
        try {
            adminServiceImpl.deleteRoles(rolesId);
            resultUtil.setCode(0);
        } catch (Exception e) {
            resultUtil.setCode(500);
            e.printStackTrace();
        }
        return resultUtil;
    }

    @RequestMapping("/adminList")
    public String adminList() {
        return "page/admin/adminList";
    }

    /**
     * 管理员列表
     */
    @RequestMapping("/getAdminList")
    @RequiresPermissions("sys:admin:list")
    @ResponseBody
    public R getAdminList(Integer page, Integer limit) {
        return adminServiceImpl.getAdmins(page, limit);
    }

    @RequestMapping(value = "/addAdmin", method = RequestMethod.GET)
    @RequiresPermissions("sys:admin:save")
    public String addAdmin(HttpServletRequest req) {
        List<TbRoles> roles = adminServiceImpl.selRoles();
        req.setAttribute("roles", roles);
        return "page/admin/addAdmin";
    }

    /**
     * 管理员用户名唯一性检查
     */
    @RequestMapping("/checkAdminName/{username}")
    @ResponseBody
    public R checkAdminName(@PathVariable("username") String username) {
        TbAdmin admin = adminServiceImpl.getAdminByUserName(username);
        if (admin != null) {
            return new R(500, "管理员已存在！");
        }
        return new R(0);
    }

    /**
     * 增加管理員
     * 日期类型会导致数据填充失败，请求没反应
     */
    //@SysLog(value="添加管理员")
    @RequestMapping(value = "/addAdmin", method = RequestMethod.POST)
    @RequiresPermissions("sys:admin:save")
    @ResponseBody
    public R addAdmin(TbAdmin admin) {
        //防止浏览器提交
        TbAdmin a = adminServiceImpl.getAdminByUserName(admin.getUsername());
        if (a != null) {
            return new R(500, "用户名已存在,请重试！");
        }
        adminServiceImpl.addAdmin(admin);
        return R.ok();
    }

    @RequestMapping("/editAdmin/{id}")
    @RequiresPermissions("sys:admin:update")
    public String editAdmin(HttpServletRequest req, @PathVariable("id") Long id) {
        TbAdmin ad = adminServiceImpl.selAdminById(id);
        List<TbRoles> roles = adminServiceImpl.selRoles();
        req.setAttribute("ad", ad);
        req.setAttribute("roles", roles);
        return "page/admin/editAdmin";
    }

    /**
     * 更新管理员信息,FIXME:尚有问题!
     */
    @RequestMapping(value = "/updAdmin",method = RequestMethod.POST)
    @RequiresPermissions("sys:admin:update")
    @ResponseBody
    public R updAdmin(TbAdmin admin) {
        if (admin != null && admin.getId() == 1) {
            return R.error("不允许修改!");
        }
        try {
            adminServiceImpl.updAdmin(admin);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }

    /**
     * 通过id删除管理员
     */
    //@SysLog(value="删除指定管理员")
    @RequestMapping("/delAdminById/{id}")
    @RequiresPermissions("sys:admin:delete")
    @ResponseBody
    public R delAdminById(@PathVariable("id")Long id) {
        if(id==1){
            return R.error();
        }
        try {
            adminServiceImpl.deleteAdminById(id);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }

    /**
     * 批量删除指定管理员
     */
    //@SysLog(value="批量删除指定管理员")
    @RequestMapping("/delAdmins/{adminStr}")
    @RequiresPermissions("sys:admin:delete")
    @ResponseBody
    public R delAdmins(HttpServletRequest req,@PathVariable("adminStr")String adminStr) {
        String[] adminIds = adminStr.split(",");
        for (String str : adminIds) {
            TbAdmin admin = (TbAdmin)SecurityUtils.getSubject().getPrincipal();
            if((admin.getId()==Long.parseLong(str))){
                return R.error();
            }
            if("1".equals(str)){
                return R.error();
            }
        }
        try {
            adminServiceImpl.deleteAdmins(adminStr);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }

    //跳转到菜单管理界面
    @RequestMapping("/menuList")
    public String menuList() {
        return "page/admin/menuList";
    }

    /**
     * 获取菜单信息
     */
    @RequestMapping("/menuData")
    @RequiresPermissions("sys:menu:list")
    @ResponseBody
    public R menuData(){
        List<TbMenus> list=adminServiceImpl.selectMenusByParentId();
        R resultUtil=new R();
        resultUtil.setCode(0);
        resultUtil.setCount((long) list.size());
        resultUtil.setData(list);
        return resultUtil;
    }

    @RequestMapping(value = "/saveMenu/{menuId}",method = RequestMethod.GET)
    @RequiresPermissions("sys:menu:save")
    public String saveMenu(@PathVariable("menuId") Long menuId,Model model){
        if(menuId!=null&&menuId!=1){
            TbMenus menus=new TbMenus();
            menus.setMenuId(menuId);
            model.addAttribute("menu",menus);
            model.addAttribute("flag","1");
            return "page/admin/menuForm";
        }else{
            model.addAttribute("msg","不允许操作！");
            return "page/active";
        }
    }

    //@SysLog("维护菜单信息")
    @RequestMapping(value = "/menuForm",method = RequestMethod.POST)
    @RequiresPermissions(value={"sys:menu:save","sys:menu:update"})
    @ResponseBody
    public R menuForm(TbMenus menus,String flag){
        if(StringUtils.isBlank(flag)){
            //同级菜单名不相同
            List<TbMenus> data=adminServiceImpl.checkTitleSameLevel(menus);
            TbMenus m = adminServiceImpl.getMenuById(menus.getMenuId());
            Boolean f=false;
            if(m.getTitle().equals(menus.getTitle())||data.size()==0){
                f=true;
            }
            if(!f||data.size()>1){
                return R.error("同级菜单名不能相同！");
            }
            menus.setSpread("false");
            adminServiceImpl.updateMenu(menus);
            return R.ok("修改成功！");
        }else if(menus.getMenuId()!=1){
            menus.setParentId(menus.getMenuId());

            //规定只能3级菜单
            TbMenus m=adminServiceImpl.getMenusById(menus.getMenuId());
            if(m!=null&&m.getParentId()!=0){
                TbMenus m1=adminServiceImpl.getMenusById(m.getParentId());
                if(m1!=null&&m1.getParentId()!=0){
                    return R.error("此菜单不允许添加子菜单！");
                }
            }

            //同级菜单名不相同
            List<TbMenus> data=adminServiceImpl.checkTitleSameLevel(menus);
            if(data.size()>0){
                return R.error("同级菜单名不能相同！");
            }

            menus.setMenuId(null);
            menus.setSpread("false");
            adminServiceImpl.addMenu(menus);
            return R.ok("添加成功！");
        }else{
            return R.error("此菜单不允许操作！");
        }
    }

    @RequestMapping(value = "/editMenu/{menuId}",method = RequestMethod.GET)
    @RequiresPermissions("sys:menu:update")
    public String editMenu(@PathVariable("menuId") Long menuId,Model model){
        if(menuId!=null&&menuId!=1){
            TbMenus menus=adminServiceImpl.getMenuById(menuId);
            model.addAttribute("menu",menus);
            return "page/admin/menuForm";
        }else if(menuId==1){
            model.addAttribute("msg","不允许操作此菜单！");
            return "page/active";
        }else{
            model.addAttribute("msg","不允许操作！");
            return "page/active";
        }
    }

    //@SysLog(value="删除菜单信息")
    @RequestMapping(value = "/delMenuById/{menuId}",method = RequestMethod.POST)
    @RequiresPermissions("sys:menu:delete")
    @ResponseBody
    public R delMenuById(@PathVariable("menuId")Long menuId) {
        try {
            if(menuId==1){
                return R.error("此菜单不允许删除！");
            }
            //查询是否有子菜单，不允许删除
            List<TbMenus> data=adminServiceImpl.getMenus(menuId);
            if(data!=null&&data.size()>0){
                return R.error("包含子菜单，不允许删除！");
            }
            adminServiceImpl.deleteMenuById(menuId);
            return R.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("系统错误！");
        }
    }

}
