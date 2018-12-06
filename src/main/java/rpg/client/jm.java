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

import io.netty.channel.Channel;

public class jm extends JFrame implements KeyListener {
	JPanel jPanel1, jPanel2; // 三块面板，两块是按钮，一块是输出台
	JScrollPane jPanel4, jPanel5, jPanel6;

	JButton jButton1, jButton2; // 两个按钮，一个清屏，一个展示

	JTextField txt;

	static JTextArea jTextArea, jTextArea2, jTextArea3; // 输出台

	Channel channel;

	public jm(Channel channel) {
		this.channel = channel;

		initComp();
	}

	public void initComp() {

		jPanel1 = new JPanel();
		jPanel2 = new JPanel();
		// 两个按钮

		jButton1 = new JButton("清屏");
		jButton2 = new JButton("展示");
		txt = new JTextField();
		// 输出台

		jTextArea = new JTextArea(10, 100);
		Font x = new Font("宋体", 1, 20);
		jTextArea.setFont(x);
		jTextArea.setEditable(false);

		jTextArea2 = new JTextArea(10, 100);
		jTextArea2.setFont(x);
		jTextArea2.setEditable(false);

		jTextArea3 = new JTextArea(10, 100);
		jTextArea3.setFont(x);
		jTextArea3.setEditable(false);
		// 设置布局

		this.setLayout(new BorderLayout());
		jButton1.setPreferredSize(new Dimension(119, 34));
		jButton2.setPreferredSize(new Dimension(119, 34));
		txt.setPreferredSize(new Dimension(300, 34));

		// 面板添加组件
		txt.addKeyListener(this);

		jPanel1.add(txt);
		jPanel1.add(jButton1);
		jPanel1.add(jButton2);
		jPanel1.setSize(700, 100);

//		jPanel2 = new JScrollPane(jTextArea2);
		jPanel2.setLayout(new BorderLayout());
//		jPanel2.setBackground(Color.pink);
//		jPanel2.setBounds(0, 0, 0, 0);
//		jPanel2.setBorder(BorderFactory.createTitledBorder("玩家输出信息"));
//		jPanel2.setSize(0, 0);

		jPanel4 = new JScrollPane(jTextArea);
		jPanel4.setBackground(Color.pink);
		jPanel4.setBounds(0, 0, 0, 0);
		jPanel4.setBorder(BorderFactory.createTitledBorder("玩家输出信息"));
		jPanel4.setSize(10, 10);

		jPanel5 = new JScrollPane(jTextArea2);
		jPanel5.setBackground(Color.pink);
		jPanel5.setBounds(0, 0, 0, 0);
		jPanel5.setBorder(BorderFactory.createTitledBorder("怪物攻击信息"));
		jPanel5.setSize(10, 10);

		jPanel6 = new JScrollPane(jTextArea3);
		jPanel6.setBackground(Color.pink);
		jPanel6.setBounds(0, 0, 0, 0);
		jPanel6.setBorder(BorderFactory.createTitledBorder("玩家buff信息"));
		jPanel6.setSize(10, 10);

		jPanel2.add(jPanel4, BorderLayout.NORTH);
		jPanel2.add(jPanel5, BorderLayout.CENTER);
		jPanel2.add(jPanel6, BorderLayout.SOUTH);
		jPanel4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jPanel4.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jPanel5.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jPanel5.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jPanel6.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jPanel6.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.add(jPanel1, BorderLayout.NORTH);
		this.add(jPanel2, BorderLayout.CENTER);

		// 设置显示框大小
		this.setSize(700, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
//		this.setResizable(false);

		jButton1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jTextArea.setText("");
			}

		});

		jButton2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				channel.writeAndFlush("showbag\n");
			}

		});

	}

	public static void printMsg(String msg) {
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
				channel.writeAndFlush(req + "\n");
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
