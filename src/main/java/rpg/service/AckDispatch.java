package rpg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.area.Area;
import rpg.data.dao.UserskillMapper;
import rpg.data.dao.UserzbMapper;
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
 * 战斗逻辑
 * 
 * @author ljq
 *
 */
@Component("ackDispatch")
public class AckDispatch {

	@Autowired
	private UserskillMapper userskillMapper;
	@Autowired
	private UserzbMapper userzbMapper;
	@Autowired
	private UserService userService;
	@Value("${id}")
	private int spcid;

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
		LinkedList<Monster> monsterList = Area.sceneList.get(id - 1).getMonsterList();
		// 第一次攻击
		if (msg.length == 3) {
			boolean find = false;
			for (Monster monster : monsterList) {
				// 找到场景内怪物
				if (msg[1].equals(monster.getName())) {
					find = true;
					if (monster.getHp() > 0) {
						monster.setCountAcker(monster.getCountAcker() + 1);
						List<Monster> list2 = new ArrayList<>();
						list2.add(monster);
						IOsession.monsterMp.put(ch.remoteAddress(), list2);
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

//									user.setMp(user.getMp() - skill.getMp());
									user.getAndSetMp(user, user.getMp() - skill.getMp());
									// 更新人物buff
									if (skill.getId() != 1) {
										userService.updateUserBuff(user, skill);
									}
									// 更新怪物buff
									userService.updateMonsterBuff(user, skill, monster);
									SendMsg.send(
											"使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp(),ch);
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
									SendMsg.send(
											"攻击了" + monster.getName() + "-造成" + hurt + "点伤害-怪物血量" + monster.getHp(),ch);
									// 损耗装备耐久度
									for (Userzb userzb : list1) {
										userzb.setNjd(userzb.getNjd() - 5);
									}
									// 怪物攻击线程
									if (monster.getCountAcker() == 1) {
										IOsession.monsterThreadPool.execute(new Runnable() {
											@Override
											public void run() {
												while (true) {
													User user2 = IOsession.nameMap.get(user.getNickname());
													Channel newchannel = IOsession.userchMp.get(user2);
													boolean ackstatus = IOsession.ackStatus
															.containsKey(newchannel.remoteAddress());
													if (ackstatus) {
														if (IOsession.ackStatus.get(newchannel.remoteAddress()) == 1) {
															ConcurrentHashMap<Integer, Long> buffTime1 = IOsession.buffTimeMp
																	.get(user2);
															// 检验怪物Buff
															String word1 = userService.checkMonsterBuff(monster, newchannel);
															SendMsg.send(word1, newchannel);
															// 检测用户状态
															if (buffTime1 != null && buffTime1.get(3) != null) {
																SendMsg.send("002" + "-你有最强护盾护体，免疫伤害，你的血量剩余："
																		+ user.getHp(),newchannel);
															} else {
																int monsterAck = monster.getAck() - attribute.getDef();
																if (monsterAck <= 0)
																	monsterAck = 1;
																int hp = user2.getHp() - monsterAck;
																// 怪物存活
																if (monster.getHp() > 0) {
																	if (hp > 0) {
																		user2.setHp(hp);
																		SendMsg.send("002" + "-你受到伤害：" + monsterAck
																				+ "-你的血量剩余：" + hp,newchannel);
																	} else {
																		SendMsg.send("你已被打死",newchannel);
																		user2.setHp(100);
																		IOsession.ackStatus.put(newchannel.remoteAddress(), 0);
																		break;
																	}
																}
																// 怪物死亡
																else {
																	IOsession.ackStatus.put(newchannel.remoteAddress(), 0);
																	break;
																}
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
										});
										break;
									}
								}
								// 蓝量不足
								else {
									SendMsg.send("蓝量不足，请充值",ch);
								}
							}

						}
					} else {
						IOsession.ackStatus.put(ch.remoteAddress(), 0);
						SendMsg.send("怪物不存在",ch);
						break;
					}
				}
			}
			if (!find) {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				SendMsg.send("怪物不存在",ch);
			}
		}
		// 二次及以上攻击
		else if (msg.length == 1) {
			List<Monster> list2 = IOsession.monsterMp.get(ch.remoteAddress());
			Monster monster = list2.get(list2.size()-1);
			// 找到配置的技能
			if (msg[0].equals("esc")) {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				SendMsg.send("成功退出战斗",ch);
			} else if (msg[0].equals("ack")) {
				IOsession.ackStatus.put(ch.remoteAddress(), 0);
				SendMsg.send("指令错误",ch);
			} else {
				if (msg[0].equals("3")) {
					String s = RpgUtil.skillChange(msg[0], user);
					msg[0] = s;
				}
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

//							SendMsg.send("使用了" + skill.getName());
//									user.setMp(user.getMp() - skill.getMp());
									user.getAndSetMp(user, user.getMp() - skill.getMp());
									// 判断特殊技能
									if (skill.getId() == spcid) {
										user.getAndAddHp(user, skill.getHurt());
										SendMsg.send(
												"使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp()
														+ "\n" + "血量恢复" + skill.getHurt() + "-剩余血量" + user.getHp(),ch);
									}

									// 更新人物buff
									else {
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
													attribute.setAck(
															attribute.getAck() - zb.getAck() * userzb.getIsuse());
													userzb.setIsuse(0);
												}
											}
										}
										int ack = attribute.getAck();
										int hurt = skill.getHurt() + ack;
										int monsterHp = monster.getHp() - hurt;
										monster.setHp(monsterHp);
										if (monsterHp <= 0) {
											SendMsg.send("怪物已被消灭！你真棒",ch);
											RpgUtil.ackEnd(user, ch, monster);
											TaskManage.checkTaskComplete(user, monster.getId());
											for (Channel channel : group) {
												if (ch != channel) {
													SendMsg.send(
															user.getNickname() + "消灭了" + monster.getName(),channel);
//												IOsession.ackStatus.put(channel.remoteAddress(), false);
												}
											}
											Monster monster3 = IOsession.moster.get(monster.getId());
											Monster monster2 = (Monster) monster3.clone();
											monsterList.remove(monster);
											monsterList.add(monster2);
//										monster.setAliveFlag(false);
											IOsession.ackStatus.put(ch.remoteAddress(), 0);
											// 损耗装备耐久度
											for (Userzb userzb : list1) {
												userzb.setNjd(userzb.getNjd() - 5);
											}
										} else {
											SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余"
													+ user.getMp() + "\n" + "攻击了" + monster.getName() + "-造成" + hurt
													+ "点伤害-怪物血量" + monster.getHp(),ch);
											// 损耗装备耐久度
											for (Userzb userzb : list1) {
												userzb.setNjd(userzb.getNjd() - 5);
											}
										}
										break;
									}
								}
								// cd未到
								else {
									SendMsg.send("技能冷却中",ch);
								}
							}
							// 技能未使用过
							else {
								// 存储上次使用技能时间
								long currentTimeMillis = System.currentTimeMillis();
								HashMap<String, Long> curSkill = new HashMap<String, Long>();
								curSkill.put(skillId, currentTimeMillis);
								SkillList.cdMp.put(user, curSkill);
//						SendMsg.send("使用了" + skill.getName());
//								user.setMp(user.getMp() - skill.getMp());
								user.getAndSetMp(user, user.getMp() - skill.getMp());
								// 判断特殊技能
								if (skill.getId() == spcid) {
									user.getAndAddHp(user, skill.getHurt());
									SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余"
											+ user.getMp() + "\n" + "血量恢复" + skill.getHurt() + "-剩余血量" + user.getHp(),ch);
								}

								// 更新人物buff
								else {
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
										SendMsg.send("怪物已被消灭！你真棒",ch);
										RpgUtil.ackEnd(user, ch, monster);
										TaskManage.checkTaskComplete(user, monster.getId());
										for (Channel channel : group) {
											if (ch != channel) {
												SendMsg.send(user.getNickname() + "消灭了" + monster.getName(),channel);
//											IOsession.ackStatus.put(channel.remoteAddress(), false);
											}
										}
										Monster monster3 = IOsession.moster.get(monster.getId());
										Monster monster2 = (Monster) monster3.clone();
										monsterList.remove(monster);
										monsterList.add(monster2);
//									monster.setAliveFlag(false);
										IOsession.ackStatus.put(ch.remoteAddress(), 0);
										// 损耗装备耐久度
										for (Userzb userzb : list1) {
											userzb.setNjd(userzb.getNjd() - 5);
										}
									} else {
										SendMsg.send("使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余"
												+ user.getMp() + "\n" + "攻击了" + monster.getName() + "-造成" + hurt
												+ "点伤害-怪物血量" + monster.getHp(),ch);
										// 损耗装备耐久度
										for (Userzb userzb : list1) {
											userzb.setNjd(userzb.getNjd() - 5);
										}
									}
									break;
								}
							}
						}
						// 蓝量不足
						else {
							SendMsg.send("蓝量不足，请充值",ch);
						}
					}
				}
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}
}
