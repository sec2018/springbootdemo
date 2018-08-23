package sec.secwatchdog.controller;


import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sec.secwatchdog.model.Managers;
import sec.secwatchdog.redis.service.RedisService;
import sec.secwatchdog.service.UserService;
import sec.secwatchdog.shiro.BusinessException;
import sec.secwatchdog.shiro.LuoErrorCode;
import sec.secwatchdog.util.AESUtil;

import net.sf.json.JSONObject;
import com.google.gson.Gson;

@Controller
@RequestMapping("/user")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private UserService userService;
	@Autowired
    private RedisService redisService;
	private Gson gson = new Gson();
	
	@RequestMapping(value="/login", produces="text/html;charset=UTF-8",method=RequestMethod.POST)
	@ResponseBody
	public String login(Managers manager,HttpServletRequest request){
		
       AESUtil aes = new AESUtil();
        try{
            UsernamePasswordToken token = new UsernamePasswordToken(manager.getUsername(), aes.encryptData(manager.getPassword()));  
            Subject currentUser = SecurityUtils.getSubject();        
                //使用shiro来验证  
            //token.setRememberMe(true);  
            currentUser.login(token);//验证角色和权限
            
          //验证是否登录成功
            if(currentUser.isAuthenticated()){
            	logger.info("用户[" + manager.getUsername() + "]登录认证通过(这里可进行一些认证通过后的系统参数初始化操作)");
		       /*	if(currentUser.hasRole("admin")){
		             System.out.println("有角色admin");
		        }*/
				Managers resultUser=userService.login(manager);
				HttpSession session=request.getSession();
				session.setAttribute("currentUser", resultUser);
				Cookie cookie = new Cookie("rememberuser",manager.getUsername()+"+"+manager.getPassword());
				cookie.setMaxAge(10000);
				JSONObject jsStr = JSONObject.fromObject(resultUser);
				String res = jsStr.toString();
				return res;
		               // return "redirect:/getUI";//重定向至HT-UI
                }else{
                token.clear();
 
                return "";
            }
        }catch(UnknownAccountException uae){
        	logger.warn("对用户[" + manager.getUsername() + "]进行登录验证...验证未通过，未知账户");
            request.setAttribute("message_login", "未知账户");
        }catch(IncorrectCredentialsException ice){
        	logger.warn("对用户[" + manager.getUsername() + "]进行登录验证...验证未通过，错误的凭证");
            request.setAttribute("message_login", "密码不正确");
        }catch(LockedAccountException lae){
        	logger.warn("对用户[" + manager.getUsername() + "]进行登录验证...验证未通过，错误的凭证");
            request.setAttribute("message_login", "账户已锁定");
        }catch(ExcessiveAttemptsException eae){
        	logger.warn("对用户[" + manager.getUsername() + "]进行登录验证...验证未通过，错误的凭证");
            request.setAttribute("message_login", "用户名或密码错误次数过多");
        }catch(AuthenticationException ae){
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
        	logger.warn("对用户[" + manager.getUsername() + "]进行登录验证...验证未通过，错误的凭证");
            request.setAttribute("message_login", "用户名或密码不正确");
        }catch(Exception ex){
            throw new BusinessException(LuoErrorCode.LOGIN_VERIFY_FAILURE);
        }
        return "";
	}


	@RequestMapping("/index")
	@ResponseBody
	public String index(HttpServletRequest request,ModelMap model,RedirectAttributes redirectAttributes) throws Exception{

		HttpSession session=request.getSession();
		if(session.getAttribute("currentUser")==null){;
			return "redirect:/login.jsp";
		}
		Managers resultUser= (Managers) session.getAttribute("currentUser");
	
		return resultUser.getUsername()+" login success";
	}


}
