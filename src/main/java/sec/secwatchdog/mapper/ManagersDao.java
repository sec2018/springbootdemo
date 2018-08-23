package sec.secwatchdog.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import sec.secwatchdog.model.Managers;

@Mapper
public interface ManagersDao {
	@Select("select * from managers where userName=#{ManagerName}")
	public Managers login(String ManagerName);
	
	@Select("select * from managers where userName=#{ManagerName}")
	public Managers checkLogin(String ManagerName);
	
   @Select("select * from managers where username = #{name}")
    Managers findUserByName(String name);
	
}
