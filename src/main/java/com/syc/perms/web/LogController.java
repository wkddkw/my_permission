package com.syc.perms.web;

import com.syc.perms.pojo.R;
import com.syc.perms.pojo.UserSearch;
import com.syc.perms.service.LogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogService logServiceImpl;

    @RequestMapping(value = "/logList", method = RequestMethod.GET)
    @RequiresPermissions("log:log:list")
    public String logList() {
        return "page/log/logList";
    }

    @RequestMapping(value = "/getLogList", method = RequestMethod.GET)
    @RequiresPermissions("log:log:list")
    @ResponseBody
    public R getLogList(Integer page, Integer limit, UserSearch search) {
        return logServiceImpl.getLogList(page, limit, search);
    }

}
