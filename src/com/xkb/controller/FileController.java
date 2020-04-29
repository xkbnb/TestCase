package com.xkb.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xkb.service.FileService;
import com.xkb.service.impl.FileServiceImpl;

public class FileController {

	private static FileService fileService = new FileServiceImpl();
	static List<String> testcase = null;
	static List<String> list = null;
	static ArrayList[] arrayList = null;

	public static Map<Integer, String> TestCase(String inputString, String caseFileUrl, String ZjFileUrl,
			String exportFileUrl, String method) {
		Map<Integer, String> inputMap = new HashMap<Integer, String>(); // 用户输入的正交数
		Map<Integer, String> resultMap = new HashMap<Integer, String>(); // 结果
		inputMap = fileService.cfmInput(inputString);
		resultMap = inputMap;
		if (resultMap.get(0).equals("OtherStrings") || resultMap.get(0).equals("Input error")) {
			return resultMap;
		}
		File ZJFile = new File(ZjFileUrl);
		if (!ZJFile.exists()) {
			resultMap.put(0, "正交表文件不存在");
			return resultMap;
		}
		File CaseFile = new File(caseFileUrl);
		if (!ZJFile.exists()) {
			resultMap.put(0, "样例文件不存在");
			return resultMap;
		}
		list = new ArrayList<String>(); // 用于存储符合要求的正交表
		list = fileService.readTableFile(ZJFile, inputMap);
		if (list.isEmpty()) {
			resultMap.put(0, "正交表中没有与之对应的测试用例！");
			return resultMap;
		}
		arrayList = fileService.readCaseFile(CaseFile); // 存储样例的二维list
		if (!fileService.allright(arrayList, inputMap)) { // 样例文件和用户输入的正交数不对应
			resultMap.put(0, "样例文件和输入的正交数不对应！");
			return resultMap;
		}
		testcase = fileService.matchCase(list, arrayList); // 测试用例
		if(testcase.get(0).equals("error")) {
			resultMap.put(0, "样例文件与正交表匹配出错");
			return resultMap;
		}
		if (method.equals("txt")) {
			if (!fileService.writeToTxt(exportFileUrl, arrayList, testcase)) { // 测试用例写入txt文件
				resultMap.put(0, "测试用例写入文件失败");
				return resultMap;
			} else {
				resultMap.put(0, "求解测试用例并写入文件成功");
			}
		} else {
			if (!fileService.writeToExcel(exportFileUrl, arrayList, testcase)) { // 测试用例写入txt文件
				resultMap.put(0, "测试用例写入文件失败");
				return resultMap;
			} else {
				resultMap.put(0, "求解测试用例并写入文件成功");
			}
		}
		return resultMap;
	}

	// 返回测试用例
	public static List<String> ReturnTestCase() {
		String s = "";
		for (int i = 0; i < arrayList.length; i++) {
				s += arrayList[i].get(0).toString() + "\t";
		}
		testcase.add(0, s);
		return testcase;
	}
}
