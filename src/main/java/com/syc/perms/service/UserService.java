package com.syc.perms.service;

import com.syc.perms.pojo.R;
import com.syc.perms.pojo.TbUsers;
import com.syc.perms.pojo.UserSearch;

public interface UserService {

    //得到用户信息
    R getUsers(Integer page, Integer limit, UserSearch search);

    //用户邮箱唯一性检验
    TbUsers getUserByEmail(String eMail, Long uid);

    //用户昵称唯一性检验
    TbUsers getUserByNickname(String nickname, Long uid);

    //增加用户
    void addUserService(TbUsers user) throws Exception;

    //查询用户
    TbUsers getUserByUid(Long uid);

    //更新用户信息
    void updateUserService(TbUsers user);

    //删除指定用户
    void deleteUser(String uid);

    //批量删除用户
    void deleteUsers(String userStr);

}
