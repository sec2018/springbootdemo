package sec.secwatchdog.service;

import java.util.List;
import java.util.Map;

import sec.secwatchdog.model.Managers;

public interface UserService {

	public Managers login(Managers manager) throws Exception;
	
	public Managers checklogin(String username) throws Exception;
	
	Managers findUserByName(String name) throws Exception;

}

