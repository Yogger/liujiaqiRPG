package rpg.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.data.dao.AccountMapper;
import rpg.data.dao.UserMapper;
import rpg.data.dao.UserbagMapper;
import rpg.data.dao.UserzbMapper;
import rpg.pojo.Account;
import rpg.pojo.AccountExample;
import rpg.pojo.AccountExample.Criteria;
import rpg.pojo.Task;
import rpg.pojo.TaskProcess;
import rpg.session.IOsession;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.UserbagExample;
import rpg.pojo.Userzb;
import rpg.pojo.UserzbExample;

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
	@Autowired
	private UserbagMapper userbagMapper;
	@Autowired
	private UserzbMapper userzbMapper;

	public User login(String username, String psw) {
		// 验证账户和密码
		AccountExample example = new AccountExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<Account> list = accountMapper.selectByExample(example);
		if (list == null || list.size() == 0)
			return null;
		Account account = list.get(0);
		if (!account.getPsw().equals(psw))
			return null;
		Integer id = account.getId();
		User user = userMapper.selectByPrimaryKey(id);
		user.setLevel(1);
		user.setExp(0);
		return user;
	}

	public void loadData(User user) {
		// 加载用户背包
		String nickname = user.getNickname();
		UserbagExample example = new UserbagExample();
		rpg.pojo.UserbagExample.Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(nickname);
		List<Userbag> userBagList = userbagMapper.selectByExample(example);
		IOsession.userBagMp.put(user, userBagList);
		// 加载用户装备
		UserzbExample example1 = new UserzbExample();
		rpg.pojo.UserzbExample.Criteria criteria1 = example1.createCriteria();
		criteria1.andUsernameEqualTo(nickname);
		List<Userzb> userZbList = userzbMapper.selectByExample(example1);
		IOsession.userZbMp.put(user, userZbList);
		// 加载任务
		Map<Integer, TaskProcess> doingTask = new ConcurrentHashMap<Integer, TaskProcess>();
		int cout =1;
		for(int i=1;i<=2;i++) {
			Task task = IOsession.taskMp.get(i);
			TaskProcess process = new TaskProcess();
			process.setId(cout++);
			process.setName(task.getName());
			process.setNum(0);
			if(i==2) process.setNum(1);
			process.setReqid(task.getReqid());
			process.setTaskid(i);
			doingTask.put(i, process);
		}
		user.setDoingTask(doingTask);
		Map<Integer, TaskProcess> finishTask=new ConcurrentHashMap<Integer, TaskProcess>();
		user.setFinishTask(finishTask);
	}
}
