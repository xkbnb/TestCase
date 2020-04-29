package com.xkb.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.xkb.service.FileService;

public class FileServiceImpl implements FileService {

	@Override
	public List<String> readTableFile(File file, Map<Integer, String> inputMap) {
		List<String> list = new ArrayList<String>(); // 用于存储符合要求的实例
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String lineTex = null;
			String n = null;
			while ((lineTex = br.readLine()) != null) {
				if (lineTex.contains("n")) { // 该行含有字符n
					Map<Integer, String> map = new HashMap<Integer, String>();
					String[] spaceStrings = lineTex.split("\\s+"); // 先分割空格
					int num = 0;

					for (String ss : spaceStrings) { // 将水平数和因子数添加进map
						if (ss.contains("^")) {
							map.put(num, ss);
							num++;
						}
					}
					if (map.equals(inputMap)) { // 匹配到相符的因子水和水平数
						for (String ss : spaceStrings) { // 获取n值
							if (ss.contains("=")) {
								String[] aa = ss.split("=");
								n = aa[1];
								list.add(n);
							}
						}
						for (int i = 0; i < Integer.parseInt(n); i++) {// 读取从此行开始的n行并存入list中
							list.add(br.readLine());
						}
						break;
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return list;
	}

	@Override
	public ArrayList[] readCaseFile(File file) {
		ArrayList[] firstList = null; // 存放最开始读取的样例
		ArrayList[] arrayList = null; // 存储最终按水平数从小到大排序的样例
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(); // 存储每个因子中水平数的多少，key对应二维firstList行号，value为水平数量
		try {
			BufferedReader br0 = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String tmp = null;
			int lines = 0;
			while ((tmp = br0.readLine()) != null) { // 获取除去空白行的总行数
				if (!tmp.equals("")) {
					lines++;
				}
			}
			firstList = new ArrayList[lines];
			arrayList = new ArrayList[lines]; // 二维list
			for (int i = 0; i < lines; i++) {
				firstList[i] = new ArrayList<String>();
				arrayList[i] = new ArrayList<String>();
			}
			br0.close();

			String lineTex = null;
			int line = 0;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			while ((lineTex = br.readLine()) != null) {
				String[] arrStrings = null;
				if (lineTex.contains(":"))// 英文冒号
				{
					arrStrings = lineTex.split("\\:");
				} else if (lineTex.contains("：")) { // 中文冒号
					arrStrings = lineTex.split("：");
				} else { // 不包含冒号分隔符
					continue;
				}
				firstList[line].add(arrStrings[0]);
				String[] levelStrings = arrStrings[1].trim().split("\\s+"); // 分割同一因数下的水平数
				for (String ss : levelStrings) { // 将水平数添加进arraylist
					firstList[line].add(ss);
				}
				map.put(line, levelStrings.length);
				line++;
			}
			// 按水平数排序
			List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
				@Override
				public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});
			line = 0;
			for (Map.Entry<Integer, Integer> mapping : list) {
				for (int j = 0; j < firstList[mapping.getKey()].size(); j++) {
					arrayList[line].add(firstList[mapping.getKey()].get(j));
				}
				line++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return arrayList;
	}

	@Override
	public Boolean allright(ArrayList[] arrayList, Map<Integer, String> inputMap) {
		String[] arr = new String[inputMap.size()];
		int n = 0;
		int num = 0;
		int temp = 0;
		int row = 0; // 用户输入的正交数中包含的行数
		List<Integer> collist = new ArrayList<Integer>();
		List<Integer> arrcollist = new ArrayList<Integer>();

		for (int key : inputMap.keySet()) { // 获取正交数
			arr[n] = inputMap.get(key);
			n++;
		}
		for (int i = 0; i < arr.length; i++) {
			String[] aa = arr[i].split("\\^");
			collist.add(Integer.parseInt(aa[0]));
			num = Integer.parseInt(aa[1]);
			row += num;
			// 将输入的正交数每一行都加进数组，如2^4,有4行，每行都有2个水平数，则再添加3(4-1)个2进人列数组方便比较
			for (int j = 1 + temp; j < num + temp; j++) {
				collist.add(Integer.parseInt(aa[0]));
			}
			temp = collist.size();
		}
		for (int i = 0; i < arrayList.length; i++) {
			arrcollist.add(arrayList[i].size() - 1); // 获取样例list中每列的个数
		}
		if (row != arrayList.length || !collist.equals(arrcollist)) { // 判断行的个数和每行的水平数是否相等
			return false;
		}
		return true;
	}

	@Override
	public List<String> matchCase(List<String> list, ArrayList[] arrayList) {
		// 当前正交表中有四种情况：
		// 第一种为正交表的一行数据的长度等于因子数的总和，即每个数代表对应因子里面的水平数
		// 第二种为正交表的一行数据的长度 比因子数总和多1(多了一个空格或一位数字，留给其中一个的水平数>10的数做占位符(如"
		// 8")或大于10的水平数(如"12")
		// 这种情况下一般都是最后一位为" 5"或"12"这样的数
		// 第三种为正交表的一行数据的长度是因子数的两倍
		// 这种情况下没个因子里的水平数都占两位
		// 第四种为正交表的一行数据的长度 比因子数总和多2(有一个正交数的水平数大于10且其因子数为2)
		List<String> testCase = new ArrayList<String>();
		int tableLength = list.get(1).length(); // 获取单行正交表的长度
		int num = 0;
		if (tableLength == arrayList.length) { // 第一种情况
			for (int i = 0; i < list.size() - 1; i++) {
				char[] a = list.get(i + 1).toCharArray(); // 将正交表的一行转换为字符数组
				String testcaseString = "";
				for (int j = 0; j < a.length; j++) {
					num = Integer.parseInt(String.valueOf(a[j]));
					testcaseString += arrayList[j].get(num + 1) + "\t";
				}
				testCase.add(testcaseString);
			}
		} else if (tableLength == arrayList.length + 1) { // 第二种情况
			for (int i = 0; i < list.size() - 1; i++) {
				String testcaseString = "";
				char[] a = list.get(i + 1).toCharArray(); // 将正交表的一行转换为字符数组
				for (int j = 0; j < a.length - 2; j++) { // 最后两位长度为一个水平数
					num = Integer.parseInt(String.valueOf(a[j]));
					testcaseString += arrayList[j].get(num + 1) + "\t";
				}
				if (Character.isSpace(a[a.length - 2])) {// 倒数第二个为空格，即" 8"的格式，直接取最后一个数
					num = Integer.parseInt(String.valueOf(a[a.length - 1]));
				} else { // 倒数两位均为数字，则合并为一个数字
					num = Integer.parseInt(String.valueOf(a[a.length - 2]) + String.valueOf(a[a.length - 1]));
				}
				testcaseString += arrayList[a.length - 2].get(num + 1);
				testCase.add(testcaseString);
			}
		} else if(tableLength == arrayList.length*2){ // 第三种情况
			for (int i = 0; i < list.size() - 1; i++) {
				String level = list.get(i + 1).toString();
				int tmp = 0;
				String testcaseString = "";
				for (int j = 0; j < arrayList.length; j++) {
					String s = level.substring(tmp, tmp + 2); // 以两位为单位分割
					if (s.contains(" ")) { // 包含空格，则第二位为数字
						String[] w = s.split(" ");
						num = Integer.parseInt(w[1]);
					} else {
						num = Integer.parseInt(s);
					}
					testcaseString += arrayList[j].get(num + 1) + "\t";
				}
				testCase.add(testcaseString);
			}
		}else if(tableLength == arrayList.length+2){     //第四种情况
			for (int i = 0; i < list.size() - 1; i++) {
				String testcaseString = "";
				char[] a = list.get(i + 1).toCharArray(); // 将正交表的一行转换为字符数组
				for (int j = 0; j < a.length - 4; j++) { // 最后四位长度为两个水平数
					num = Integer.parseInt(String.valueOf(a[j]));
					testcaseString += arrayList[j].get(num + 1) + "\t";
				}
				if (Character.isSpace(a[a.length - 4])) {// 倒数第四个为空格，即" 8"的格式，直接取最后一个数
					num = Integer.parseInt(String.valueOf(a[a.length - 3]));
				} else { // 两位均为数字，则合并为一个数字
					num = Integer.parseInt(String.valueOf(a[a.length - 4]) + String.valueOf(a[a.length - 3]));
				}
				testcaseString += arrayList[a.length - 4].get(num + 1) + "\t";
				if (Character.isSpace(a[a.length - 2])) {// 倒数第二个为空格，即" 8"的格式，直接取最后一个数
					num = Integer.parseInt(String.valueOf(a[a.length - 1]));
				} else { // 倒数两位均为数字，则合并为一个数字
					num = Integer.parseInt(String.valueOf(a[a.length - 2]) + String.valueOf(a[a.length - 1]));
				}
				testcaseString += arrayList[a.length - 3].get(num + 1);
				testCase.add(testcaseString);
			}
		}else {
			testCase.add("error");
		}
		return testCase;
	}

	@Override
	public Boolean writeToExcel(String exportFileUrl, ArrayList[] arrayList, List<String> caselist) {
		Boolean flag = false;
		XSSFWorkbook workbook = new XSSFWorkbook(); // 创建excel工作簿
		XSSFSheet sheet = workbook.createSheet(); // 创建工作表sheet

		XSSFRow row = sheet.createRow(0); // 创建第一行
		XSSFCell cell = null;
		// 插入第一行数据的表头
		for (int i = 0; i < arrayList.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue((arrayList[i].get(0)).toString());
		}
		// 写入数据
		for (int i = 0; i < caselist.size(); i++) {
			XSSFRow nrow = sheet.createRow(i + 1);
			List<String> list = new ArrayList<String>();
			String[] result = caselist.get(i).split("\\t");
			for (String ss : result) { // 将水平数添加进arraylist
				list.add(ss);
			}
			XSSFCell ncell = null;
			for (int j = 0; j < list.size(); j++) {
				ncell = nrow.createCell(j);
				ncell.setCellValue(list.get(j));
			}
		}
		// 创建excel文件
		File file = new File(exportFileUrl);
		try {
			file.createNewFile();
			// 将excel写入
			FileOutputStream stream = new FileOutputStream(file);
			workbook.write(stream);
			stream.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

		return flag;
	}

	@Override
	public Boolean writeToTxt(String exportFileUrl, ArrayList[] arrayList, List<String> caselist) {
		FileWriter fileWriter = null;
		Boolean flag = false;
		try {
			fileWriter = new FileWriter(exportFileUrl);// 创建文本文件
			for (int i = 0; i < arrayList.length; i++) { // 第一行标题
				if (i == arrayList.length - 1) {
					fileWriter.write((arrayList[i].get(0)).toString() + "\n");
				} else {

					fileWriter.write((arrayList[i].get(0)).toString() + "\t");
				}
			}
			// 写入数据
			for (int i = 0; i < caselist.size(); i++) {
				List<String> list = new ArrayList<String>();
				String[] result = caselist.get(i).split("\\t");
				for (String ss : result) { // 将水平数添加进arraylist
					list.add(ss);
				}
				for (int j = 0; j < list.size(); j++) {
					if (j == list.size() - 1) {
						fileWriter.write(list.get(j) + "\n");
					} else {
						fileWriter.write(list.get(j) + "\t");
					}
				}
			}

			fileWriter.write("共" + caselist.size() + "条");
			fileWriter.flush();
			fileWriter.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return flag;
	}

	@Override
	public Map<Integer, String> cfmInput(String inputString) {
		Map<Integer, String> inputMap = new HashMap<Integer, String>();
		String regex0 = "[a-zA-Z]"; // 含有字母
		String regex1 = "[`~!@#$%&*()+=|{}':;',\\[\\].<>/?~@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; // 含有特定字符
		Pattern p0 = Pattern.compile(regex0);
		Pattern p1 = Pattern.compile(regex1);
		Matcher m0 = p0.matcher(inputString);
		Matcher m1 = p1.matcher(inputString);
		if (m0.find() || m1.find()) { // 有特殊字符和字母
			inputMap.put(0, "OtherStrings");
			return inputMap;
		}
		String[] singleString = inputString.split("\\s+"); // 分成单个正交数
		List<Integer> levelList = new ArrayList<Integer>(); // 存放每个正交数的水平数用于排序
		for (int i = 0; i < singleString.length; i++) {
			String[] aa = singleString[i].split("\\^");
			if (aa.length != 2) { // 可能为^^2或2^^或2^3^3或2^^3的形式，这些形式分割后数组长度不为2
				inputMap.put(0, "Input error");
				return inputMap;
			} else {
				levelList.add(Integer.parseInt(aa[0]));
			}
		}
		Collections.sort(levelList); // 按水平数从小到大排序
		for (int i = 0; i < levelList.size(); i++) {
			String level = levelList.get(i).toString() + "^";
			for (int j = 0; j < singleString.length; j++) {
				if (singleString[j].startsWith(level)) {
					inputMap.put(i, singleString[j]);
					break;
				}
			}
		}
		return inputMap;
	}
}
