package com.syc.perms.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.syc.perms.mapper.TbLogMapper;
import com.syc.perms.pojo.R;
import com.syc.perms.pojo.TbLog;
import com.syc.perms.pojo.TbLogExample;
import com.syc.perms.pojo.UserSearch;
import com.syc.perms.service.LogService;
import com.syc.perms.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private TbLogMapper tbLogMapper;

    @Override
    public void saveLog(TbLog log) {
        tbLogMapper.insert(log);
    }

    @Override
    public R getLogList(Integer page, Integer limit, UserSearch search) {
        PageHelper.startPage(page, limit);
        TbLogExample example = new TbLogExample();
        //设置按创建时间降序排序
        example.setOrderByClause("id DESC");
        TbLogExample.Criteria criteria = example.createCriteria();

        if (search.getOperation() != null && !"".equals(search.getOperation())) {
            criteria.andOperationLike("%" + search.getOperation() + "%");
        }

        if (search.getCreateTimeStart() != null && !"".equals(search.getCreateTimeStart())) {
            criteria.andCreateTimeGreaterThanOrEqualTo(DateUtil.getDateByString(search.getCreateTimeStart()));
        }
        if (search.getCreateTimeEnd() != null && !"".equals(search.getCreateTimeEnd())) {
            criteria.andCreateTimeLessThanOrEqualTo(DateUtil.getDateByString(search.getCreateTimeEnd()));
        }

        List<TbLog> logs = tbLogMapper.selectByExample(example);

        PageInfo<TbLog> pageInfo = new PageInfo<>(logs);
        R resultUtil = new R();
        resultUtil.setCode(0);
        resultUtil.setCount(pageInfo.getTotal());
        resultUtil.setData(pageInfo.getList());
        return resultUtil;
    }

    @Override
    public int deleteLogByDate(Date date) {
        TbLogExample example = new TbLogExample();
        TbLogExample.Criteria criteria = example.createCriteria();
        criteria.andCreateTimeLessThanOrEqualTo(date);
        return tbLogMapper.deleteByExample(example);
    }

}
