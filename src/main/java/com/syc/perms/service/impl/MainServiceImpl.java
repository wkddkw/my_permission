package com.syc.perms.service.impl;

import com.syc.perms.mapper.MainMapper;
import com.syc.perms.mapper.TbUsersMapper;
import com.syc.perms.pojo.TbUsers;
import com.syc.perms.pojo.TbUsersExample;
import com.syc.perms.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MainServiceImpl implements MainService {
	
	@Autowired
	private TbUsersMapper tbUsersMapper;
	
	@Autowired
	private MainMapper mainMapper;

	@Override
	public List<TbUsers> selUserList() {
		TbUsersExample example=new TbUsersExample();
		return tbUsersMapper.selectByExample(example);
	}
	
	@Override
	public List<TbUsers> selUsersToday() {
		return mainMapper.selUsersToday();
	}

	@Override
	public List<TbUsers> selUsersYesterday() {
		return mainMapper.selUsersYesterday();
	}

	@Override
	public List<TbUsers> selUsersYearWeek() {
		return mainMapper.selUsersYearWeek();
	}
	
	@Override
	public List<TbUsers> selUsersMonth() {
		return mainMapper.selUsersMonth();
	}

	@Override
	public int seUserCountBygender(int i) {
		TbUsersExample example=new TbUsersExample();
		TbUsersExample.Criteria criteria = example.createCriteria();
		criteria.andSexEqualTo(i+"");
		List<TbUsers> list = tbUsersMapper.selectByExample(example);
		return list.size();
	}

}
