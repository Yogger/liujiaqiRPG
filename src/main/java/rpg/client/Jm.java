package rpg.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.output.ThresholdingOutputStream;

import io.netty.channel.Channel;
import rpg.util.SendMsg;

/**
 * 界面
 * @author ljq
 *
 */
public class Jm extends JFrame implements KeyListener {
	JPanel jPanel1, jPanel2; // 三块面板，两块是按钮，一块是输出台
	JScrollPane jPanel4, jPanel5, jPanel6, jPanel7;

	JButton jButton1, jButton2, jButton3, jButton4, jButton5, jButton6, jButton7; // 两个按钮，一个清屏，一个展示

	JTextField txt;

	static JTextArea jTextArea, jTextArea2, jTextArea3, jTextArea4; // 输出台

	Channel channel;

	public Jm(Channel channel) {
		this.channel = channel;

		initComp();
		String msg="请选择指令\nlogin、登陆 regist、注册\n格式：login username psw\n格式：regist username psw psw roletype"
				+ "\n1-战士 2-法师 3-牧师 4-召唤师";
		printMsg(msg, jTextArea);
	}

	public void initComp() {

		jPanel1 = new JPanel();
		jPanel2 = new JPanel();
		// 两个按钮

		jButton1 = new JButton("清屏");
		jButton2 = new JButton("展示装备");
		jButton3 = new JButton("商店");
		jButton4 = new JButton("使用蓝药");
		jButton5 = new JButton("攻击史莱姆");
		jButton6 = new JButton("技能按钮1");
		jButton7 = new JButton("技能按钮2");
		txt = new JTextField();
		// 输出台

		jTextArea = new JTextArea(15, 30);
		Font x = new Font("宋体", 1, 17);
		jTextArea.setFont(x);
		jTextArea.setEditable(false);

		jTextArea2 = new JTextArea(10, 30);
		jTextArea2.setFont(x);
		jTextArea2.setEditable(false);

		jTextArea3 = new JTextArea(8, 30);
		jTextArea3.setFont(x);
		jTextArea3.setEditable(false);

		jTextArea4 = new JTextArea(8, 30);
		jTextArea4.setFont(x);
		jTextArea4.setEditable(false);
		// 设置布局

		this.setLayout(new BorderLayout());
		jButton1.setPreferredSize(new Dimension(119, 34));
//		jButton2.setPreferredSize(new Dimension(119, 34));
//		jButton3.setPreferredSize(new Dimension(119, 34));
//		jButton4.setPreferredSize(new Dimension(119, 34));
//		jButton5.setPreferredSize(new Dimension(119, 34));
//		jButton6.setPreferredSize(new Dimension(119, 34));
//		jButton7.setPreferredSize(new Dimension(119, 34));
		txt.setPreferredSize(new Dimension(300, 34));

		// 面板添加组件
		txt.addKeyListener(this);

		jPanel1.add(txt);
		jPanel1.add(jButton1);
//		jPanel1.add(jButton2);
//		jPanel1.add(jButton3);
//		jPanel1.add(jButton4);
//		jPanel1.add(jButton5);
//		jPanel1.add(jButton6);
//		jPanel1.add(jButton7);
		jPanel1.setSize(700, 100);

		jPanel2.setLayout(new BorderLayout());
		jPanel2.setSize(700, 100);

		jPanel4 = new JScrollPane(jTextArea);
		jPanel4.setBackground(Color.pink);
		// jPanel4.setBounds(0, 0, 5, 30);
		jPanel4.setBorder(BorderFactory.createTitledBorder("玩家输出信息"));
		// jPanel4.setSize(700, 100);

		jPanel5 = new JScrollPane(jTextArea2);
		jPanel5.setBackground(Color.pink);
		// jPanel5.setBounds(0, 0, 5, 30);
		jPanel5.setBorder(BorderFactory.createTitledBorder("玩家buff信息"));
		// jPanel5.setSize(700, 100);

		jPanel6 = new JScrollPane(jTextArea3);
		jPanel6.setBackground(Color.pink);
		// jPanel6.setBounds(0, 0, 5, 30);
		jPanel6.setBorder(BorderFactory.createTitledBorder("怪物攻击信息"));
		// jPanel6.setSize(700, 100);

		jPanel7 = new JScrollPane(jTextArea4);
		jPanel7.setBackground(Color.pink);
		// jPanel7.setBounds(0, 0, 5, 30);
		jPanel7.setBorder(BorderFactory.createTitledBorder("怪物buff信息"));
		jPanel7.setSize(700, 100);

		jPanel2.add(jPanel4, BorderLayout.NORTH);
		jPanel2.add(jPanel5, BorderLayout.CENTER);
		jPanel2.add(jPanel6, BorderLayout.SOUTH);
		jPanel4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jPanel4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jPanel5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jPanel5.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jPanel6.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jPanel6.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jPanel7.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jPanel7.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.add(jPanel1, BorderLayout.NORTH);
		this.add(jPanel2, BorderLayout.CENTER);
		this.add(jPanel7, BorderLayout.SOUTH);

		// 设置显示框大小
		this.setSize(1500, 1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		// this.setResizable(false);

		jButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jTextArea.setText("");
				jTextArea2.setText("");
				jTextArea3.setText("");
				jTextArea4.setText("");
			}

		});

		jButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SendMsg.send("showbag", channel);
			}

		});

		jButton3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SendMsg.send("store", channel);
			}
		});

		jButton4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SendMsg.send("use 蓝药", channel);
			}
		});

		jButton5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SendMsg.send("ack 史莱姆 1", channel);
			}
		});

		jButton6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SendMsg.send("1", channel);
			}
		});

		jButton7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SendMsg.send("3", channel);
			}
		});
	}

	public static void printMsg(String msg, JTextArea jTextArea) {
		jTextArea.append(msg + "\r\n");
		jTextArea.paintImmediately(jTextArea.getBounds());
		// 手动设置光标的位置为最后一行
		jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == txt) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) // 判断按下的键是否是回车键
			{
				String req = txt.getText();
				txt.setText("");
				SendMsg.send(req, channel);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
