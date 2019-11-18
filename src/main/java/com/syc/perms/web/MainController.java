package com.syc.perms.web;

import com.syc.perms.pojo.TbUsers;
import com.syc.perms.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/main")
public class MainController {

    @Autowired
    private MainService mainServiceImpl;

    @RequestMapping("/dataAccessGender")
    @ResponseBody
    public Map<String, Object> dataAccessGender() {
        Map<String, Object> map= new HashMap<>();
        String[] categories = {"男", "女", "保密"};
        map.put("categories", categories);
        Map<String, Object> json;
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < categories.length; i++) {
            json = new HashMap<>();
            json.put("value", mainServiceImpl.seUserCountBygender(i));
            json.put("name", categories[i]);
            list.add(json);
        }
        map.put("values", list);
        return map;
    }

    @RequestMapping("getUserTotal")
    @ResponseBody
    public List<TbUsers> getUserTotal() {
        return mainServiceImpl.selUserList();
    }

    @RequestMapping("getUsersToday")
    @ResponseBody
    public List<TbUsers> getUsersToday() {
        return mainServiceImpl.selUsersToday();
    }

    @RequestMapping("getUsersYesterday")
    @ResponseBody
    public List<TbUsers> getUsersYesterday() {
        return mainServiceImpl.selUsersYesterday();
    }

    @RequestMapping("getUsersYearWeek")
    @ResponseBody
    public List<TbUsers> getUsersYearWeek() {
        return mainServiceImpl.selUsersYearWeek();
    }

    @RequestMapping("getUsersMonth")
    @ResponseBody
    public List<TbUsers> getUsersMonth() {
        return mainServiceImpl.selUsersMonth();
    }

}
