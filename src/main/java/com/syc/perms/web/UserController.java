package com.syc.perms.web;

import com.syc.perms.annotation.SysLog;
import com.syc.perms.pojo.R;
import com.syc.perms.pojo.TbUsers;
import com.syc.perms.pojo.UserSearch;
import com.syc.perms.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userServiceImpl;

    @RequestMapping(value = "/userList", method = RequestMethod.GET)
    @RequiresPermissions("user:user:list")
    public String userList() {
        return "page/user/userList";
    }

    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    @RequiresPermissions("user:user:list")
    @ResponseBody
    public R getUserList(Integer page, Integer limit, UserSearch search) {
        return userServiceImpl.getUsers(page, limit, search);
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.GET)
    @RequiresPermissions("user:user:save")
    public String userAdd() {
        return "page/user/addUser";
    }

    @RequestMapping("checkUserByEmail")
    @ResponseBody
    public R checkUserEmail(String eMail, Long uid) {
        TbUsers user = userServiceImpl.getUserByEmail(eMail, uid);
        if (user != null) {
            return new R(500, "邮箱已存在，请重新填写！");
        }
        return new R(0);
    }

    @RequestMapping("checkUserByNickname/{nickname}")
    @ResponseBody
    public R checkNickname(@PathVariable("nickname") String nickname, Long uid) {
        TbUsers user = userServiceImpl.getUserByNickname(nickname, uid);
        if (user != null) {
            return new R(501, "昵称已存在，请重新填写！");
        }
        return new R(0);
    }

    /**
     * 添加用户
     */
    //@SysLog(value="添加用户")
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @RequiresPermissions("user:user:save")
    @ResponseBody
    public R addUser(TbUsers user) {

        if (user.getEMail() != null) {
            //防止浏览器提交
            TbUsers tbUsers = userServiceImpl.getUserByEmail(user.getEMail(), null);
            if (tbUsers != null) {
                return new R(500, "邮箱已存在,请重新填写！");
            }
        }

        if (user.getNickname() != null) {
            TbUsers tbUsers = userServiceImpl.getUserByNickname(user.getNickname(), null);
            if (tbUsers != null) {
                return new R(501, "昵称已存在,请重新填写！");
            }
        }

        try {
            //TbUsers tbUsers=new TbUsers();
            //BeanUtils.copyProperties(user,tbUsers);
            userServiceImpl.addUserService(user);
            return R.ok();
        } catch (Exception e) {
            return new R(502, "邮件发送错误，请检查邮箱！");
        }
    }

    @RequestMapping(value = "/editUser/{uid}", method = RequestMethod.GET)
    @RequiresPermissions("user:user:save")
    public String editUser(@PathVariable("uid") String uid, Model model) {
        TbUsers user = userServiceImpl.getUserByUid(Long.parseLong(uid));
        model.addAttribute("user", user);
        return "page/user/editUser";
    }

    /**
     * 更新用户信息
     */
    @SysLog(value="更新用户信息")
    @RequestMapping(value = "/updUser", method = RequestMethod.POST)
    @RequiresPermissions("user:user:update")
    @ResponseBody
    public R updUser(TbUsers user) {
        try {
            userServiceImpl.updateUserService(user);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }

    /**
     * 根据ID删除用户
     */
    @SysLog(value="根据ID删除用户")
    @RequestMapping("/delUserByUid/{uid}")
    @RequiresPermissions("user:user:delete")
    @ResponseBody
    public R delUserByUid(@PathVariable("uid") String uid) {
        try {
            userServiceImpl.deleteUser(uid);
            ;
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }

    /**
     * 批量删除用户
     */
    @SysLog(value="批量删除用户")
    @RequestMapping("/delUsers/{userStr}")
    @RequiresPermissions("user:user:delete")
    @ResponseBody
    public R delUsers(@PathVariable("userStr") String userStr) {
        try {
            userServiceImpl.deleteUsers(userStr);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }

}
