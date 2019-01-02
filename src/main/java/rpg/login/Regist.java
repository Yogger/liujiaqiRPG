package rpg.login;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.data.dao.AccountMapper;
import rpg.data.dao.UserMapper;
import rpg.data.dao.UserbuffMapper;
import rpg.data.dao.UserskillMapper;
import rpg.data.dao.UserzbMapper;
import rpg.pojo.Account;
import rpg.pojo.User;
import rpg.pojo.Userbuff;
import rpg.pojo.Userskill;
import rpg.pojo.Userzb;

/**
 * 注册功能
 * 
 * @author ljq
 *
 */
@Component("regist")
public class Regist {
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserbuffMapper userbuffMapper;
	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private UserzbMapper userzbMapper;

	public boolean regist(String username, String psw, String psw1, Integer roleid) {
		if (psw.equals(psw1)) {
			Account account = new Account();
			account.setUsername(username);
			account.setPsw(psw);
			accountMapper.insertSelective(account);
			Integer id = account.getId();
			User user = new User();
			user.setId(id);
			user.setNickname(username);
			user.setAreaid(1);
			user.setHp(10000);
			user.setMp(100);
			user.setMoney(8000);
			user.setGhId(0);
			user.setRoletype(roleid);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			user.setUpdatetime(df.format(new Date()));
			userMapper.insert(user);
			Userbuff userbuff = new Userbuff();
			userbuff.setUsername(username);
			userbuff.setBuff("1");
			userbuffMapper.insert(userbuff);
			Userbuff userbuff2 = new Userbuff();
			userbuff2.setUsername(username);
			userbuff2.setBuff("2");
			userbuffMapper.insert(userbuff2);
			Userskill userskill = new Userskill();
			userskill.setUsername(username);
			Userskill userskill2 = new Userskill();
			userskill2.setSkill(1);
			userskill2.setUsername(username);
			if (roleid == 1)
				userskill.setSkill(3);
			else if (roleid == 2)
				userskill.setSkill(5);
			else if (roleid == 3)
				userskill.setSkill(6);
			else if (roleid == 4)
				userskill.setSkill(7);
			userskillMapper.insert(userskill);
			userskillMapper.insert(userskill2);
			Userzb userzb = new Userzb();
			userzb.setId(UUID.randomUUID().toString());
			userzb.setUsername(username);
			userzb.setZbid(101);
			userzb.setNjd(10);
			userzb.setIsuse(1);
			userzbMapper.insert(userzb);
			return true;
		} else {
			return false;
		}
	}
}
