package rpg.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class jm extends JFrame {
	JPanel jPanel1, jPanel3; // 三块面板，两块是按钮，一块是输出台
	JScrollPane jPanel2;

	JButton jButton1, jButton2; // 两个按钮，一个扫描目录，一个确认执行

	static JTextArea jTextArea; // 输出台

	public jm() {

		initComp();

	}

	public void initComp() {

		jPanel1 = new JPanel();

		// 两个按钮

		jButton1 = new JButton("扫描目录");

		jButton2 = new JButton("执行");

		// 输出台

		jTextArea = new JTextArea();

		// 设置布局

		this.setLayout(new BorderLayout());

		jButton1.setPreferredSize(new Dimension(119, 34));

		jButton2.setPreferredSize(new Dimension(119, 34));

		// 面板添加组件

		jPanel1.add(jButton1);

		jPanel1.add(jButton2);

		jPanel1.setSize(700, 100);

		jPanel2 = new JScrollPane(jTextArea);

		jPanel2.setBackground(Color.LIGHT_GRAY);

		jPanel2.setBounds(41, 34, 313, 194);

		jPanel2.setBorder(BorderFactory.createTitledBorder("控制台"));

		jPanel2.setSize(300, 100);

		this.add(jPanel1, BorderLayout.NORTH);

		this.add(jPanel2, BorderLayout.CENTER);

		// 设置显示框大小

		this.setSize(700, 600);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setVisible(true);

		jButton1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

			}

		});

		jButton2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

			}

		});

	}

	public static void printMsg(String msg) {
		jTextArea.append(msg);
		jTextArea.paintImmediately(jTextArea.getBounds());
	}
}
