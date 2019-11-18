package com.syc.perms.service;

import com.syc.perms.pojo.TbUsers;

import java.util.List;

public interface MainService {

    int seUserCountBygender(int i);

    List<TbUsers> selUserList();

    List<TbUsers> selUsersToday();

    List<TbUsers> selUsersYesterday();

    List<TbUsers> selUsersYearWeek();

    List<TbUsers> selUsersMonth();

}
