package rpg.login;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.data.dao.AccountMapper;
import rpg.data.dao.UserMapper;
import rpg.pojo.Account;
import rpg.pojo.AccountExample;
import rpg.pojo.AccountExample.Criteria;
import rpg.pojo.User;

/**
 * 登陆功能
 * 
 * @author ljq
 *
 */
@Component("login")
public class Login {
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private UserMapper userMapper;

	public   User login(String username, String psw) {
		//验证账户和密码
		AccountExample example = new AccountExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<Account> list = accountMapper.selectByExample(example);
		if(list==null||list.size()==0) return null;
		Account account = list.get(0);
		if(!account.getPsw().equals(psw)) return null;
		Integer id = account.getId();
		User user = userMapper.selectByPrimaryKey(id);
		return user;
	}
}
