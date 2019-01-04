package rpg.task;

import java.util.Map;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.InstructionsType;
import rpg.configure.MsgSize;
import rpg.pojo.Task;
import rpg.pojo.TaskProcess;
import rpg.pojo.User;
import rpg.session.IOsession;
import rpg.util.SendMsg;

/**任务功能处理逻辑
 * @author ljq
 *
 */
@Component
public class TaskfunctionDispatch {
	public void task(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue() && InstructionsType.SHOWD.getValue().equals(msg[1])) {
			showTask(user, ch);
		} else if (msg.length == MsgSize.MAX_MSG_SIZE_2.getValue() && InstructionsType.SHOWF.getValue().equals(msg[1])){
			showFinishTask(user,ch);
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	private void showFinishTask(User user, Channel ch) {
		Map<Integer, TaskProcess> finishTask = user.getFinishTask();
		String string="";
		if(finishTask!=null) {
			for (TaskProcess taskProcess : finishTask.values()) {
				Task task = IOsession.taskMp.get(taskProcess.getTaskid());
				string += "任务名称：" + taskProcess.getName()+"\n";
			}
		}
		SendMsg.send(string,ch);
	}

	private void showTask(User user, Channel ch) {
		Map<Integer, TaskProcess> doingTask = user.getDoingTask();
		String string = "";
		if (doingTask != null) {
			for (TaskProcess taskProcess : doingTask.values()) {
				Task task = IOsession.taskMp.get(taskProcess.getTaskid());
				string += "任务名称：" + taskProcess.getName() + "----任务进度" + taskProcess.getNum() + "/" + task.getNum()+"\n";
			}
		}
		SendMsg.send(string,ch);
	}
}
