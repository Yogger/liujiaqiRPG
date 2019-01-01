package rpg.task;

import java.util.List;
import java.util.Map;

import io.netty.channel.Channel;
import rpg.pojo.Task;
import rpg.pojo.TaskProcess;
import rpg.pojo.User;
import rpg.pojo.Userzb;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;

public class TaskManage {

	/**
	 * 检查任务是否完成,带对象id
	 * 
	 * @param user
	 * @param reqid
	 */
	public static void checkTaskComplete(User user, int reqid) {
		Map<Integer, TaskProcess> doingTask = user.getDoingTask();
		String string = "";
		if (doingTask != null) {
			for (TaskProcess taskProcess : doingTask.values()) {
				if (taskProcess.getReqid() == reqid) {
					int num = taskProcess.getNum();
					Task task = IOsession.taskMp.get(taskProcess.getTaskid());
					int num2 = task.getNum();
					if (num + 1 < num2) {
						taskProcess.setNum(num + 1);
						string += "任务名称:" + task.getName() + "---任务进度:" + taskProcess.getNum() + "/" + num2;
					} else {
						Map<Integer, TaskProcess> finishTask = user.getFinishTask();
						finishTask.put(taskProcess.getTaskid(), taskProcess);
						doingTask.remove(taskProcess.getTaskid());
						string += "任务名称---" + task.getName() + "---完成" + "\n";
						String string2 = getAward(user, task);
						string += string2;
					}
				}
			}
			Channel channel = IOsession.userchMp.get(user);
			SendMsg.send(string, channel);
		}
	}

	/**
	 * 检查任务是否完成,带任务id
	 * 
	 * @param user
	 * @param reqid
	 */
	public static void checkTaskCompleteBytaskid(User user, int taskid) {
		String string = "";
		Map<Integer, TaskProcess> doingTask = user.getDoingTask();
		if (doingTask != null) {
			TaskProcess taskProcess = doingTask.get(taskid);
			if (taskProcess != null) {
				int num = taskProcess.getNum();
				Task task = IOsession.taskMp.get(taskProcess.getTaskid());
				int num2 = task.getNum();
				if (num + 1 < num2) {
					taskProcess.setNum(num + 1);
					string += "任务名称:" + task.getName() + "---任务进度:" + taskProcess.getNum() + "/" + num2;
				} else {
					Map<Integer, TaskProcess> finishTask = user.getFinishTask();
					finishTask.put(taskProcess.getTaskid(), taskProcess);
					doingTask.remove(taskProcess.getTaskid());
					string += "任务名称---" + task.getName() + "---完成" + "\n";
					String string2 = getAward(user, task);
					string += string2;
				}
				Channel channel = IOsession.userchMp.get(user);
				SendMsg.send(string, channel);
			}
		}
	}

	public static void checkTaskCompleteBytaskidWithzb(User user, int taskid, Zb zb) {
		String string = "";
		Map<Integer, TaskProcess> doingTask = user.getDoingTask();
		if (doingTask != null) {
			TaskProcess taskProcess = doingTask.get(taskid);
			if (taskProcess != null) {
				if (zb.getLevel() >= taskProcess.getReqid()) {
					int num = taskProcess.getNum();
					Task task = IOsession.taskMp.get(taskProcess.getTaskid());
					int num2 = task.getNum();
					if (num + 1 < num2) {
						taskProcess.setNum(num + 1);
						string += "任务名称:" + task.getName() + "---任务进度:" + taskProcess.getNum() + "/" + num2;
					} else {
						Map<Integer, TaskProcess> finishTask = user.getFinishTask();
						finishTask.put(taskProcess.getTaskid(), taskProcess);
						doingTask.remove(taskProcess.getTaskid());
						string += "任务名称---" + task.getName() + "---完成" + "\n";
						String string2 = getAward(user, task);
						string += string2;
					}
					Channel channel = IOsession.userchMp.get(user);
					SendMsg.send(string, channel);
				}
			}
		}
	}

	public static void checkTaskCompleteByTaskidWithZbList(User user, int taskid, List<Userzb> list) {
		int num = 0;
		for (Userzb userzb : list) {
			Zb zb = IOsession.zbMp.get(userzb.getZbid());
			num += zb.getLevel();
		}
		String string = "";
		Map<Integer, TaskProcess> doingTask = user.getDoingTask();
		if (doingTask != null) {
			TaskProcess taskProcess = doingTask.get(taskid);
			if (taskProcess != null) {
				Task task = IOsession.taskMp.get(taskProcess.getTaskid());
				int num2 = task.getNum();
				if (num >= num2) {
					Map<Integer, TaskProcess> finishTask = user.getFinishTask();
					finishTask.put(taskProcess.getTaskid(), taskProcess);
					doingTask.remove(taskProcess.getTaskid());
					string += "任务名称---" + task.getName() + "---完成" + "\n";
					String string2 = getAward(user, task);
					string += string2;
				}
				Channel channel = IOsession.userchMp.get(user);
				SendMsg.send(string, channel);
			}
		}
	}

	public static void checkMoneyTaskCompleteBytaskid(User user, int taskid) {
		String string = "";
		Map<Integer, TaskProcess> doingTask = user.getDoingTask();
		if (doingTask != null) {
			TaskProcess taskProcess = doingTask.get(taskid);
			if (taskProcess != null) {
				int num = taskProcess.getNum();
				Task task = IOsession.taskMp.get(taskProcess.getTaskid());
				int num2 = task.getNum();
				if (user.getMoney() < num2) {
					taskProcess.setNum(user.getMoney());
					string += "任务名称:" + task.getName() + "---任务进度:" + taskProcess.getNum() + "/" + num2 + "\n";
				} else {
					Map<Integer, TaskProcess> finishTask = user.getFinishTask();
					finishTask.put(taskProcess.getTaskid(), taskProcess);
					doingTask.remove(taskProcess.getTaskid());
					string += "任务名称---" + task.getName() + "---完成" + "\n";
					String string2 = getAward(user, task);
					string += string2;
				}
				Channel channel = IOsession.userchMp.get(user);
				SendMsg.send(string, channel);
			}
		}
	}

	/**
	 * 获取任务奖励
	 * 
	 * @param user
	 * @param task
	 * @return
	 */
	private static String getAward(User user, Task task) {
		String string = "";
		user.setMoney(user.getMoney() + task.getMoney());
		int id = task.getAwardId();
		Zb zb = IOsession.zbMp.get(id);
		Yaopin yaopin = IOsession.yaopinMp.get(id);
		if (zb != null) {
			RpgUtil.putZb(user, zb);
			string += "任务奖励---获得金钱：" + task.getMoney() + "获得装备：" + zb.getName() + "\n";
		} else if (yaopin != null) {
			RpgUtil.putYaopin(user, yaopin);
			string += "任务奖励---获得金钱：" + task.getMoney() + "获得药品：" + yaopin.getName() + "\n";
		}
		TaskManage.checkMoneyTaskCompleteBytaskid(user, 11);
		return string;
	}
}