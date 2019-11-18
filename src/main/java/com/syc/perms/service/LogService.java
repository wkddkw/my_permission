package com.syc.perms.service;

import com.syc.perms.pojo.R;
import com.syc.perms.pojo.TbLog;
import com.syc.perms.pojo.UserSearch;

import java.util.Date;

public interface LogService {

    //添加日志
    void saveLog(TbLog log);

    //获取日志列表
    R getLogList(Integer page, Integer limit, UserSearch search);

    //删除指定日期以前的日志
    int deleteLogByDate(Date date);
}
