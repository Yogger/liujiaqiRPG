package rpg.task;

import java.util.Map;

import io.netty.channel.Channel;
import rpg.pojo.Task;
import rpg.pojo.TaskProcess;
import rpg.pojo.User;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.util.RpgUtil;

public class TaskManage {
	
	/**
	 * 检查任务是否完成
	 * @param user
	 * @param reqid
	 */
	public static void checkTaskComplete(User user,int reqid) {
		Map<Integer, TaskProcess> doingTask = user.getDoingTask();
		String string="";
		if(doingTask!=null) {
		for (TaskProcess taskProcess : doingTask.values()) {
			if(taskProcess.getReqid()==reqid) {
				int num = taskProcess.getNum();
				Task task = IOsession.taskMp.get(taskProcess.getTaskid());
				int num2 = task.getNum();
				if(num+1<num2) {
					taskProcess.setNum(num+1);
					string+="任务名称:"+task.getName()+"---任务进度:"+taskProcess.getNum()+"/"+num2;
				} else {
					Map<Integer, TaskProcess> finishTask = user.getFinishTask();
					finishTask.put(taskProcess.getTaskid(), taskProcess);
					doingTask.remove(taskProcess.getReqid());
					string+="任务名称---"+task.getName()+"---完成"+"\n";
					String string2 = getAward(user, task);
					string+=string2;
				}
			}
		}
		Channel channel = IOsession.userchMp.get(user);
		channel.writeAndFlush(string);
	}
	}

	private static String getAward(User user, Task task) {
		String string="";
		user.setMoney(user.getMoney()+task.getMoney());
		int id = task.getAwardId();		Zb zb = IOsession.zbMp.get(id);
		Yaopin yaopin = IOsession.yaopinMp.get(id);
		if (zb != null) {
			RpgUtil.putZb(user, zb);
			string+="任务奖励---获得金钱：" + task.getMoney() + "获得装备：" + zb.getName();
		} else if (yaopin != null) {
			RpgUtil.putYaopin(user, yaopin);
			string+="任务奖励---获得金钱：" + task.getMoney() + "获得药品：" + yaopin.getName();
		}
		return string;
	}
}