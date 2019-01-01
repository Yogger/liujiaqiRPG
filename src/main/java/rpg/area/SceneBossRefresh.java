package rpg.area;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import rpg.pojo.BossScene;
import rpg.pojo.Buff;
import rpg.pojo.Group;
import rpg.pojo.Monster;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.session.IOsession;
import rpg.skill.SkillList;
import rpg.util.SendMsg;
import rpg.util.UserService;

public class SceneBossRefresh implements Runnable {

	private UserService userService;
	private User initUser;
	private BossScene bossScene;
	private Channel ch;
	private Group firstGroup;
	private UserAttribute attribute;

	public SceneBossRefresh(UserService userService, User initUser, BossScene bossScene, Channel ch, Group firstGroup,
			UserAttribute attribute) {
		super();
		this.userService = userService;
		this.initUser = initUser;
		this.bossScene = bossScene;
		this.ch = ch;
		this.firstGroup = firstGroup;
		this.attribute = attribute;
	}

	@Override
	public void run() {

		while (true) {
			boolean exitFlag = false;
			List<User> list3 = firstGroup.getList();
			User user = initUser;
			System.out.println(list3.size());
			if (list3.size() == 0) {
				BossScene bossScene = IOsession.userBossMp.get(user.getGroupId());
				if (bossScene != null) {
					bossScene = null;
					IOsession.userBossMp.remove(user.getGroupId());
				}
				break;
			}
			if (firstGroup.getUser() != initUser) {
				for (User userLeader : list3) {
					user = userLeader;
					break;
				}
			}
			// Monster monster = IOsession.monsterMp.get(ch.remoteAddress());
			List<Monster> list4 = IOsession.monsterMp.get(ch.remoteAddress());
			// 达到挑战时间
			if (System.currentTimeMillis() - bossScene.getStartTime() >= bossScene.getLastedTime()) {
				Group group2 = IOsession.userGroupMp.get(user.getGroupId());
				if (group2 != null) {
					List<User> list2 = group2.getList();
					for (User user2 : list2) {
						Channel channel = IOsession.userchMp.get(user2);
						SendMsg.send("已达挑战时间上限，副本挑战失败，自动退出副本", channel);
						IOsession.ackStatus.put(channel.remoteAddress(), 0);
					}
				}
				// 回收boss场景，怪物线程
				BossScene bossScene1 = IOsession.userBossMp.get(user.getGroupId());
				bossScene1 = null;
				IOsession.userBossMp.remove(user.getGroupId());
				break;
			}
			// 挑战时间未到
			else {
				for (int index = 0; index < list4.size(); index++) {
					Monster monster = list4.get(index);
					System.out.println(monster.getName());
					// Monster monster = IOsession.monsterMp.get(ch.remoteAddress());
					boolean ackstatus = IOsession.ackStatus.containsKey(ch.remoteAddress());
					if (ackstatus) {
						if (IOsession.ackStatus.get(ch.remoteAddress()) == 2) {
							// ConcurrentHashMap<Integer,Long> buffTime1 = IOsession.buffTimeMp
							// .get(user);
							// 检验怪物Buff
							String word1 = userService.checkMonsterBuff(monster, ch);
							Group group6 = IOsession.userGroupMp.get(user.getGroupId());
							if (group6 != null) {
								List<User> list = group6.getList();
								for (User user6 : list) {
									Channel channel = IOsession.userchMp.get(user6);
									SendMsg.send(word1, channel);
								}
							}
							// 怪物存活
							if (monster.getHp() > 0) {
								boolean wudiFlag = false;
								for (User user2 : list3) {
									ConcurrentHashMap<Integer, Long> buffTime1 = IOsession.buffTimeMp.get(user2);
									if (buffTime1 != null && buffTime1.get(3) != null) {
										wudiFlag = true;
										break;
									}
								}
								// 检测用户状态
								if (wudiFlag) {
									Group group2 = IOsession.userGroupMp.get(user.getGroupId());
									if (group2 != null) {
										List<User> list = group2.getList();
										for (User user2 : list) {
											Channel channel = IOsession.userchMp.get(user2);
											SendMsg.send("002" + "-" + monster.getName() + "-你有最强护盾护体，免疫伤害，你的血量剩余："
													+ user2.getHp(), channel);
										}
									}
								} else {
									List<User> userList = monster.getUserList();
									// int hp = user.getHp() - monster.getAck();
									Random random = new Random();
									int monsterSkillId = random.nextInt(2);
									int ackuserId = random.nextInt(userList.size());
									// 第一种攻击
									if (monsterSkillId == 0) {
										User user3 = userList.get(ackuserId);
										int hp = user3.getHp() - monster.getAck();
										Channel ch1 = IOsession.userchMp.get(user3);
										Group group2 = IOsession.userGroupMp.get(user3.getGroupId());
										if (hp > 0) {
											// 选定具体技能
											List<Integer> skillList = monster.getSkillList();
											int randomId = random.nextInt(skillList.size());
											Integer integer = skillList.get(randomId);
											Skill skill2 = SkillList.mp.get(String.valueOf(integer));
											userService.updateUserBuff(user3, skill2);
											Buff getBuff = IOsession.buffMp.get(Integer.valueOf(skill2.getEffect()));
											user3.setHp(hp);
											// 推送攻击消息
											SendMsg.send("002" + "-你受到" + monster.getName() + "单体技能(无视防御)"
													+ skill2.getName() + "伤害：" + monster.getAck() + "-你的血量剩余：" + hp
													+ "你产生了" + getBuff.getName(), ch1);
											if (group2 != null) {
												List<User> list = group2.getList();
												for (User user2 : list) {
													Channel channel = IOsession.userchMp.get(user2);
													if (channel != ch1) {
														SendMsg.send("002" + "-" + user3.getNickname() + "受到"
																+ monster.getName() + "单体技能(无视防御)伤害：" + monster.getAck()
																+ "-血量剩余：" + hp, channel);
													}
												}
											}
										} else {
											SendMsg.send("你已被打死，副本挑战失败，你已被传送出副本", ch1);
											if (group2 != null) {
												List<User> list = group2.getList();
												for (User user2 : list) {
													Channel channel = IOsession.userchMp.get(user2);
													if (channel != ch1)
														SendMsg.send(user3.getNickname() + "已被打死，副本挑战失败，你已被传送出副本",
																channel);
													IOsession.ackStatus.put(channel.remoteAddress(), 0);
												}
											}
											user3.setHp(1000);
											IOsession.ackStatus.put(ch1.remoteAddress(), 0);
											// 回收boss场景，怪物线程
											BossScene bossScene1 = IOsession.userBossMp.get(user.getGroupId());
											bossScene1 = null;
											IOsession.userBossMp.remove(user.getGroupId());
											exitFlag = true;
											break;
										}
									}
									// 第二种攻击
									else {
										Group group2 = IOsession.userGroupMp.get(user.getGroupId());
										if (group2 != null) {
											List<User> list2 = group2.getList();
											for (User user2 : list2) {
												Channel channel = IOsession.userchMp.get(user2);
												int monsterAck = monster.getAck() - attribute.getDef();
												if (monsterAck <= 0)
													monsterAck = 1;
												int hp = user2.getHp() - monsterAck;
												// 血量满足
												if (hp > 0) {
													user2.setHp(hp);
													SendMsg.send("002" + "-你受到" + monster.getName() + "全体技能(无视护盾)伤害："
															+ monsterAck + "-你的血量剩余：" + hp, channel);
												} else {
													Channel ch1 = IOsession.userchMp.get(user2);
													SendMsg.send("你已被打死，副本挑战失败，你已被传送出副本", ch1);
													if (group2 != null) {
														List<User> list = group2.getList();
														for (User user3 : list) {
															Channel channel1 = IOsession.userchMp.get(user3);
															if (channel1 != ch1)
																SendMsg.send(
																		user2.getNickname() + "已被打死，副本挑战失败，你已被传送出副本",
																		channel1);
															IOsession.ackStatus.put(channel1.remoteAddress(), 0);
														}
													}
													user2.setHp(1000);
													IOsession.ackStatus.put(ch1.remoteAddress(), 0);
													// 回收boss场景，怪物线程
													BossScene bossScene1 = IOsession.userBossMp.get(user.getGroupId());
													bossScene1 = null;
													IOsession.userBossMp.remove(user.getGroupId());
													exitFlag = true;
													break;
												}
											}
										}
									}
								}
							}
							// 怪物死亡
							else {
								Group group2 = IOsession.userGroupMp.get(user.getGroupId());
								if (group2 != null) {
									List<User> list2 = group2.getList();
									for (User user2 : list2) {
										Channel channel = IOsession.userchMp.get(user2);
										IOsession.ackStatus.put(channel.remoteAddress(), 0);
									}
								}
								// 回收boss场景，怪物线程
								BossScene bossScene1 = IOsession.userBossMp.get(user.getGroupId());
								bossScene1 = null;
								IOsession.userBossMp.remove(user.getGroupId());
								exitFlag = true;
								break;
							}

						} else {
							exitFlag = true;
							break;
						}
					}
				}
				if (exitFlag)
					break;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}