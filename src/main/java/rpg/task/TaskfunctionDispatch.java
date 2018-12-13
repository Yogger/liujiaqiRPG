package rpg.task;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.Task;
import rpg.pojo.TaskProcess;
import rpg.pojo.User;
import rpg.session.IOsession;

@Component
public class TaskfunctionDispatch {
	public void task(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == 2 && msg[1].equals("show")) {
			showTask(user, ch);
		} else {
			ch.writeAndFlush("指令错误");
		}
	}

	private void showTask(User user, Channel ch) {
		Map<Integer, TaskProcess> doingTask = user.getDoingTask();
		String string = "";
		if (doingTask != null) {
			for (TaskProcess taskProcess : doingTask.values()) {
				Task task = IOsession.taskMp.get(taskProcess.getTaskid());
				string += "任务名称：" + taskProcess.getName() + "----任务进度" + taskProcess.getNum() + "/" + task.getNum();
			}
		}
		ch.writeAndFlush(string);
	}
}
