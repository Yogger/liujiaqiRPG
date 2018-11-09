package RPG.RPGljq;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rpg.data.dao.UserskillMapper;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;
import rpg.pojo.UserskillExample.Criteria;


@Service("test")
public class Test {
	@Autowired
	private UserskillMapper userskillMapper;
	
	public  void test() {
		UserskillExample example = new UserskillExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo("xiaohong");
		List<Userskill> list = userskillMapper.selectByExample(example);
		for (Userskill userskill : list) {
			System.out.println(userskill.getSkill());
		}
	}
}
