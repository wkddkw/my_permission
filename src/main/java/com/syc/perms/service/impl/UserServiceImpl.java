package com.syc.perms.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.syc.perms.mapper.TbUsersMapper;
import com.syc.perms.pojo.R;
import com.syc.perms.pojo.TbUsers;
import com.syc.perms.pojo.TbUsersExample;
import com.syc.perms.pojo.UserSearch;
import com.syc.perms.service.UserService;
import com.syc.perms.util.DateUtil;
import com.syc.perms.util.EmailUtil;
import com.syc.perms.util.GlobalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUsersMapper usersMapper;

    @Override
    public R getUsers(Integer page, Integer limit, UserSearch search) {
        PageHelper.startPage(page, limit);

        TbUsersExample example = new TbUsersExample();
        //设置按创建时间降序排序
        example.setOrderByClause("create_time DESC");
        TbUsersExample.Criteria criteria = example.createCriteria();

        if (search.getNickname() != null && !"".equals(search.getNickname())) {
            //注意：模糊查询需要进行拼接”%“  如下，不进行拼接是不能完成查询的哦。
            criteria.andNicknameLike("%" + search.getNickname() + "%");
        }
        if (search.getSex() != null && !"-1".equals(search.getSex())) {
            criteria.andSexEqualTo(search.getSex());
        }
        if (search.getStatus() != null && !"-1".equals(search.getStatus())) {
            criteria.andStatusEqualTo(search.getStatus());
        }
        if (search.getCreateTimeStart() != null && !"".equals(search.getCreateTimeStart())) {
            criteria.andCreateTimeGreaterThanOrEqualTo(DateUtil.getDateByString(search.getCreateTimeStart()));
        }
        if (search.getCreateTimeEnd() != null && !"".equals(search.getCreateTimeEnd())) {
            criteria.andCreateTimeLessThanOrEqualTo(DateUtil.getDateByString(search.getCreateTimeEnd()));
        }

        List<TbUsers> users = usersMapper.selectByExample(example);
        PageInfo<TbUsers> pageInfo = new PageInfo<>(users);

        R result = new R();
        result.setCode(0);
        result.setCount(pageInfo.getTotal());
        result.setData(pageInfo.getList());
        return result;
    }

    @Override
    public TbUsers getUserByEmail(String eMail, Long uid) {
        TbUsersExample example = new TbUsersExample();
        TbUsersExample.Criteria criteria = example.createCriteria();
        criteria.andEMailEqualTo(eMail);
        if (uid != null) {
            criteria.andUidNotEqualTo(uid);
        }
        List<TbUsers> users = usersMapper.selectByExample(example);
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public TbUsers getUserByNickname(String nickname, Long uid) {
        TbUsersExample example = new TbUsersExample();
        TbUsersExample.Criteria criteria = example.createCriteria();
        criteria.andNicknameEqualTo(nickname);
        if (uid != null) {
            criteria.andUidNotEqualTo(uid);
        }
        List<TbUsers> users = usersMapper.selectByExample(example);
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public void addUserService(TbUsers user) throws Exception {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        String code = DateUtil.getStrUUID();
        user.setECode(code);
        Date date = new Date();
        user.setCreateTime(date);
        if (Boolean.parseBoolean(GlobalUtil.getValue("send.email"))) {
            user.setStatus(String.valueOf(0));//0:未激活，1，正常，2，禁用
            EmailUtil.sendMail(user.getEMail(), code);
        } else {
            user.setStatus(String.valueOf(1));//0:未激活，1，正常，2，禁用
        }
        usersMapper.insert(user);
    }

    @Override
    public TbUsers getUserByUid(Long uid) {
        return usersMapper.selectByPrimaryKey(uid);
    }

    @Override
    public void updateUserService(TbUsers user) {
        TbUsers u = usersMapper.selectByPrimaryKey(user.getUid());
        user.setPassword(u.getPassword());
        user.setECode(u.getECode());
        user.setCreateTime(u.getCreateTime());
        usersMapper.updateByPrimaryKey(user);
    }

    @Override
    public void deleteUser(String uid) {
        usersMapper.deleteByPrimaryKey(Long.parseLong(uid));
    }

    @Override
    public void deleteUsers(String userStr) {
        String[] users = userStr.split(",");
        if(users.length > 0){
            for (String uid : users) {
                usersMapper.deleteByPrimaryKey(Long.parseLong(uid));
            }
        }
    }

}
