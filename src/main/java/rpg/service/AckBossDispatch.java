package rpg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.area.SceneBossRefresh;
import rpg.configure.InstructionsType;
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
import rpg.task.TaskManage;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;
import rpg.util.UserService;

/**
 * 副本战斗逻辑
 * 
 * @author ljq
 *
 */
@Component("ackBossDispatch")
public class AckBossDispatch {

	private static final int YUN_BUFF = 4;
	private static final int CHANGE_ACK_TARGET_MAX_MSG_LENGTH = 2;
	private static final int FIRST_ACK_MAX_MSG_LENGTH = 3;
	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private UserzbMapper userzbMapper;
	@Autowired
	private UserService userService;
	@Value("${id}")
	private int spcid;
	private ReentrantLock lock = new ReentrantLock();

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
		// LinkedList<Monster> monsterList = Area.sceneList.get(id -
		// 1).getMonsterList();
		BossScene bossScene = IOsession.userBossMp.get(user.getGroupId());
		ArrayList<Monster> monsterList = bossScene.getMonsterList();
		// 第一次攻击
		if (msg.length == FIRST_ACK_MAX_MSG_LENGTH) {
			ackFirst(user, ch, group, msg, id, nickname, list, monsterList, bossScene);
		}
		// 转换攻击目标
		else if (msg.length == CHANGE_ACK_TARGET_MAX_MSG_LENGTH
				&& InstructionsType.CHANGE_ACK_TARGET.getValue().equals(msg[0])) {
			List<Monster> list2 = IOsession.monsterMp.get(ch.remoteAddress());
			int index = -1;
			for (int i = 0; i < list2.size(); i++) {
				Monster monster = list2.get(i);
				if (monster.getName().equals(msg[1])) {
					index = i;
					break;
				}
			}
			if (index != -1) {
				Monster monster = list2.get(index);
				list2.remove(monster);
				list2.add(0, monster);
				SendMsg.send("转换攻击目标-" + monster.getName() + "-成功", ch);
			} else {
				SendMsg.send("指令错误", ch);
			}
		}
		// 二次及以上攻击
		else if (msg.length == 1) {
			// Monster monster = IOsession.monsterMp.get(ch.remoteAddress());
			List<Monster> list2 = IOsession.monsterMp.get(ch.remoteAddress());
			List<Monster> linkedList = IOsession.monsterMp.get(ch.remoteAddress());
			// Monster monster = list2.get(0);
			// if (monster != null) {
			ConcurrentHashMap<Integer, Long> buffTime2 = IOsession.buffTimeMp.get(user);
			// 找到配置的技能
			if (msg[0].equals(InstructionsType.ESC.getValue())) {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				SendMsg.send("成功退出战斗", ch);
			} else if (msg[0].equals(InstructionsType.ACK.getValue())) {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				SendMsg.send("指令错误", ch);
			} else if (buffTime2 != null && buffTime2.get(YUN_BUFF) != null) {
				SendMsg.send("你被打晕了，无法进行攻击", ch);
			} else {
				if (msg[0].equals(InstructionsType.SKILL_KEY_1.getValue())
						|| msg[0].equals(InstructionsType.SKILL_KEY_3.getValue())) {
					if (msg[0].equals(InstructionsType.SKILL_KEY_3.getValue())) {
						String s = RpgUtil.skillChange(msg[0], user);
						msg[0] = s;
					}
					for (Userskill userskill : list) {
						String skillId = String.valueOf(userskill.getSkill());
						if (skillId.equals(msg[0])) {
							Skill skill = SkillList.mp.get(msg[0]);
							// 获取当前时间毫秒值
							long millis = System.currentTimeMillis();
							HashMap<String, Long> map = SkillList.cdMp.get(user);
							if (map != null) {
								Long lastmillis = map.get(skillId);

								// 蓝量满足
								if (skill.getMp() <= user.getMp()) {
									if (lastmillis != null) {
										// 技能cd满足
										if (millis - lastmillis >= skill.getCd()) {
											// 存储上次使用技能时间
											long currentTimeMillis = System.currentTimeMillis();
											HashMap<String, Long> curSkill = new HashMap<String, Long>(500);
											curSkill.put(skillId, currentTimeMillis);
											SkillList.cdMp.put(user, curSkill);

											// SendMsg.send("使用了" + skill.getName());
											// user.setMp(user.getMp() - skill.getMp());
											user.getAndSetMp(user, user.getMp() - skill.getMp());
											// 判断特殊技能
											if (skill.getId() == spcid) {
												Group group2 = IOsession.userGroupMp.get(user.getGroupId());
												if (group2 != null) {
													List<User> list3 = group2.getList();
													for (User user3 : list3) {
														user3.getAndAddHp(user3, skill.getHurt());
														if (!user3.getNickname().equals(user.getNickname())) {
															Channel channel = IOsession.userchMp.get(user3);
															SendMsg.send(user.getNickname() + "对你使用了" + skill.getName()
																	+ "血量恢复" + skill.getHurt() + "-剩余血量"
																	+ user3.getHp(), channel);
														} else {
															SendMsg.send(
																	"使用了" + skill.getName() + "-蓝量消耗" + skill.getMp()
																			+ "-剩余" + user.getMp() + "\n" + "血量恢复"
																			+ skill.getHurt() + "-剩余血量" + user.getHp(),
																	ch);
														}
													}
												}
												break;
											}
											// 更新人物buff
											else {
												for (int index = 0; index < list2.size(); index++) {
													Monster monster = list2.get(index);
													if (skill.getId() != 1) {
														userService.updateUserBuff(user, skill);
													}
													// 更新怪物buff
													userService.updateMonsterBuff(user, skill, monster);
													// 判断装备是否还有耐久度
													UserAttribute attribute = IOsession.attMp.get(user);
													List<Userzb> list1 = IOsession.userZbMp.get(user);
													for (Userzb userzb : list1) {
														if (userzb.getNjd() <= 0) {
															Zb zb = IOsession.zbMp.get(userzb.getZbid());
															if (zb != null && attribute != null) {
																attribute.setAck(attribute.getAck()
																		- zb.getAck() * userzb.getIsuse());
																userzb.setIsuse(0);
															}
														}
													}
													if (skill.getId() == 7) {
														SendMsg.send(skill.getName() + "对" + monster.getName()
																+ "进行攻击-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp(),
																ch);
														break;
													}
													int ack = attribute.getAck();
													int hurt = skill.getHurt() + ack;
													int monsterHp = monster.getHp() - hurt;
													monster.setHp(monsterHp);
													if (monsterHp <= 0) {
														if (linkedList.size() != 1) {
															linkedList.remove(monster);
															SendMsg.send("消灭了" + monster.getName(), ch);
															monster = null;
															// 更新怪物列表
															Group group2 = IOsession.userGroupMp.get(user.getGroupId());
															if (group2 != null) {
																List<User> list3 = group2.getList();
																for (User user3 : list3) {
																	Channel channel = IOsession.userchMp.get(user3);
																	IOsession.monsterMp.put(channel.remoteAddress(),
																			linkedList);
																}
															}
															index--;
														}
														// 产生新的boss
														else {
															if (bossScene.getLayer() - 1 > bossScene.getId()) {
																bossScene.setId(bossScene.getId() + 1);
																// Monster monster2 =
																// monsterList.get(bossScene.getId());
																Map<Integer, Integer> struct = bossScene.getStruct();
																Integer csid1 = struct.get(bossScene.getId());
																Integer csid2 = struct.get(bossScene.getId() - 1);
																Group group2 = IOsession.userGroupMp
																		.get(user.getGroupId());
																// 将怪物指定用户
																if (group2 != null) {
																	List<User> list3 = group2.getList();
																	for (User user3 : list3) {
																		LinkedList<Monster> linkedList2 = new LinkedList<>();
																		for (int i = csid2 + 1; i <= csid1; i++) {
																			Monster monster2 = monsterList.get(i);
																			if (group2 != null) {
																				List<User> list31 = group2.getList();
																				ArrayList<User> userList1 = new ArrayList<>();
																				for (User user31 : list31) {
																					userList1.add(user31);
																				}
																				monster2.setUserList(userList1);
																			}
																			// monster = null;
																			// monster = monster2;
																			linkedList2.add(monster2);
																		}
																		linkedList = null;
																		linkedList = linkedList2;
																		Channel channel1 = IOsession.userchMp
																				.get(user3);
																		IOsession.monsterMp.put(
																				channel1.remoteAddress(), linkedList);
																		String word1 = "";
																		for (Monster monster21 : linkedList) {
																			word1 += monster21.getName() + "-血量:"
																					+ monster21.getHp() + "攻击力:"
																					+ monster21.getAck() + "\n";
																		}
																		SendMsg.send("boss已被消灭,新的Boss出现\n" + word1,
																				channel1);
																	}
																}
															} else {
																try {
																	lock.lock();
																	if (bossScene != null) {
																		SendMsg.send("boss已全被消灭，退出副本", ch);
																		Group group2 = IOsession.userGroupMp
																				.get(user.getGroupId());
																		RpgUtil.ackEnd(user, ch, monster);
																		RpgUtil.bossEndAward(user, ch, bossScene);
																		TaskManage.checkTaskComplete(user,
																				bossScene.getSceneid());
																		if (group2 != null) {
																			List<User> list3 = group2.getList();
																			for (User user3 : list3) {
																				Channel channel1 = IOsession.userchMp
																						.get(user3);
																				if (channel1 != ch) {
																					SendMsg.send(
																							user.getNickname() + "消灭了"
																									+ monster.getName()
																									+ "-你已通关，退出副本",
																							channel1);
																					RpgUtil.ackEnd(user3, channel1,
																							monster);
																					TaskManage.checkTaskComplete(user3,
																							bossScene.getSceneid());
																				}
																				IOsession.ackStatus.put(
																						channel1.remoteAddress(), 0);
																			}
																		}
																		monster.setAliveFlag(false);
																		IOsession.ackStatus.put(ch.remoteAddress(), 0);
																		// 损耗装备耐久度
																		for (Userzb userzb : list1) {
																			userzb.setNjd(userzb.getNjd() - 5);
																		}
																		removeUserlist(user, bossScene);
																		// 回收boss场景
																		bossScene = null;
																		IOsession.userBossMp.remove(user.getGroupId());
																	}
																} finally {
																	lock.unlock();
																}
															}
														}
													} else {
														if (index == 0) {
															SendMsg.send("使用了" + skill.getName() + "-蓝量消耗"
																	+ skill.getMp() + "-剩余" + user.getMp(), ch);
														}
														SendMsg.send("攻击了" + monster.getName() + "-造成" + hurt
																+ "点伤害-怪物血量" + monster.getHp(), ch);
														// 损耗装备耐久度
														for (Userzb userzb : list1) {
															userzb.setNjd(userzb.getNjd() - 5);
														}
													}
													// break;
													if (skill.getId() != 6) {
														break;
													}
												}
											}
											break;
										}
										// cd未到
										else {
											SendMsg.send("技能冷却中", ch);
										}
									}
									// 技能未使用过
									else {
										// 存储上次使用技能时间
										long currentTimeMillis = System.currentTimeMillis();
										HashMap<String, Long> curSkill = new HashMap<String, Long>(500);
										curSkill.put(skillId, currentTimeMillis);
										SkillList.cdMp.put(user, curSkill);
										// SendMsg.send("使用了" + skill.getName());
										// user.setMp(user.getMp() - skill.getMp());
										user.getAndSetMp(user, user.getMp() - skill.getMp());
										// 判断特殊技能
										if (skill.getId() == spcid) {
											Group group2 = IOsession.userGroupMp.get(user.getGroupId());
											if (group2 != null) {
												List<User> list3 = group2.getList();
												for (User user3 : list3) {
													user3.getAndAddHp(user3, skill.getHurt());
													if (!user3.getNickname().equals(user.getNickname())) {
														Channel channel = IOsession.userchMp.get(user3);
														SendMsg.send(
																user.getNickname() + "对你使用了" + skill.getName() + "血量恢复"
																		+ skill.getHurt() + "-剩余血量" + user3.getHp(),
																channel);
													} else {
														SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp()
																+ "-剩余" + user.getMp() + "\n" + "血量恢复" + skill.getHurt()
																+ "-剩余血量" + user.getHp(), ch);
													}
												}
											}
										}
										// 更新人物buff
										else {
											for (int index = 0; index < list2.size(); index++) {
												Monster monster = list2.get(index);
												if (skill.getId() != 1) {
													userService.updateUserBuff(user, skill);
												}
												// 更新怪物buff
												userService.updateMonsterBuff(user, skill, monster);
												// 判断装备是否还有耐久度
												UserAttribute attribute = IOsession.attMp.get(user);
												List<Userzb> list1 = IOsession.userZbMp.get(user);
												for (Userzb userzb : list1) {
													if (userzb.getNjd() <= 0) {
														Zb zb = IOsession.zbMp.get(userzb.getZbid());
														if (zb != null && attribute != null) {
															attribute.setAck(attribute.getAck()
																	- zb.getAck() * userzb.getIsuse());
															userzb.setIsuse(0);
														}
													}
												}
												if (skill.getId() == 7) {
													SendMsg.send(skill.getName() + "对" + monster.getName() + "进行攻击-蓝量消耗"
															+ skill.getMp() + "-剩余" + user.getMp(), ch);
													break;
												}
												int ack = attribute.getAck();
												int hurt = skill.getHurt() + ack;
												int monsterHp = monster.getHp() - hurt;
												monster.setHp(monsterHp);
												if (monsterHp <= 0) {
													if (linkedList.size() != 1) {
														linkedList.remove(monster);
														SendMsg.send("消灭了" + monster.getName(), ch);
														monster = null;
														// 更新怪物列表
														Group group2 = IOsession.userGroupMp.get(user.getGroupId());
														if (group2 != null) {
															List<User> list3 = group2.getList();
															for (User user3 : list3) {
																Channel channel = IOsession.userchMp.get(user3);
																IOsession.monsterMp.put(channel.remoteAddress(),
																		linkedList);
															}
														}
														index--;
													}
													// 产生新的boss
													else {
														if (bossScene.getLayer() - 1 > bossScene.getId()) {
															bossScene.setId(bossScene.getId() + 1);
															// Monster monster2 = monsterList.get(bossScene.getId());
															Map<Integer, Integer> struct = bossScene.getStruct();
															Integer csid1 = struct.get(bossScene.getId());
															Integer csid2 = struct.get(bossScene.getId() - 1);
															Group group2 = IOsession.userGroupMp.get(user.getGroupId());
															// 将怪物指定用户
															if (group2 != null) {
																List<User> list3 = group2.getList();
																for (User user3 : list3) {
																	LinkedList<Monster> linkedList2 = new LinkedList<>();
																	for (int i = csid2 + 1; i <= csid1; i++) {
																		Monster monster2 = monsterList.get(i);
																		if (group2 != null) {
																			List<User> list31 = group2.getList();
																			ArrayList<User> userList1 = new ArrayList<>();
																			for (User user31 : list31) {
																				userList1.add(user31);
																			}
																			monster2.setUserList(userList1);
																		}
																		// monster = null;
																		// monster = monster2;
																		linkedList2.add(monster2);
																	}
																	linkedList = null;
																	linkedList = linkedList2;
																	Channel channel1 = IOsession.userchMp.get(user3);
																	IOsession.monsterMp.put(channel1.remoteAddress(),
																			linkedList);
																	String word1 = "";
																	for (Monster monster21 : linkedList) {
																		word1 += monster21.getName() + "-血量:"
																				+ monster21.getHp() + "攻击力:"
																				+ monster21.getAck() + "\n";
																	}
																	SendMsg.send("boss已被消灭,新的Boss出现\n" + word1,
																			channel1);
																}
															}
														} else {
															try {
																lock.lock();
																if (bossScene != null) {
																	SendMsg.send("boss已全被消灭，退出副本", ch);
																	RpgUtil.ackEnd(user, ch, monster);
																	RpgUtil.bossEndAward(user, ch, bossScene);
																	TaskManage.checkTaskComplete(user,
																			bossScene.getSceneid());
																	Group group2 = IOsession.userGroupMp
																			.get(user.getGroupId());
																	if (group2 != null) {
																		List<User> list3 = group2.getList();
																		for (User user3 : list3) {
																			Channel channel1 = IOsession.userchMp
																					.get(user3);
																			if (channel1 != ch) {
																				SendMsg.send(
																						user.getNickname() + "消灭了"
																								+ monster.getName()
																								+ "-你已通关，退出副本" + "\n",
																						channel1);
																				RpgUtil.ackEnd(user3, channel1,
																						monster);
																				TaskManage.checkTaskComplete(user3,
																						bossScene.getSceneid());
																			}
																			IOsession.ackStatus
																					.put(channel1.remoteAddress(), 0);
																		}
																	}
																	monster.setAliveFlag(false);
																	IOsession.ackStatus.put(ch.remoteAddress(), 0);
																	// 损耗装备耐久度
																	for (Userzb userzb : list1) {
																		userzb.setNjd(userzb.getNjd() - 5);
																	}
																	removeUserlist(user, bossScene);
																	// 回收boss场景
																	bossScene = null;
																	IOsession.userBossMp.remove(user.getGroupId());
																}
															} finally {
																lock.unlock();
															}
														}
													}
												} else {
													if (index == 0) {
														SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp()
																+ "-剩余" + user.getMp(), ch);
													}
													SendMsg.send("攻击了" + monster.getName() + "-造成" + hurt + "点伤害-怪物血量"
															+ monster.getHp(), ch);
													// 损耗装备耐久度
													for (Userzb userzb : list1) {
														userzb.setNjd(userzb.getNjd() - 5);
													}
												}
												// break;
												if (skill.getId() != 6) {
													break;
												}
											}
										}
									}
									break;
								}
								// 蓝量不足
								else {
									// IOsession.ackStatus.put(ch.remoteAddress(), 0);
									SendMsg.send("蓝量不足，请充值", ch);
								}
							} else {
								SendMsg.send("指令错误", ch);
							}
						}
					}
				} else {
					SendMsg.send("指令错误", ch);
				}
			}
			// }
			// else {
			// SendMsg.send("指令错误",ch);
			// }
		} else {
			SendMsg.send("指令错误", ch);
		}
	}

	public static void removeUserlist(User user, BossScene bossScene) {
		ArrayList<Monster> monsterList1 = bossScene.getMonsterList();
		// 找到场景内怪物
		for (int i = 0; i < monsterList1.size(); i++) {
			Monster monster1 = monsterList1.get(i);
			if (monster1 != null) {
				List<User> userList = monster1.getUserList();
				userList.remove(user);
			}
		}
	}

	private void ackFirst(User user, Channel ch, ChannelGroup group, String[] msg, Integer id, String nickname,
			List<Userskill> list, ArrayList<Monster> monsterList, BossScene bossScene) {
		// for (Monster monster : monsterList) {
		// 找到场景内怪物
		int bossId = bossScene.getId();
		Monster monster = monsterList.get(bossId);
		if (msg[1].equals(monster.getName())) {
			if (monster.getHp() > 0) {
				// IOsession.monsterMp.put(ch.remoteAddress(), monster);
				monster.setCountAcker(monster.getCountAcker() + 1);
				// 添加怪物的攻击者
				if (monster.getCountAcker() == 1) {
					LinkedList<User> userList = new LinkedList<>();
					userList.add(user);
					monster.setUserList(userList);
				} else {
					List<User> userList = monster.getUserList();
					userList.add(user);
				}
				// 将怪物指定到用户
				for (Channel channel : group) {
					List<Monster> list2 = new ArrayList<>();
					list2.add(monster);
					IOsession.monsterMp.put(channel.remoteAddress(), list2);
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
							HashMap<String, Long> curSkill = new HashMap<String, Long>(500);
							curSkill.put(skillId, currentTimeMillis);
							SkillList.cdMp.put(user, curSkill);

							// user.setMp(user.getMp() - skill.getMp());
							user.getAndSetMp(user, user.getMp() - skill.getMp());
							// 更新人物buff
							if (skill.getId() != 1) {
								userService.updateUserBuff(user, skill);
							}
							// 更新怪物buff
							userService.updateMonsterBuff(user, skill, monster);
							SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp(), ch);
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
							SendMsg.send("攻击了" + monster.getName() + "-造成" + hurt + "点伤害-怪物血量" + monster.getHp(), ch);
							// 损耗装备耐久度
							for (Userzb userzb : list1) {
								userzb.setNjd(userzb.getNjd() - 5);
							}
							// 怪物攻击线程
							if (monster.getCountAcker() == 1) {
								bossScene.setStartTime(System.currentTimeMillis());
								Group firstGroup = IOsession.userGroupMp.get(user.getGroupId());
								IOsession.monsterThreadPool.execute(
										new SceneBossRefresh(userService, user, bossScene, ch, firstGroup, attribute));
								// IOsession.monsterThreadPool.execute(new Runnable() {
								// @Override
								// public void run() {
								// while (true) {
								// // 达到挑战时间
								// if (System.currentTimeMillis() - bossScene.getStartTime() >= bossScene
								// .getLastedTime()) {
								// Group group2 = IOsession.userGroupMp.get(user.getGroupId());
								// if (group2 != null) {
								// List<User> list2 = group2.getList();
								// for (User user2 : list2) {
								// Channel channel = IOsession.userchMp.get(user2);
								// SendMsg.send("已达挑战时间上限，副本挑战失败，自动退出副本");
								// IOsession.ackStatus.put(channel.remoteAddress(), 0);
								// }
								// }
								// // 回收boss场景，怪物线程
								// BossScene bossScene1 = IOsession.userBossMp.get(user.getGroupId());
								// bossScene1 = null;
								// IOsession.userBossMp.remove(user.getGroupId());
								// break;
								// }
								// // 挑战时间未到
								// else {
								//// Monster monster = IOsession.monsterMp.get(ch.remoteAddress());
								// boolean ackstatus = IOsession.ackStatus.containsKey(ch.remoteAddress());
								// if (ackstatus) {
								// if (IOsession.ackStatus.get(ch.remoteAddress()) == 2) {
								// HashMap<Integer, Long> buffTime1 = IOsession.buffTimeMp
								// .get(user);
								// // 检验怪物Buff
								// String word1 = userService.checkMonsterBuff(monster, ch);
								// // 检测用户状态
								// if (buffTime1!=null&&buffTime1.get(3) != null) {
								// SendMsg.send(
								// word1 + "-你有最强护盾护体，免疫伤害，你的血量剩余：" + user.getHp());
								// }else {
								// // 怪物存活
								// if (monster.getHp() > 0) {
								// List<User> userList = monster.getUserList();
								//// int hp = user.getHp() - monster.getAck();
								// Random random = new Random();
								// int monsterSkillId = random.nextInt(2);
								// int ackuserId = random.nextInt(userList.size());
								// // 第一种攻击
								// if (monsterSkillId == 0) {
								// User user3 = userList.get(ackuserId);
								// int hp = user3.getHp() - monster.getAck();
								// Channel ch1 = IOsession.userchMp.get(user3);
								// Group group2 = IOsession.userGroupMp
								// .get(user3.getGroupId());
								// if (hp > 0) {
								// // 选定具体技能
								// List<Integer> skillList = monster
								// .getSkillList();
								// int randomId = random.nextInt(skillList.size());
								// Integer integer = skillList.get(randomId);
								// Skill skill2 = SkillList.mp
								// .get(String.valueOf(integer));
								// userService.updateUserBuff(user3, skill2);
								// Buff getBuff = IOsession.buffMp.get(
								// Integer.valueOf(skill2.getEffect()));
								// user3.setHp(hp);
								// // 推送攻击消息
								// ch1.writeAndFlush(word1 + "-你受到单体技能"
								// + skill2.getName() + "伤害："
								// + monster.getAck() + "-你的血量剩余：" + hp
								// + "你产生了" + getBuff.getName());
								// if (group2 != null) {
								// List<User> list = group2.getList();
								// for (User user2 : list) {
								// Channel channel = IOsession.userchMp
								// .get(user2);
								// if (channel != ch1) {
								// SendMsg.send(word1 + "-"
								// + user3.getNickname()
								// + "受到单体技能伤害："
								// + monster.getAck()
								// + "-血量剩余：" + hp);
								// }
								// }
								// }
								// } else {
								// ch1.writeAndFlush("你已被打死，副本挑战失败，你已被传送出副本");
								// if (group2 != null) {
								// List<User> list = group2.getList();
								// for (User user2 : list) {
								// Channel channel = IOsession.userchMp
								// .get(user2);
								// if (channel != ch1)
								// SendMsg.send(user3
								// .getNickname()
								// + "已被打死，副本挑战失败，你已被传送出副本");
								// IOsession.ackStatus.put(
								// channel.remoteAddress(), 0);
								// }
								// }
								// user3.setHp(1000);
								// IOsession.ackStatus.put(ch1.remoteAddress(), 0);
								// // 回收boss场景，怪物线程
								// BossScene bossScene1 = IOsession.userBossMp
								// .get(user.getGroupId());
								// bossScene1 = null;
								// IOsession.userBossMp.remove(user.getGroupId());
								// break;
								// }
								// }
								// // 第二种攻击
								// else {
								// Group group2 = IOsession.userGroupMp
								// .get(user.getGroupId());
								// if (group2 != null) {
								// List<User> list2 = group2.getList();
								// for (User user2 : list2) {
								// Channel channel = IOsession.userchMp
								// .get(user2);
								// int hp = user2.getHp() - monster.getAck();
								// // 血量满足
								// if (hp > 0) {
								// user2.setHp(hp);
								// SendMsg.send(
								// word1 + "-你受到全体技能伤害："
								// + monster.getAck()
								// + "-你的血量剩余：" + hp);
								// } else {
								// Channel ch1 = IOsession.userchMp
								// .get(user2);
								// ch1.writeAndFlush(
								// "你已被打死，副本挑战失败，你已被传送出副本");
								// if (group2 != null) {
								// List<User> list = group2.getList();
								// for (User user3 : list) {
								// Channel channel1 = IOsession.userchMp
								// .get(user3);
								// if (channel1 != ch1)
								// SendMsg.send(user2
								// .getNickname()
								// + "已被打死，副本挑战失败，你已被传送出副本");
								// IOsession.ackStatus.put(channel1
								// .remoteAddress(), 0);
								// }
								// }
								// user2.setHp(1000);
								// IOsession.ackStatus
								// .put(ch1.remoteAddress(), 0);
								// // 回收boss场景，怪物线程
								// BossScene bossScene1 = IOsession.userBossMp
								// .get(user.getGroupId());
								// bossScene1 = null;
								// IOsession.userBossMp
								// .remove(user.getGroupId());
								// break;
								// }
								// }
								// }
								// }
								// }
								// // 怪物死亡
								// else {
								// Group group2 = IOsession.userGroupMp
								// .get(user.getGroupId());
								// if (group2 != null) {
								// List<User> list2 = group2.getList();
								// for (User user2 : list2) {
								// Channel channel = IOsession.userchMp.get(user2);
								// IOsession.ackStatus.put(channel.remoteAddress(),
								// 0);
								// }
								// }
								// // 回收boss场景，怪物线程
								// BossScene bossScene1 = IOsession.userBossMp
								// .get(user.getGroupId());
								// bossScene1 = null;
								// IOsession.userBossMp.remove(user.getGroupId());
								// break;
								// }
								// }
								// }else {
								// break;
								// }
								// }
								// try {
								// Thread.sleep(5000);
								// } catch (InterruptedException e) {
								// e.printStackTrace();
								// }
								// }
								// }
								// }
								// });
								break;
							}
						}
						// 蓝量不足
						else {
							SendMsg.send("蓝量不足，请充值", ch);
						}
					}

				}
			} else {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				SendMsg.send("怪物不存在", ch);
			}
		} else {
			IOsession.ackStatus.put(ch.remoteAddress(), 0);
			SendMsg.send("怪物不存在", ch);
		}
		// }
	}
}
