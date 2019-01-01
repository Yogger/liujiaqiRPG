package rpg.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.data.dao.GhMapper;
import rpg.data.dao.GhstoreMapper;
import rpg.data.dao.GhuserMapper;
import rpg.data.dao.UserMapper;
import rpg.pojo.Gh;
import rpg.pojo.GhExample;
import rpg.pojo.Ghstore;
import rpg.pojo.GhstoreExample;
import rpg.pojo.Ghuser;
import rpg.pojo.GhuserExample;
import rpg.pojo.GhuserExample.Criteria;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.task.TaskManage;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;

/**
 * 工会逻辑
 * 
 * @author ljq
 *
 */
@Component
public class GhDispatch {

	@Autowired
	private GhMapper ghMapper;
	@Autowired
	private GhuserMapper ghuserMapper;
	@Autowired
	private GhstoreMapper ghstoreMapper;
	@Autowired
	private UserMapper userMapper;
	
	private ReentrantLock lock=new ReentrantLock();

	public void gh(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == 3 && msg[1].equals("creat")) {
			creatGh(user, ch, msg);
		} else if (msg.length == 2 && msg[1].equals("show")) {
			showGh(user, ch, group, msg);
		} else if (msg.length == 3 && msg[1].equals("join")) {
			joinGh(user, ch, group, msg);
		} else if (msg.length == 3 && msg[1].equals("accept")) {
			acceptGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("showsq")) {
			showsqGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("showuser")) {
			showuserGh(user, ch, group, msg);
		} else if (msg.length == 3 && msg[1].equals("t")) {
			tGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("js")) {
			jsGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("esc")) {
			quitGh(user, ch, group, msg);
		} else if (msg.length == 4 && msg[1].equals("raise")) {
			raiseGh(user, ch, group, msg);
		} else if (msg.length == 4 && msg[1].equals("down")) {
			downGh(user, ch, group, msg);
		} else if (msg.length > 3 && msg[1].equals("put")) {
			putGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("showstore")) {
			showstoreGh(user, ch, group, msg);
		} else if (msg.length > 3 && msg[1].equals("take")) {
			takeGh(user, ch, group, msg);
		}
	}

	private void takeGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		lock.lock();
		try {
			GhstoreExample example = new GhstoreExample();
			rpg.pojo.GhstoreExample.Criteria criteria = example.createCriteria();
			criteria.andIdEqualTo(user.getGhId());
			List<Ghstore> list = ghstoreMapper.selectByExample(example);
			if (msg[2].equals("2") && msg.length == 5) {
				for (Ghstore ghstore : list) {
					if (ghstore.getGzid().equals(msg[3])) {
						GhstoreExample example2 = new GhstoreExample();
						rpg.pojo.GhstoreExample.Criteria criteria2 = example2.createCriteria();
						criteria2.andGzidEqualTo(ghstore.getGzid());
						if (ghstore.getIsadd() == 0) {
							if (StringUtils.isNumeric(msg[4])) {
								if (Integer.valueOf(msg[4]) == 1) {
									Userbag userbag = new Userbag();
									userbag.setId(ghstore.getGzid());
									userbag.setUsername(user.getNickname());
									userbag.setGid(ghstore.getWpid());
									userbag.setNumber(1);
									userbag.setNjd(ghstore.getNjd());
									userbag.setIsadd(0);
									List<Userbag> list2 = IOsession.userBagMp.get(user);
									list2.add(userbag);
									ghstoreMapper.deleteByExample(example2);
									SendMsg.send("物品拿取成功",ch);
								} else {
									SendMsg.send("数量错误",ch);
								}
							} else {
								SendMsg.send("指令错误",ch);
							}
						} else {
							if (StringUtils.isNumeric(msg[4])) {
								Integer num = Integer.valueOf(msg[4]);
								if (num > 0) {
									Yaopin yaopin = IOsession.yaopinMp.get(ghstore.getWpid());
									RpgUtil.putYaopin(user, yaopin, num);
									//								Userbag userbag = new Userbag();
									//								userbag.setId(ghstore.getGzid());
									//								userbag.setUsername(user.getNickname());
									//								userbag.setGid(ghstore.getWpid());
									//								userbag.setNumber(Integer.valueOf(msg[4]));
									//								userbag.setNjd(ghstore.getNjd());
									//								userbag.setIsadd(1);
									//								List<Userbag> list2 = IOsession.userBagMp.get(user);
									//								list2.add(userbag);
									if (ghstore.getNumber() > num) {
										ghstore.setNumber(ghstore.getNumber() - num);
										ghstoreMapper.updateByExample(ghstore, example2);
										SendMsg.send("物品拿取成功",ch);
									} else if (ghstore.getNumber() == num) {
										ghstoreMapper.deleteByExample(example2);
										SendMsg.send("物品拿取成功",ch);
									} else {
										SendMsg.send("指令错误",ch);
									}
								} else {
									SendMsg.send("请放入正确的数量",ch);
								}
							} else {
								SendMsg.send("指令错误",ch);
							}
						}
						break;
					}
				}
			} else if (msg[2].equals("1") && msg.length == 4) {
				if (StringUtils.isNumeric(msg[3])) {
					Gh gh = ghMapper.selectByPrimaryKey(user.getGhId());
					if (gh.getGold() >= Integer.valueOf(msg[3])) {
						gh.setGold(gh.getGold() - Integer.valueOf(msg[3]));
						ghMapper.updateByPrimaryKey(gh);
						user.setMoney(user.getMoney() + Integer.valueOf(msg[3]));
						SendMsg.send("金币取出成功",ch);
					} else {
						SendMsg.send("金币不足",ch);
					}
				} else {
					SendMsg.send("指令错误",ch);
				}
			} else {
				SendMsg.send("指令错误",ch);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	private void showstoreGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhstoreExample example = new GhstoreExample();
		rpg.pojo.GhstoreExample.Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(user.getGhId());
		List<Ghstore> list = ghstoreMapper.selectByExample(example);
		String yaopinWord = "";
		String zbWord = "";
		for (Ghstore ghstore : list) {
			Yaopin yaopin = IOsession.yaopinMp.get(ghstore.getWpid());
			Zb zb = IOsession.zbMp.get(ghstore.getWpid());
			if (yaopin != null)
				yaopinWord += "格子id:" + ghstore.getGzid() + "---" + yaopin.getName() + "---" + ghstore.getNumber()
						+ "\n";
			else {
				zbWord += "格子id:" + ghstore.getGzid() + "---" + zb.getName() + "-耐久度：" + ghstore.getNjd() + "-攻击力"
						+ zb.getAck() + "\n";
			}
		}
		Gh gh = ghMapper.selectByPrimaryKey(user.getGhId());
		SendMsg.send("金币：" + gh.getGold() + "\n" + yaopinWord + zbWord,ch);
	}

	private void putGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		List<Userbag> list = IOsession.userBagMp.get(user);
		if (msg[2].equals("2") && msg.length == 5) {
			for (Userbag userbag : list) {
				if (userbag.getId().equals(msg[3])) {
					if (userbag.getIsadd() == 0) {
						if (StringUtils.isNumeric(msg[4])) {
							if (Integer.valueOf(msg[4]) == 1) {
								Ghstore ghstore = new Ghstore();
								ghstore.setId(user.getGhId());
								ghstore.setGzid(userbag.getId());
								ghstore.setWpid(userbag.getGid());
								ghstore.setNumber(1);
								ghstore.setNjd(userbag.getNjd());
								ghstore.setIsadd(0);
								ghstoreMapper.insert(ghstore);
								list.remove(userbag);
								SendMsg.send("物品放入成功",ch);
							} else {
								SendMsg.send("数量不足",ch);
							}
						} else {
							SendMsg.send("指令错误",ch);
						}
					} else {
						if (StringUtils.isNumeric(msg[4])) {
							Integer num = Integer.valueOf(msg[4]);
							if (num > 0) {
								GhstoreExample example = new GhstoreExample();
								rpg.pojo.GhstoreExample.Criteria criteria = example.createCriteria();
								criteria.andWpidEqualTo(userbag.getGid());
								List<Ghstore> list2 = ghstoreMapper.selectByExample(example);
								if (list2 != null && list2.size() == 0) {
									Ghstore ghstore = new Ghstore();
									ghstore.setId(user.getGhId());
									ghstore.setGzid(userbag.getId());
									ghstore.setWpid(userbag.getGid());
									ghstore.setNumber(num);
									ghstore.setNjd(userbag.getNjd());
									ghstore.setIsadd(1);
									ghstoreMapper.insert(ghstore);
									if (userbag.getNumber() > num) {
										userbag.setNumber(userbag.getNumber() - num);
										SendMsg.send("物品放入成功",ch);
									} else if (userbag.getNumber() == num) {
										list.remove(userbag);
										SendMsg.send("物品放入成功",ch);
									} else {
										SendMsg.send("数量不足",ch);
									}
								} else {
									Ghstore ghstore = list2.get(0);
									ghstore.setNumber(ghstore.getNumber() + num);
									ghstoreMapper.updateByExample(ghstore, example);
									if (userbag.getNumber() > num) {
										userbag.setNumber(userbag.getNumber() - num);
										SendMsg.send("物品放入成功",ch);
									} else if (userbag.getNumber() == num) {
										list.remove(userbag);
										SendMsg.send("物品放入成功",ch);
									} else {
										SendMsg.send("数量不足",ch);
									}
								}
							} else {
								SendMsg.send("请放入正确的数量",ch);
							}
						} else {
							SendMsg.send("指令错误",ch);
						}
					}
					break;
				}
			}
		} else if (msg[2].equals("1") && msg.length == 4) {
			if (StringUtils.isNumeric(msg[3])) {
				if (user.getMoney() >= Integer.valueOf(msg[3])) {
					Gh gh = ghMapper.selectByPrimaryKey(user.getGhId());
					gh.setGold(gh.getGold() + Integer.valueOf(msg[3]));
					ghMapper.updateByPrimaryKey(gh);
					user.setMoney(user.getMoney() - Integer.valueOf(msg[3]));
					SendMsg.send("金币放入成功",ch);
				} else {
					SendMsg.send("金币不足",ch);
				}
			} else {
				SendMsg.send("指令错误",ch);
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	private void downGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(msg[2]);
		GhuserExample example2 = new GhuserExample();
		Criteria criteria2 = example2.createCriteria();
		criteria2.andUsernameEqualTo(user.getNickname());
		List<Ghuser> list2 = ghuserMapper.selectByExample(example2);
		if (list2 != null && list2.size() > 0) {
			Ghuser ghuser = list2.get(0);
			List<Ghuser> list = ghuserMapper.selectByExample(example);
			if (list != null && list.size() > 0) {
				Ghuser ghuser2 = list.get(0);
				if (ghuser.getPower() < ghuser2.getPower()) {
					if (msg[3].equals("3") && ghuser2.getPower() < 3) {
						if (ghuser.getPower() < 2) {
							ghuser2.setPower(3);
							ghuser2.setJobname("精英");
							ghuserMapper.updateByExample(ghuser2, example);
							User user2 = IOsession.nameMap.get(msg[2]);
							Channel channel = IOsession.userchMp.get(user2);
							SendMsg.send("降低职位成功",ch);
							SendMsg.send("你被降级为精英",channel);
						} else {
							SendMsg.send("权限不够,不能降级该职位",ch);
						}
					} else if (msg[3].equals("4") && ghuser2.getPower() < 4) {
						if (ghuser.getPower() < 3) {
							ghuser2.setPower(4);
							ghuser2.setJobname("成员");
							ghuserMapper.updateByExample(ghuser2, example);
							User user2 = IOsession.nameMap.get(msg[2]);
							Channel channel = IOsession.userchMp.get(user2);
							SendMsg.send("降低职位成功",ch);
							SendMsg.send("你被降级为成员",channel);
						} else {
							SendMsg.send("权限不够,不能降级该职位",ch);
						}
					} else {
						SendMsg.send("指令错误",ch);
					}
				} else {
					SendMsg.send("权限不够,不能降级该玩家头衔",ch);
				}
			} else {
				SendMsg.send("不存在该玩家",ch);
			}
		} else {
			SendMsg.send("你不在工会",ch);
		}
	}

	private void raiseGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(msg[2]);
		GhuserExample example2 = new GhuserExample();
		Criteria criteria2 = example2.createCriteria();
		criteria2.andUsernameEqualTo(user.getNickname());
		List<Ghuser> list2 = ghuserMapper.selectByExample(example2);
		if (list2 != null && list2.size() > 0) {
			Ghuser ghuser = list2.get(0);
			List<Ghuser> list = ghuserMapper.selectByExample(example);
			if (list != null && list.size() > 0) {
				Ghuser ghuser2 = list.get(0);
				if (ghuser.getPower() < ghuser2.getPower() - 1) {
					if (msg[3].equals("3")) {
						if (ghuser.getPower() < 3) {
							ghuser2.setPower(3);
							ghuser2.setJobname("精英");
							ghuserMapper.updateByExample(ghuser2, example);
							User user2 = IOsession.nameMap.get(msg[2]);
							Channel channel = IOsession.userchMp.get(user2);
							SendMsg.send("提升职位成功",ch);
							SendMsg.send("你被提升为精英",channel);
						} else {
							SendMsg.send("权限不够,不能提升该职位",ch);
						}
					} else if (msg[3].equals("2")) {
						if (ghuser.getPower() < 2) {
							ghuser2.setPower(2);
							ghuser2.setJobname("副会长");
							ghuserMapper.updateByExample(ghuser2, example);
							User user2 = IOsession.nameMap.get(msg[2]);
							Channel channel = IOsession.userchMp.get(user2);
							SendMsg.send("提升职位成功",ch);
							SendMsg.send("你被提升为副会长",channel);
						} else {
							SendMsg.send("权限不够,不能提升该职位",ch);
						}
					} else {
						SendMsg.send("指令错误",ch);
					}
				} else {
					SendMsg.send("权限不够,不能提升该玩家头衔",ch);
				}
			} else {
				SendMsg.send("不存在该玩家",ch);
			}
		} else {
			SendMsg.send("你不在工会",ch);
		}
	}

	private void quitGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(user.getNickname());
		ghuserMapper.deleteByExample(example);
		user.setGhId(0);
		userMapper.updateByPrimaryKey(user);
		SendMsg.send("退出工会成功",ch);
	}

	private void jsGh(User user, Channel ch, ChannelGroup group, String[] msg) {

	}

	private void tGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(msg[2]);
		GhuserExample example2 = new GhuserExample();
		Criteria criteria2 = example2.createCriteria();
		criteria2.andUsernameEqualTo(user.getNickname());
		List<Ghuser> list2 = ghuserMapper.selectByExample(example2);
		if (list2 != null && list2.size() > 0) {
			Ghuser ghuser = list2.get(0);
			List<Ghuser> list = ghuserMapper.selectByExample(example);
			if (list != null && list.size() > 0) {
				Ghuser ghuser2 = list.get(0);
				if (ghuser.getPower() < ghuser2.getPower()) {
					ghuserMapper.deleteByExample(example);
					User user2 = IOsession.nameMap.get(msg[2]);
					user2.setGhId(0);
					userMapper.updateByPrimaryKey(user2);
					Channel channel = IOsession.userchMp.get(user2);
					SendMsg.send("已将该玩家踢出",ch);
					SendMsg.send("你被踢出工会",channel);
				} else {
					SendMsg.send("权限不够,不能移除该玩家",ch);
				}
			} else {
				SendMsg.send("不存在该玩家",ch);
				return;
			}
		} else {
			SendMsg.send("你不在工会",ch);
			return;
		}
	}

	private void showuserGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(user.getGhId());
		List<Ghuser> list = ghuserMapper.selectByExample(example);
		String string = "";
		for (Ghuser ghuser : list) {
			string += "用户名:" + ghuser.getUsername() + "---职位:" + ghuser.getJobname() + "\n";
		}
		SendMsg.send(string,ch);
	}

	private void showsqGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		if (user.getGhId() != 0) {
			String string = "";
			HashMap<String, String> map = IOsession.ghsqMp.get(user.getGhId());
			if (map != null) {
				for (Entry<String, String> entry : map.entrySet()) {
					string += "申请人：" + entry.getKey() + "---申请时间:" + entry.getValue();
				}
				SendMsg.send(string,ch);
			}
		} else {
			SendMsg.send("你没有工会",ch);
		}
	}

	private void acceptGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		int ghId = user.getGhId();
		if (ghId != 0) {
			HashMap<String, String> map = IOsession.ghsqMp.get(user.getGhId());
			if (map.containsKey(msg[2])) {
				GhuserExample example = new GhuserExample();
				Criteria criteria = example.createCriteria();
				criteria.andUsernameEqualTo(user.getNickname());
				List<Ghuser> list = ghuserMapper.selectByExample(example);
				Ghuser ghuser2 = list.get(0);
				if (ghuser2.getPower() < 4) {
					User user2 = IOsession.nameMap.get(msg[2]);
					String username = user2.getNickname();
					user2.setGhId(ghId);
					userMapper.updateByPrimaryKey(user2);
					Ghuser ghuser = new Ghuser();
					ghuser.setId(ghId);
					ghuser.setUsername(username);
					ghuser.setPower(4);
					ghuser.setJobname("成员");
					ghuserMapper.insert(ghuser);
					SendMsg.send(user2.getNickname() + "加入工会",ch);
					Channel channel = IOsession.userchMp.get(user2);
					SendMsg.send("你成功加入工会",channel);
					TaskManage.checkTaskCompleteBytaskid(user2, 8);
				} else {
					SendMsg.send("你没有权限",ch);
				}
			} else {
				SendMsg.send("指令错误",ch);
			}
		} else {
			SendMsg.send("你没有工会",ch);
		}
	}

	private void joinGh(User user, Channel ch, ChannelGroup group, String[] msg) {
//		HashMap<String, Ghuser> map = IOsession.ghUserMp.get(Integer.valueOf(msg[2]));
//		for (Ghuser ghuser : map.values()) {
//			if (ghuser.getPower() < 4) {
//				User user2 = IOsession.nameMap.get(ghuser.getUsername());
//				Channel channel = IOsession.userchMp.get(user2);
//				SendMsg.send("申请已发送");
//				SendMsg.send(user.getNickname()+"---申请加入工会");
//			}
//		}
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		if (StringUtils.isNumeric(msg[2])) {
			criteria.andIdEqualTo(Integer.valueOf(msg[2]));
			List<Ghuser> list = ghuserMapper.selectByExample(example);
			for (Ghuser ghuser : list) {
				if (ghuser.getPower() < 4) {
					User user2 = IOsession.nameMap.get(ghuser.getUsername());
					Channel channel = IOsession.userchMp.get(user2);
					HashMap<String, String> hashMap = new HashMap<>();
					String name = user.getNickname();
					Integer id = ghuser.getId();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
					hashMap.put(name, df.format(new Date()));
					IOsession.ghsqMp.put(id, hashMap);
					SendMsg.send(user.getNickname() + "---申请加入工会",channel);
				}
			}
			SendMsg.send("申请已发送",ch);
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	private void creatGh(User user, Channel ch, String[] msg) {
		String name = user.getNickname();
		Gh gh = new Gh();
		gh.setName(msg[2]);
		gh.setCreatname(name);
		gh.setLevel(1);
		gh.setGold(0);
		ghMapper.insertSelective(gh);
		Integer id = gh.getId();
		Ghuser ghuser = new Ghuser();
		ghuser.setId(id);
		ghuser.setUsername(name);
		ghuser.setPower(1);
		ghuser.setJobname("会长");
		ghuserMapper.insertSelective(ghuser);
		user.setGhId(id);
		userMapper.updateByPrimaryKey(user);
		// 存入缓存
//		IOsession.ghMp.put(id, gh);
//		HashMap<String, Ghuser> map = new HashMap<>();
//		map.put(name, ghuser);
//		IOsession.ghUserMp.put(id, map);
		SendMsg.send("创建工会成功",ch);
		TaskManage.checkTaskCompleteBytaskid(user, 8);
	}

	private void showGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhExample ghExample = new GhExample();
		List<Gh> list = ghMapper.selectByExample(ghExample);
		String req = "";
		for (Gh gh : list) {
			req += gh.getId() + "---公会名：" + gh.getName() + "---创建者名：" + gh.getCreatname() + "---工会等级：" + gh.getLevel()
					+ "\n";
		}
//		if (IOsession.ghMp != null) {
//			for (Gh gh : IOsession.ghMp.values()) {
//				req += gh.getId() + "---公会名：" + gh.getName() + "---创建者名：" + gh.getCreatname() + "---工会等级："
//						+ gh.getLevel() + "\n";
//			}
//		}
		SendMsg.send(req,ch);
	}
}
