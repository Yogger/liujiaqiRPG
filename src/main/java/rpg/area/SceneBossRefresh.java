package rpg.area;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.netty.channel.Channel;
import rpg.pojo.BossScene;
import rpg.pojo.Buff;
import rpg.pojo.Group;
import rpg.pojo.Monster;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.session.IOsession;
import rpg.skill.SkillList;
import rpg.util.UserService;

public class SceneBossRefresh implements Runnable {

	private UserService userService;
	private User user;
	private BossScene bossScene;
	private Channel ch;

	public SceneBossRefresh(UserService userService, User user, BossScene bossScene, Channel ch) {
		super();
		this.userService = userService;
		this.user = user;
		this.bossScene = bossScene;
		this.ch = ch;
	}
	
	@Override
	public void run() {

		while (true) {
			Monster monster = IOsession.monsterMp.get(ch.remoteAddress());
			System.out.println(monster.getName());
			// 达到挑战时间
			if (System.currentTimeMillis() - bossScene.getStartTime() >= bossScene
					.getLastedTime()) {
				Group group2 = IOsession.userGroupMp.get(user.getGroupId());
				if (group2 != null) {
					List<User> list2 = group2.getList();
					for (User user2 : list2) {
						Channel channel = IOsession.userchMp.get(user2);
						channel.writeAndFlush("已达挑战时间上限，副本挑战失败，自动退出副本");
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
//				Monster monster = IOsession.monsterMp.get(ch.remoteAddress());
				boolean ackstatus = IOsession.ackStatus.containsKey(ch.remoteAddress());
				if (ackstatus) {
					if (IOsession.ackStatus.get(ch.remoteAddress()) == 2) {
						HashMap<Integer, Long> buffTime1 = IOsession.buffTimeMp
								.get(user);
						// 检验怪物Buff
						String word1 = userService.checkMonsterBuff(monster, ch);
							// 怪物存活
							if (monster.getHp() > 0) {
								// 检测用户状态
								if (buffTime1!=null&&buffTime1.get(3) != null) {
									Group group2 = IOsession.userGroupMp.get(user.getGroupId());
									if (group2 != null) {
										List<User> list = group2.getList();
										for (User user2 : list) {
											Channel channel = IOsession.userchMp.get(user2);
											channel.writeAndFlush(
													word1 + "-你有最强护盾护体，免疫伤害，你的血量剩余：" + user2.getHp());
										}
									}
								} else {
								List<User> userList = monster.getUserList();
//					int hp = user.getHp() - monster.getAck();
								Random random = new Random();
								int monsterSkillId = random.nextInt(2);
								int ackuserId = random.nextInt(userList.size());
								// 第一种攻击
								if (monsterSkillId == 0) {
									User user3 = userList.get(ackuserId);
									int hp = user3.getHp() - monster.getAck();
									Channel ch1 = IOsession.userchMp.get(user3);
									Group group2 = IOsession.userGroupMp
											.get(user3.getGroupId());
									if (hp > 0) {
										// 选定具体技能
										List<Integer> skillList = monster
												.getSkillList();
										int randomId = random.nextInt(skillList.size());
										Integer integer = skillList.get(randomId);
										Skill skill2 = SkillList.mp
												.get(String.valueOf(integer));
										userService.updateUserBuff(user3, skill2);
										Buff getBuff = IOsession.buffMp.get(
												Integer.valueOf(skill2.getEffect()));
										user3.setHp(hp);
										// 推送攻击消息
										ch1.writeAndFlush(word1 + "-你受到单体技能"
												+ skill2.getName() + "伤害："
												+ monster.getAck() + "-你的血量剩余：" + hp
												+ "你产生了" + getBuff.getName());
										if (group2 != null) {
											List<User> list = group2.getList();
											for (User user2 : list) {
												Channel channel = IOsession.userchMp
														.get(user2);
												if (channel != ch1) {
													channel.writeAndFlush(word1 + "-"
															+ user3.getNickname()
															+ "受到单体技能伤害："
															+ monster.getAck()
															+ "-血量剩余：" + hp);
												}
											}
										}
									} else {
										ch1.writeAndFlush("你已被打死，副本挑战失败，你已被传送出副本");
										if (group2 != null) {
											List<User> list = group2.getList();
											for (User user2 : list) {
												Channel channel = IOsession.userchMp
														.get(user2);
												if (channel != ch1)
													channel.writeAndFlush(user3
															.getNickname()
															+ "已被打死，副本挑战失败，你已被传送出副本");
												IOsession.ackStatus.put(
														channel.remoteAddress(), 0);
											}
										}
										user3.setHp(1000);
										IOsession.ackStatus.put(ch1.remoteAddress(), 0);
										// 回收boss场景，怪物线程
										BossScene bossScene1 = IOsession.userBossMp
												.get(user.getGroupId());
										bossScene1 = null;
										IOsession.userBossMp.remove(user.getGroupId());
										break;
									}
								}
								// 第二种攻击
								else {
									Group group2 = IOsession.userGroupMp
											.get(user.getGroupId());
									if (group2 != null) {
										List<User> list2 = group2.getList();
										for (User user2 : list2) {
											Channel channel = IOsession.userchMp
													.get(user2);
											int hp = user2.getHp() - monster.getAck();
											// 血量满足
											if (hp > 0) {
												user2.setHp(hp);
												channel.writeAndFlush(
														word1 + "-你受到全体技能(无视护盾)伤害："
																+ monster.getAck()
																+ "-你的血量剩余：" + hp);
											} else {
												Channel ch1 = IOsession.userchMp
														.get(user2);
												ch1.writeAndFlush(
														"你已被打死，副本挑战失败，你已被传送出副本");
												if (group2 != null) {
													List<User> list = group2.getList();
													for (User user3 : list) {
														Channel channel1 = IOsession.userchMp
																.get(user3);
														if (channel1 != ch1)
															channel1.writeAndFlush(user2
																	.getNickname()
																	+ "已被打死，副本挑战失败，你已被传送出副本");
														IOsession.ackStatus.put(channel1
																.remoteAddress(), 0);
													}
												}
												user2.setHp(1000);
												IOsession.ackStatus
														.put(ch1.remoteAddress(), 0);
												// 回收boss场景，怪物线程
												BossScene bossScene1 = IOsession.userBossMp
														.get(user.getGroupId());
												bossScene1 = null;
												IOsession.userBossMp
														.remove(user.getGroupId());
												break;
											}
										}
									}
								}
							}
							}
							// 怪物死亡
							else {
								Group group2 = IOsession.userGroupMp
										.get(user.getGroupId());
								if (group2 != null) {
									List<User> list2 = group2.getList();
									for (User user2 : list2) {
										Channel channel = IOsession.userchMp.get(user2);
										IOsession.ackStatus.put(channel.remoteAddress(),
												0);
									}
								}
								// 回收boss场景，怪物线程
								BossScene bossScene1 = IOsession.userBossMp
										.get(user.getGroupId());
								bossScene1 = null;
								IOsession.userBossMp.remove(user.getGroupId());
								break;
							}
						
					}else {
						break;
					}
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}