package com.syc.perms.web;

import com.google.code.kaptcha.Producer;
import com.syc.perms.exception.CommonException;
import com.syc.perms.pojo.Menu;
import com.syc.perms.pojo.R;
import com.syc.perms.pojo.TbAdmin;
import com.syc.perms.pojo.TbRoles;
import com.syc.perms.service.AdminService;
import com.syc.perms.util.ShiroUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.List;

@Controller
@RequestMapping("/sys")
public class SysController {

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private AdminService adminServiceImpl;

    @RequestMapping("/main")
    public String main() {
        return "page/main";
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest req) {
        TbAdmin admin = (TbAdmin) SecurityUtils.getSubject().getPrincipal();
        req.setAttribute("admin", admin);
        return "redirect:/index.jsp";
    }

    @RequestMapping("/refuse")
    public String refuse() {
        return "refuse";
    }

    /**
     * 管理员登陆
     */
    @RequestMapping("/login")
    @ResponseBody
    public R login(HttpServletRequest req, String username, String password, String vcode) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(vcode)) {
            throw new CommonException("参数不能为空");
        }
        if (!vcode.toLowerCase().equals(((String) req.getSession().getAttribute("kaptcha")).toLowerCase())) {
            return R.error("验证码不正确");
        }
        try {
            Subject subject = ShiroUtils.getSubject();
            //md5加密
            //password=DigestUtils.md5DigestAsHex(password.getBytes());
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            subject.login(token);
        } catch (UnknownAccountException e) {
            return R.error(e.getMessage());
        } catch (IncorrectCredentialsException e) {
            return R.error(e.getMessage());
        } catch (LockedAccountException e) {
            return R.error(e.getMessage());
        } catch (AuthenticationException e) {
            return R.error("账户验证失败");
        }
        return R.ok();
    }

    /**
     * 登出
     */
    @RequestMapping(value = "/loginOut")
    public String loginOut() {
        ShiroUtils.logout();
        return "redirect:/login.jsp";
    }

    /**
     * 生成验证码
     */
    @RequestMapping("/vcode")
    public void vcode(HttpServletRequest req, HttpServletResponse resp) throws Exception {
//		VerifyCode vc = new VerifyCode();
//		BufferedImage image = vc.getImage();// 获取一次性验证码图片
        String text = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(text);
        // 该方法必须在getImage()方法之后来调用
        // System.out.println("验证码图片上的文本:"+vc.getText());//获取图片上的文本
        // 把文本保存到session中，为验证做准备
        //req.getSession().setAttribute("vcode", vc.getText());
        //保存到shiro session
//        ShiroUtils.setSessionAttribute("kaptcha", text);
        req.getSession().setAttribute("kaptcha", text);
        //VerifyCode.output(image, resp.getOutputStream());// 把图片写到指定流中
        ImageIO.write(image, "JPEG", resp.getOutputStream());
    }

    /**
     * 获取用户菜单列表
     */
    @RequestMapping(value = "/getMenus", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    @ResponseBody
    public List<Menu> getMenus(HttpServletRequest req, HttpServletResponse resp) {
        //TbAdmin admin = (TbAdmin) req.getSession().getAttribute("admin");
        TbAdmin admin = (TbAdmin) SecurityUtils.getSubject().getPrincipal();
        List<Menu> menus = null;
        if (admin != null) {
            // 得到用户菜单
            menus = adminServiceImpl.selMenus(admin);
        }
        return menus;
    }

    /**
     * 个人资料
     */
    @RequestMapping("/personalData")
    public String personalData(HttpServletRequest req) {
        TbAdmin admin = (TbAdmin) SecurityUtils.getSubject().getPrincipal();
        TbAdmin ad = adminServiceImpl.selAdminById(admin.getId());
        List<TbRoles> roles = adminServiceImpl.selRoles();
        req.setAttribute("ad", ad);
        req.setAttribute("roles", roles);
        return "page/admin/personalData";
    }

    @RequestMapping("/checkAdminByEmail")
    @ResponseBody
    public R checkAdminByEmail(String eMail,String username) {
        TbAdmin admin=adminServiceImpl.getAdminByEmail(eMail,username);
        if(admin!=null){
            return new R(500,"邮箱已被占用！");
        }
        return new R(0);
    }

    @RequestMapping("/changePwd")
    public String changePwd() {
        return "page/admin/changePwd";
    }

    /**
     * 修改密码
     */
    //@SysLog(value="修改密码")
    @RequestMapping("/updPwd")
    @ResponseBody
    public R updPwd(HttpServletRequest req,String oldPwd,String newPwd) {
        TbAdmin user = (TbAdmin)SecurityUtils.getSubject().getPrincipal();
        if(user!=null){
            //测试账号不支持修改密码
            if("test".equals(user.getUsername())){
                return R.error();
            }
            TbAdmin admin = adminServiceImpl.login(user.getUsername(), oldPwd);
            if(admin!=null){
                admin.setPassword(newPwd);
                adminServiceImpl.updAdminPws(admin);
                //修改密码后移除作用域，重新登陆
                SecurityUtils.getSubject().logout();
                return R.ok();
            }else{
                return new R(501,"旧密码错误，请重新填写！");
            }
        }
        return new R(500,"请求错误！");
    }

}
