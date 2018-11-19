package rpg.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * 战斗逻辑
 * 
 * @author ljq
 *
 */
@Component("AckBossDispatch")
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
		LinkedList<Monster> monsterList = Area.sceneList.get(id - 1).getMonsterList();
		ackFirst(user, ch, group, msg, id, nickname, list,monsterList);
		// 第一次攻击
		if (msg.length == 3) {
			ackFirst(user, ch, group, msg, id, nickname, list,monsterList);
		}
		// 二次及以上攻击
		else if (msg.length == 1) {
			Monster monster = IOsession.monsterMp.get(ch.remoteAddress());
			// 找到配置的技能
			if (msg[0].equals("esc")) {
				IOsession.ackStatus.put(ch.remoteAddress(), false);
				ch.writeAndFlush("成功退出战斗");
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
										ch.writeAndFlush("怪物已被消灭！你真棒");
										for (Channel channel : group) {
											if (ch != channel) {
												channel.writeAndFlush(user.getNickname() + "消灭了" + monster.getName());
//												IOsession.ackStatus.put(channel.remoteAddress(), false);
											}
										}
										monster.setAliveFlag(false);
										IOsession.ackStatus.put(ch.remoteAddress(), false);
										// 损耗装备耐久度
										for (Userzb userzb : list1) {
											userzb.setNjd(userzb.getNjd() - 5);
										}
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
									ch.writeAndFlush("怪物已被消灭！你真棒");
									for (Channel channel : group) {
										if (ch != channel) {
											channel.writeAndFlush(user.getNickname() + "消灭了" + monster.getName());
//											IOsession.ackStatus.put(channel.remoteAddress(), false);
										}
									}
									monster.setAliveFlag(false);
									IOsession.ackStatus.put(ch.remoteAddress(), false);
									// 损耗装备耐久度
									for (Userzb userzb : list1) {
										userzb.setNjd(userzb.getNjd() - 5);
									}
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
			List<Userskill> list, LinkedList<Monster> monsterList) {
		for (Monster monster : monsterList) {
			// 找到场景内怪物
			if (msg[1].equals(monster.getName())) {
				if (monster.getHp() > 0) {
					IOsession.monsterMp.put(ch.remoteAddress(), monster);
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
								ch.writeAndFlush(
										"使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余" + user.getMp());
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
								ch.writeAndFlush(
										"攻击了" + monster.getName() + "-造成" + hurt + "点伤害-怪物血量" + monster.getHp());
								// 损耗装备耐久度
								for (Userzb userzb : list1) {
									userzb.setNjd(userzb.getNjd() - 5);
								}
								// 怪物攻击线程
								IOsession.monsterThreadPool.execute(new Runnable() {
									@Override
									public void run() {
										while (true) {
											boolean ackstatus = IOsession.ackStatus.containsKey(ch.remoteAddress());
											if (ackstatus) {
												if (IOsession.ackStatus.get(ch.remoteAddress())) {
													int hp = user.getHp() - monster.getAck();
													// 怪物存活
													if (monster.getHp() > 0) {
														if (hp > 0) {
															user.setHp(hp);
															ch.writeAndFlush(
																	"你受到伤害：" + monster.getAck() + "-你的血量剩余：" + hp);
														} else {
															ch.writeAndFlush("你已被打死");
															user.setHp(100);
															IOsession.ackStatus.put(ch.remoteAddress(), false);
															break;
														}
													}
													// 怪物死亡
													else {
														IOsession.ackStatus.put(ch.remoteAddress(), false);
														break;
													}
												} else {
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
								});
								break;
							}
							// 蓝量不足
							else {
								ch.writeAndFlush("蓝量不足，请充值");
							}
						}

					}
				} else {
					ch.writeAndFlush("怪物不存在");
				}
			}
		}
	}
}
