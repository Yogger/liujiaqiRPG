package RPG.RPGljq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rpg.data.dao.AccountMapper;
import rpg.pojo.Account;

@Service("test")
public class Test {
	@Autowired
	private AccountMapper accountMapper;
	
	public  void test() {
		Account account = accountMapper.selectByPrimaryKey(1);
		System.out.println(account.getUsername());
		System.out.println(account.getPsw());
	}
}
