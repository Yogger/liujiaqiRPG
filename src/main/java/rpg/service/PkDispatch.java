package rpg.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userskill;
import rpg.pojo.Userzb;
import rpg.session.IOsession;
import rpg.skill.SkillList;
import rpg.util.UserService;

@Component
public class PkDispatch {

	@Autowired
	private UserService userService;

	public void pk(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		List<Userskill> skillList = userService.getSkillList(user);
		if (msg.length == 3) {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[1].equals(user2.getNickname())) {
						if (user2.getAreaid() == user.getAreaid()) {
							if (user2.getAreaid()!=1) {
								for (Userskill userskill : skillList) {
									String skillId = String.valueOf(userskill.getSkill());
									if (skillId.equals(msg[2])) {
										Skill skill = SkillList.mp.get(msg[2]);
										// 蓝量满足
										if (skill.getMp() <= user.getMp()) {
											// 技能cd满足
											if (userService.cdStatus(user, skillId, skill) == 1) {
												// 存储上次使用技能时间
												userService.saveLastCdTime(user, skillId);
												// 消耗蓝量
												user.setMp(user.getMp() - skill.getMp());
												// 判断装备是否还有耐久度
												List<Userzb> list1 = IOsession.userZbMp.get(user);
												UserAttribute attribute = IOsession.attMp.get(user);
												userService.checkNjd(user, attribute);
												int ack = attribute.getAck();
												int hurt = skill.getHurt() + ack;
												int userHp = user2.getHp() - hurt;
												user2.setHp(userHp);
												Channel channel = IOsession.userchMp.get(user2);
												if (userHp <= 0) {
													ch.writeAndFlush("你已将" + user2.getNickname() + "打死！");
													user2.setHp(1000);
													user2.setAreaid(1);
													channel.writeAndFlush(
															"你已被" + user.getNickname() + "打死!" + "-你被传送至起始之地");
													// 损耗装备耐久度
													for (Userzb userzb : list1) {
														userzb.setNjd(userzb.getNjd() - 5);
													}
												} else {
													ch.writeAndFlush(
															"使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余MP"
																	+ user.getMp() + "\n" + "攻击了" + user2.getNickname()
																	+ "-造成" + hurt + "点伤害-对方血量" + user2.getHp());
													channel.writeAndFlush("你受到了"+user.getNickname()+"的技能"+skill.getName()+"-造成" + hurt + "点伤害-你的血量" + user2.getHp());
													// 损耗装备耐久度
													for (Userzb userzb : list1) {
														userzb.setNjd(userzb.getNjd() - 5);
													}
												}
											}
											// cd未到
											else if (userService.cdStatus(user, skillId, skill) == 2) {
												ch.writeAndFlush("技能冷却中");
											}
											// 技能未被使用
											else {
												// 存储上次使用技能时间
												userService.saveLastCdTime(user, skillId);
												// 消耗蓝量
												user.setMp(user.getMp() - skill.getMp());
												// 判断装备是否还有耐久度
												List<Userzb> list1 = IOsession.userZbMp.get(user);
												UserAttribute attribute = IOsession.attMp.get(user);
												userService.checkNjd(user, attribute);
												int ack = attribute.getAck();
												int hurt = skill.getHurt() + ack;
												int userHp = user2.getHp() - hurt;
												user2.setHp(userHp);
												Channel channel = IOsession.userchMp.get(user2);
												if (userHp <= 0) {
													ch.writeAndFlush("你已将" + user2.getNickname() + "打死！");
													user2.setHp(1000);
													user2.setAreaid(1);
													channel.writeAndFlush(
															"你已被" + user.getNickname() + "打死!" + "-你被传送至起始之地");
													// 损耗装备耐久度
													for (Userzb userzb : list1) {
														userzb.setNjd(userzb.getNjd() - 5);
													}
												} else {
													ch.writeAndFlush(
															"使用了" + skill.getName() + "-蓝量消耗" + skill.getMp() + "-剩余MP"
																	+ user.getMp() + "\n" + "攻击了" + user2.getNickname()
																	+ "-造成" + hurt + "点伤害-对方血量" + user2.getHp());
													channel.writeAndFlush("你受到了"+user.getNickname()+"的技能"+skill.getName()+"-造成" + hurt + "点伤害-你的血量" + user2.getHp());
													// 损耗装备耐久度
													for (Userzb userzb : list1) {
														userzb.setNjd(userzb.getNjd() - 5);
													}
												}
											}
										} else {
											ch.writeAndFlush("蓝量不足，请充值");
										}
									}
								}
							} else {
								ch.writeAndFlush("该场景不允许pk");
							}
						} else {
							ch.writeAndFlush("玩家不在当前场景");
						}
						break;
					}
				}
			}
		} else {
			ch.writeAndFlush("指令错误");
		}
	}
}