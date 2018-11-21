package rpg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.area.Area;
import rpg.data.dao.UserskillMapper;
import rpg.data.dao.UserzbMapper;
import rpg.pojo.BossScene;
import rpg.pojo.Group;
import rpg.pojo.Monster;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;
import rpg.pojo.UserskillExample.Criteria;
import rpg.pojo.Userzb;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.skill.SkillList;
import rpg.util.RpgUtil;

/**
 * 副本战斗逻辑
 * 
 * @author ljq
 *
 */
@Component("ackBossDispatch")
public class AckBossDispatch {

	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private UserzbMapper userzbMapper;

	public void ack(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		// 获取用户技能列表
		Integer id = user.getAreaid();
		String nickname = user.getNickname();
		UserskillExample example = new UserskillExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(nickname);
		List<Userskill> list = userskillMapper.selectByExample(example);
		// 获取地图中的怪物
//		LinkedList<Monster> monsterList = Area.sceneList.get(id - 1).getMonsterList();
		BossScene bossScene = IOsession.userBossMp.get(user.getGroupId());
		ArrayList<Monster> monsterList = bossScene.getMonsterList();
		// 第一次攻击
		if (msg.length == 3) {
			ackFirst(user, ch, group, msg, id, nickname, list, monsterList, bossScene);
		}
		// 二次及以上攻击
		else if (msg.length == 1) {
			Monster monster = IOsession.monsterMp.get(ch.remoteAddress());
			// 找到配置的技能
			if (msg[0].equals("esc")) {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				ch.writeAndFlush("成功退出战斗");
			} else if (msg[0].equals("ack")) {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				ch.writeAndFlush("指令错误");
			} else {
				for (Userskill userskill : list) {
					String skillId = String.valueOf(userskill.getSkill());
					if (skillId.equals(msg[0])) {
						Skill skill = SkillList.mp.get(msg[0]);

						long millis = System.currentTimeMillis();// 获取当前时间毫秒值
						HashMap<String, Long> map = SkillList.cdMp.get(user);
						Long lastmillis = map.get(skillId);

						// 蓝量满足
						if (skill.getMp() <= user.getMp()) {
							if (lastmillis != null) {
								// 技能cd满足
								if (millis - lastmillis >= skill.getCd()) {
									// 存储上次使用技能时间
									long currentTimeMillis = System.currentTimeMillis();
									HashMap<String, Long> curSkill = new HashMap<String, Long>();
									curSkill.put(skillId, currentTimeMillis);
									SkillList.cdMp.put(user, curSkill);

//							ch.writeAndFlush("使用了" + skill.getName());
									user.setMp(user.getMp() - skill.getMp());
									// 判断装备是否还有耐久度
									UserAttribute attribute = IOsession.attMp.get(user);
									List<Userzb> list1 = IOsession.userZbMp.get(user);
									for (Userzb userzb : list1) {
										if (userzb.getNjd() <= 0) {
											Zb zb = IOsession.zbMp.get(userzb.getZbid());
											if (zb != null && attribute != null) {
												attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
												userzb.setIsuse(0);
											}
										}
									}
									int ack = attribute.getAck();
									int hurt = skill.getHurt() + ack;
									int monsterHp = monster.getHp() - hurt;
									monster.setHp(monsterHp);
									if (monsterHp <= 0) {
										ch.writeAndFlush("boss已被消灭，退出副本");
										Group group2 = IOsession.userGroupMp.get(user.getGroupId());
										RpgUtil.ackEnd(user, ch, monster);
										if (group2 != null) {
											List<User> list3 = group2.getList();
											for (User user3 : list3) {
												Channel channel1 = IOsession.userchMp.get(user3);
												if (channel1 != ch) {
													channel1.writeAndFlush(user.getNickname() + "消灭了"
															+ monster.getName() + "-你已通关，退出副本");
													RpgUtil.ackEnd(user3, channel1, monster);
												}
												IOsession.ackStatus.put(channel1.remoteAddress(), 0);
											}
										}
										monster.setAliveFlag(false);
										IOsession.ackStatus.put(ch.remoteAddress(), 0);
										// 损耗装备耐久度
										for (Userzb userzb : list1) {
											userzb.setNjd(userzb.getNjd() - 5);
										}
										bossScene = null;// 回收boss场景
									} else {
										ch.writeAndFlush("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余"
												+ user.getMp() + "\n" + "攻击了" + monster.getName() + "-造成" + hurt
												+ "点伤害-怪物血量" + monster.getHp());
										// 损耗装备耐久度
										for (Userzb userzb : list1) {
											userzb.setNjd(userzb.getNjd() - 5);
										}
									}
									break;
								}
								// cd未到
								else {
									ch.writeAndFlush("技能冷却中");
								}
							}
							// 技能未使用过
							else {
								// 存储上次使用技能时间
								long currentTimeMillis = System.currentTimeMillis();
								HashMap<String, Long> curSkill = new HashMap<String, Long>();
								curSkill.put(skillId, currentTimeMillis);
								SkillList.cdMp.put(user, curSkill);
//						ch.writeAndFlush("使用了" + skill.getName());
								user.setMp(user.getMp() - skill.getMp());
								// 判断装备是否还有耐久度
								UserAttribute attribute = IOsession.attMp.get(user);
								List<Userzb> list1 = IOsession.userZbMp.get(user);
								for (Userzb userzb : list1) {
									if (userzb.getNjd() <= 0) {
										Zb zb = IOsession.zbMp.get(userzb.getZbid());
										if (zb != null && attribute != null) {
											attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
											userzb.setIsuse(0);
										}
									}
								}
								int ack = attribute.getAck();
								int hurt = skill.getHurt() + ack;
								int monsterHp = monster.getHp() - hurt;
								monster.setHp(monsterHp);
								if (monsterHp <= 0) {
									ch.writeAndFlush("boss已被消灭，退出副本");
									RpgUtil.ackEnd(user, ch, monster);
									Group group2 = IOsession.userGroupMp.get(user.getGroupId());
									if (group2 != null) {
										List<User> list3 = group2.getList();
										for (User user3 : list3) {
											Channel channel1 = IOsession.userchMp.get(user3);
											if (channel1 != ch) {
												channel1.writeAndFlush(
														user.getNickname() + "消灭了" + monster.getName() + "-你已通关，退出副本");
												RpgUtil.ackEnd(user3, channel1, monster);
											}
											IOsession.ackStatus.put(channel1.remoteAddress(), 0);
										}
									}
									monster.setAliveFlag(false);
									IOsession.ackStatus.put(ch.remoteAddress(), 0);
									// 损耗装备耐久度
									for (Userzb userzb : list1) {
										userzb.setNjd(userzb.getNjd() - 5);
									}
									bossScene = null;// 回收boss场景
								} else {
									ch.writeAndFlush("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余"
											+ user.getMp() + "\n" + "攻击了" + monster.getName() + "-造成" + hurt
											+ "点伤害-怪物血量" + monster.getHp());
									// 损耗装备耐久度
									for (Userzb userzb : list1) {
										userzb.setNjd(userzb.getNjd() - 5);
									}
								}
								break;
							}
						}
						// 蓝量不足
						else {
//							IOsession.ackStatus.put(ch.remoteAddress(), 0);
							ch.writeAndFlush("蓝量不足，请充值");
						}
					}
				}
			}
		} else {
			ch.writeAndFlush("指令错误");
		}
	}

	private void ackFirst(User user, Channel ch, ChannelGroup group, String[] msg, Integer id, String nickname,
			List<Userskill> list, ArrayList<Monster> monsterList, BossScene bossScene) {
//		for (Monster monster : monsterList) {
		// 找到场景内怪物
		int bossId = bossScene.getId();
		Monster monster = monsterList.get(bossId);
		if (msg[1].equals(monster.getName())) {
			if (monster.getHp() > 0) {
//				IOsession.monsterMp.put(ch.remoteAddress(), monster);
				monster.setCountAcker(monster.getCountAcker() + 1);
				// 添加怪物的攻击者
				if (monster.getCountAcker() == 1) {
					ArrayList<User> userList = new ArrayList<>();
					userList.add(user);
					monster.setUserList(userList);
				} else {
					List<User> userList = monster.getUserList();
					userList.add(user);
				}
				// 将怪物指定到用户
				for (Channel channel : group) {
					IOsession.monsterMp.put(channel.remoteAddress(), monster);
				}
				// 找到配置的技能
				for (Userskill userskill : list) {
					String skillId = String.valueOf(userskill.getSkill());
					if (skillId.equals(msg[2])) {
						Skill skill = SkillList.mp.get(msg[2]);
						// 蓝量足够
						if (skill.getMp() <= user.getMp()) {
							// 存储上次使用技能时间
							long currentTimeMillis = System.currentTimeMillis();
							HashMap<String, Long> curSkill = new HashMap<String, Long>();
							curSkill.put(skillId, currentTimeMillis);
							SkillList.cdMp.put(user, curSkill);

							user.setMp(user.getMp() - skill.getMp());
							ch.writeAndFlush("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp());
							// 判断装备是否还有耐久度
							UserAttribute attribute = IOsession.attMp.get(user);
							List<Userzb> list1 = IOsession.userZbMp.get(user);
							for (Userzb userzb : list1) {
								if (userzb.getNjd() <= 0) {
									Zb zb = IOsession.zbMp.get(userzb.getZbid());
									if (zb != null && attribute != null) {
										attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
										userzb.setIsuse(0);
									}
								}
							}
							int ack = attribute.getAck();
							int hurt = skill.getHurt() + ack;

							monster.setHp(monster.getHp() - hurt);
							ch.writeAndFlush("攻击了" + monster.getName() + "-造成" + hurt + "点伤害-怪物血量" + monster.getHp());
							// 损耗装备耐久度
							for (Userzb userzb : list1) {
								userzb.setNjd(userzb.getNjd() - 5);
							}
							// 怪物攻击线程
							if (monster.getCountAcker() == 1) {
								bossScene.setStartTime(System.currentTimeMillis());
								IOsession.monsterThreadPool.execute(new Runnable() {
									@Override
									public void run() {
										while (true) {
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
												break;
											}
											// 挑战时间未到
											else {
												boolean ackstatus = IOsession.ackStatus.containsKey(ch.remoteAddress());
												if (ackstatus) {
													if (IOsession.ackStatus.get(ch.remoteAddress()) == 2) {
														// 怪物存活
														if (monster.getHp() > 0) {
															List<User> userList = monster.getUserList();
//													int hp = user.getHp() - monster.getAck();
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
																	user3.setHp(hp);
																	// 推送攻击消息
																	ch1.writeAndFlush("你受到单体技能伤害：" + monster.getAck()
																			+ "-你的血量剩余：" + hp);
																	if (group2 != null) {
																		List<User> list = group2.getList();
																		for (User user2 : list) {
																			Channel channel = IOsession.userchMp
																					.get(user2);
																			if (channel != ch1) {
																				channel.writeAndFlush(
																						user3.getNickname()
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
																			IOsession.ackStatus
																					.put(channel.remoteAddress(), 0);
																		}
																	}
																	user3.setHp(1000);
																	IOsession.ackStatus.put(ch1.remoteAddress(), 0);
																	// 回收boss场景，怪物线程
																	BossScene bossScene1 = IOsession.userBossMp
																			.get(user.getGroupId());
																	bossScene1 = null;
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
																		Channel channel = IOsession.userchMp.get(user2);
																		int hp = user2.getHp() - monster.getAck();
																		// 血量满足
																		if (hp > 0) {
																			user2.setHp(hp);
																			channel.writeAndFlush(
																					"你受到全体技能伤害：" + monster.getAck()
																							+ "-你的血量剩余：" + hp);
																		} else {
																			Channel ch1 = IOsession.userchMp.get(user2);
																			ch1.writeAndFlush("你已被打死，副本挑战失败，你已被传送出副本");
																			if (group2 != null) {
																				List<User> list = group2.getList();
																				for (User user3 : list) {
																					Channel channel1 = IOsession.userchMp
																							.get(user3);
																					if (channel1 != ch1)
																						channel1.writeAndFlush(user2
																								.getNickname()
																								+ "已被打死，副本挑战失败，你已被传送出副本");
																					IOsession.ackStatus.put(
																							channel1.remoteAddress(),
																							0);
																				}
																			}
																			user2.setHp(1000);
																			IOsession.ackStatus.put(ch1.remoteAddress(),
																					0);
																			// 回收boss场景，怪物线程
																			BossScene bossScene1 = IOsession.userBossMp
																					.get(user.getGroupId());
																			bossScene1 = null;
																			break;
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
															BossScene bossScene1 = IOsession.userBossMp
																	.get(user.getGroupId());
															bossScene1 = null;
															break;
														}
													} else {
														break;
													}
												}
												try {
													Thread.sleep(2000);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
											}
										}
									}
								});
								break;
							}
						}
						// 蓝量不足
						else {
							ch.writeAndFlush("蓝量不足，请充值");
						}
					}

				}
			} else {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				ch.writeAndFlush("怪物不存在");
			}
		} else {
			IOsession.ackStatus.put(ch.remoteAddress(), 0);
			ch.writeAndFlush("怪物不存在");
		}
//		}
	}
}
