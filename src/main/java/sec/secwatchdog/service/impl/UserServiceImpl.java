package sec.secwatchdog.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sec.secwatchdog.mapper.ManagersDao;
import sec.secwatchdog.model.Managers;
import sec.secwatchdog.service.UserService;
import sec.secwatchdog.util.AESUtil;

@Service("userService")
public class UserServiceImpl implements UserService{

	@Autowired
	private ManagersDao managersDao;
	
	@Override
	public Managers login(Managers manager) {
		Managers resultUser = null;
        AESUtil util = new AESUtil(); 

        resultUser = managersDao.login(manager.getUsername());
        try {
			resultUser.setPassword(util.decryptData(resultUser.getPassword()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultUser;
	}

	@Override
	public Managers checklogin(String userName) {
		Managers resultUser = null;
		resultUser = managersDao.checkLogin(userName);
		return resultUser;
	}
	   public Managers findUserByName(String name){
	        return managersDao.findUserByName(name);
	    }

 
}
