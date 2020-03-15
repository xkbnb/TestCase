package com.xkb.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface FileService {
	/**
	 * 读取正交表文件并匹配字符
	 * 
	 * @param file     正交表文件
	 * @param inputMap 输入的水平数和因子
	 * @return 存储对应正交表的list,list第一个数为测试用例的个数
	 */
	public List<String> readTableFile(File file, Map<Integer, String> inputMap);

	/**
	 * 读取样例文件
	 * 
	 * @param file 样例文件
	 * @return 存储样例的二维list，list的每个一维list数组的第一个为水平数的类型,以服务器： IIS Apache
	 *         Jetty为例，第一个为服务器
	 */
	public ArrayList[] readCaseFile(File file);

	/**
	 * 判断样例文件和用户输入的正交数是否对应
	 * 
	 * @param arrayLists 存储样例的二维list
	 * @param inputMap   输入的水平数和因子
	 * @return 两者是否相等
	 */
	public Boolean allright(ArrayList[] arrayList, Map<Integer, String> inputMap);

	/**
	 * 将样例文件中的样例根据正交表转换为测试用例
	 * 
	 * @param list      对应正交表的list,list第一个数为测试用例的个数
	 * @param arrayList 存储样例的二维list
	 * @return 测试用例的list
	 */
	public List<String> matchCase(List<String> list, ArrayList[] arrayList);

	/**
	 * 将测试用例写入excel
	 * 
	 * @param exportFileUrl Excel表的存储路径
	 * @param arrayList     存储样例的二维list
	 * @param caselist      存储测试用例的list
	 * @return 是否写入成功
	 */
	public Boolean writeToExcel(String exportFileUrl, ArrayList[] arrayList, List<String> caselist);

	/**
	 * 将测试用例写入txt文档
	 * 
	 * @param exportFileUrl txt文档的存储路径
	 * @param arrayList     存储样例的二维list
	 * @param caselist      存储测试用例的list
	 * @return 是否写入成功
	 */
	public Boolean writeToTxt(String exportFileUrl, ArrayList[] arrayList, List<String> caselist);

	/**
	 * 将用户输入的正交数按水平数大小排序并存进map
	 * 
	 * @param inputString 用户输入的正交数
	 * @return map类型
	 */
	public Map<Integer, String> cfmInput(String inputString);

}
