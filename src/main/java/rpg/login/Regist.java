package rpg.login;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.data.dao.AccountMapper;
import rpg.data.dao.UserMapper;
import rpg.pojo.Account;
import rpg.pojo.User;

/**
 * 注册功能
 * @author ljq
 *
 */
@Component("regist")
public class Regist {
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private UserMapper userMapper;
	
	public boolean regist(String username,String psw,String psw1) {
		if(psw.equals(psw1)) {
			Account account = new Account();
			account.setUsername(username);
			account.setPsw(psw);
			accountMapper.insertSelective(account);
			Integer id = account.getId();
			User user = new User();
			user.setId(id);
			user.setNickname(username);
			user.setAreaid(1);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			user.setUpdatetime(df.format(new Date()));
			userMapper.insert(user);
			return true;
		} else {
			return false;
		}
	}
}
