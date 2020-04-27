package com.xkb.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.xkb.controller.FileController;

@SuppressWarnings("serial")
public class MyJfame extends JFrame {

	String caseFileUrl = null; // 样例文件路径
	String tableFileUrl = null; // 正交表路径
	String inputString = null; // 用户输入的正交数
	String exportFileUrl = "D:"; // 输出测试用例文件,默认D盘

	public MyJfame() {
		final JFrame jf = new JFrame("正交表测试用例");

		jf.setSize(650, 800);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel jPanel = new JPanel();
		jPanel.setLayout(null); // 绝对布局
		jPanel.setBackground(Color.CYAN);

		JLabel numJLabel = new JLabel("请输入n个正交数：");
		numJLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
		jPanel.add(numJLabel);
		numJLabel.setBounds(10, 30, 130, 30);
		// 正交数输入框
		JTextField numText = new JTextField();
		jPanel.add(numText);
		numText.setBounds(140, 30, 465, 30);

		JLabel caseFileUrlJLabel = new JLabel("请选择样例文件：");
		caseFileUrlJLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
		jPanel.add(caseFileUrlJLabel);
		caseFileUrlJLabel.setBounds(10, 90, 130, 30);

		// 样例文件路径框
		JTextField caseUrlField = new JTextField();
		caseUrlField.setEditable(false);
		jPanel.add(caseUrlField);
		caseUrlField.setBounds(140, 90, 360, 30);

		// 选择样例文件按钮
		JButton caseopenButton = new JButton("选择样例");
		caseopenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				caseFileUrl = showFileOpenDialog(jf, caseUrlField, 0);
			}
		});
		jPanel.add(caseopenButton);
		caseopenButton.setBounds(505, 90, 100, 30);

		JLabel tableFileUrlJLabel = new JLabel("请选择正交表文件：");
		tableFileUrlJLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
		jPanel.add(tableFileUrlJLabel);
		tableFileUrlJLabel.setBounds(10, 150, 135, 30);

		// 正交表文件路径框
		JTextField tableFileUrlField = new JTextField();
		tableFileUrlField.setEditable(false);
		jPanel.add(tableFileUrlField);
		tableFileUrlField.setBounds(140, 150, 360, 30);

		// 选择正交表文件按钮
		JButton tableopenButton = new JButton("选择正交表");
		tableopenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableFileUrl = showFileOpenDialog(jf, tableFileUrlField, 0);
			}
		});
		jPanel.add(tableopenButton);
		tableopenButton.setBounds(505, 150, 100, 30);

		JLabel resultFileUrlJLabel = new JLabel("请选择保存位置：");
		resultFileUrlJLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
		jPanel.add(resultFileUrlJLabel);
		resultFileUrlJLabel.setBounds(10, 210, 130, 30);

		// 保存位置路径框
		JTextField resultFileUrlField = new JTextField();
		resultFileUrlField.setEditable(false);
		jPanel.add(resultFileUrlField);
		resultFileUrlField.setBounds(140, 210, 360, 30);

		// 选择文件按钮
		JButton resultButton = new JButton("选择位置");
		resultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportFileUrl = showFileOpenDialog(jf, resultFileUrlField, 1);
			}
		});
		jPanel.add(resultButton);
		resultButton.setBounds(505, 210, 100, 30);

		JLabel testCaseJLabel = new JLabel("测试用例：");
		testCaseJLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
		jPanel.add(testCaseJLabel);
		testCaseJLabel.setBounds(10, 250, 130, 30);

		// 识别结果框
		JTextArea testCaseArea = new JTextArea();
//		testCaseArea.setLineWrap(true); // 自动换行
		testCaseArea.setFont(new Font(null, Font.PLAIN, 18)); // 设置字体
		testCaseArea.setEditable(false);

		ScrollPane sp = new ScrollPane(); // 设置滚动条
		sp.add(testCaseArea);
		sp.setBounds(10, 280, 600, 300);
		jPanel.add(sp);

		// 写入Excel按钮
		JButton excelButton = new JButton("结果写入Excel");
		excelButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
		jPanel.add(excelButton);
		excelButton.setBounds(100, 600, 200, 30);

		excelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickEvent(e, jf, numText, resultFileUrlField, testCaseArea, "xlsx");
			}
		});

		// 写入txt按钮
		JButton startButton = new JButton("结果写入txt");
		startButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
		jPanel.add(startButton);
		startButton.setBounds(340, 600, 200, 30);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickEvent(e, jf, numText, resultFileUrlField, testCaseArea, "txt");
			}
		});
		jf.setContentPane(jPanel);
		jf.setVisible(true);
	}

	/*
	 * 文件选择器
	 */
	private static String showFileOpenDialog(Component parent, JTextField urlTextField, int n) {
		JFileChooser chooser = new JFileChooser(); // 文件选择器
		String path = null;
		chooser.setCurrentDirectory(new File("D:\\软件测试\\工具开发")); // 设置默认显示的文件夹
		if (n == 0) {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // 设置只选文件
		} else {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 只选文件夹
		}
		chooser.setMultiSelectionEnabled(false); // 设置是否允许多选
		// 设置默认使用的文件过滤器
		chooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		int result = chooser.showOpenDialog(parent); // 打开文件选择框
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			path = file.getAbsolutePath();
			if (n == 0 && !path.contains(".txt")) {// 选择的不是.txt文件
				JOptionPane.showMessageDialog(parent, "请选择.txt文件", "警告", JOptionPane.WARNING_MESSAGE);
				return null;
			}
			urlTextField.setText(path); // 向路径框中写入文件路径
		}
		return path;
	}

	/**
	 * 抽取点击事件里的方法
	 */
	private void clickEvent(ActionEvent e, JFrame jf, JTextField numText, JTextField resultFileUrlField,
			JTextArea testCaseArea, String method) {
		inputString = numText.getText().trim();

		if (exportFileUrl.contains("\\ResultCase.")) { // 点击存入excel后再点击存入txt的情况
			int n = exportFileUrl.lastIndexOf("\\ResultCase.");
			exportFileUrl = exportFileUrl.substring(0, n); // 获取保存路径的前部分，如D:\Resultcase.txt，保存为D:
		}

		exportFileUrl += "\\ResultCase." + method;
		resultFileUrlField.setText(exportFileUrl);

		if (inputString.isEmpty()) {
			JOptionPane.showMessageDialog(jf, "未输入正交数", "警告", JOptionPane.WARNING_MESSAGE);
		} else if (caseFileUrl == null || tableFileUrl == null) {
			JOptionPane.showMessageDialog(jf, "未选择文件", "警告", JOptionPane.WARNING_MESSAGE);
		} else {
			Map<Integer, String> resultMap = com.xkb.controller.FileController.TestCase(inputString, caseFileUrl,
					tableFileUrl, exportFileUrl, method);
			showResult(jf, testCaseArea, resultMap);
		}
	}

	public void showResult(JFrame jf, JTextArea testCaseArea, Map<Integer, String> resultMap) {
		switch (resultMap.get(0)) {
		case "求解测试用例并写入文件成功":
			showTestCase(testCaseArea);
			JOptionPane.showMessageDialog(jf, resultMap.get(0), "成功", JOptionPane.INFORMATION_MESSAGE);
			break;
		case "OtherStrings":
			JOptionPane.showMessageDialog(jf, "含有非法字符，请重新输入", "警告", JOptionPane.WARNING_MESSAGE);
			break;
		case "Input error":
			JOptionPane.showMessageDialog(jf, "输入的正交数有误，请重新输入", "警告", JOptionPane.WARNING_MESSAGE);
			break;
		case "正交表文件不存在":
			JOptionPane.showMessageDialog(jf, resultMap.get(0) + "，请重新选择", "警告", JOptionPane.WARNING_MESSAGE);
			break;
		case "样例文件不存在":
			JOptionPane.showMessageDialog(jf, resultMap.get(0) + "，请重新选择", "警告", JOptionPane.WARNING_MESSAGE);
			break;
		case "正交表中没有与之对应的测试用例！":
		case "样例文件和输入的正交数不对应！":
		case "测试用例写入文件失败":
			JOptionPane.showMessageDialog(jf, resultMap.get(0), "警告", JOptionPane.WARNING_MESSAGE);
			break;
		default:
			break;
		}
	}
	
	public void showTestCase(JTextArea testCaseArea) {
		testCaseArea.setText("");   //第二次使用时清空上一次的
		List<String> testcase = FileController.ReturnTestCase();
		for (int i = 0; i < testcase.size(); i++) { // 第一行标题
			testCaseArea.append(testcase.get(i)+"\n");
		}
	}

	public static void main(String[] args) {
		MyJfame myJfame = new MyJfame();
	}

}
