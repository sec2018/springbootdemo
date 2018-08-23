package sec.secwatchdog.controller;

import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.support.json.JSONUtils;
import net.sf.json.JSONObject;
import sec.secwatchdog.shiro.BusinessException;
import sec.secwatchdog.shiro.LuoErrorCode;
import sec.secwatchdog.util.AESUtil;

@Controller
@RequestMapping("/shiro")
public class ShiroController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/index")
    public ModelAndView getIndex(HttpServletRequest request) throws Exception {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping("/error")
    public ModelAndView exceptionForPageJumps(HttpServletRequest request) throws Exception {
        throw new BusinessException(LuoErrorCode.NULL_OBJ);
    }

    @RequestMapping(value="/businessException.json", method=RequestMethod.POST)
    @ResponseBody  
    public String businessException(HttpServletRequest request) {
        throw new BusinessException(LuoErrorCode.NULL_OBJ);
    }

    @RequestMapping(value="/otherException.json", method=RequestMethod.POST)
    @ResponseBody  
    public String otherException(HttpServletRequest request) throws Exception {
        throw new Exception();
    }

    //跳转到登录页面
    @RequestMapping("/login")
    public ModelAndView login() throws Exception {
        ModelAndView mav = new ModelAndView("login");
        return mav;
    }

    //跳转到登录成功页面
    @RequestMapping("/success")
    public ModelAndView loginsuccess() throws Exception {
        ModelAndView mav = new ModelAndView("success");
        return mav;
    }

    /** 
     * 验证用户和密码
     * @param String username,String password
     * @return 
     */  
    @RequestMapping(value="/checkLogin",method=RequestMethod.POST)  
    @ResponseBody  
    public String checkLogin(String username,String password) {  
        Map<String, Object> result = new HashMap<String, Object>();
        AESUtil aes = new AESUtil();
        try{
            UsernamePasswordToken token = new UsernamePasswordToken(username, aes.encryptData(password));  
            Subject currentUser = SecurityUtils.getSubject();  
            if (!currentUser.isAuthenticated()){
                //ʹ��shiro����֤  
                token.setRememberMe(true);  
                currentUser.login(token);//��֤��ɫ��Ȩ��  
            } 
        }catch(Exception ex){
			logger.error("【系统错误】",ex);
            throw new BusinessException(LuoErrorCode.LOGIN_VERIFY_FAILURE);
        }
        result.put("success", true);
        JSONObject jsStr = JSONObject.fromObject(result);
		String res = jsStr.toString();
		return res;
//        return JSONUtils.toJSONString(result);  
    }  

    /** 
     * 退出登录
     */  
    @RequestMapping(value="/logout",method=RequestMethod.POST)    
    @ResponseBody    
    public String logout() {   
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        Subject currentUser = SecurityUtils.getSubject();       
        currentUser.logout();    
        return JSONUtils.toJSONString(result);
    }  
}
