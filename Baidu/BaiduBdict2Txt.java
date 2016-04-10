
/**
 * 功能：将百度词库bdict文件中包含的词语转为txt存储，一个词语占一行
 */

import java.io.PrintWriter;
import java.io.File;
import java.util.*;


public class BaiduBdict2Txt {
	/**
	 * 功能: 将输入的bdict文件转为txt文件
	 * @param inputPath: 输入的bdcit文件路径
	 * @param outputPath: 输出的txt文件路径
	 * @return : void
	 */
	public static void transBdict2Txt(String inputPath,String outputPath) throws Exception
	{
		List<String> wordList = new ArrayList<String>();
		wordList = BaiduBdcitReader.readBdictFile(inputPath);
		
		//create outputDirs if not exists
		File outFile = new File(outputPath);
		outFile.getParentFile().mkdirs();
		
		PrintWriter writer = new PrintWriter(outputPath, "UTF-8");
		for (int i=0;i<wordList.size();i++)
		{
			writer.println(wordList.get(i));
		}
		writer.close();
		System.out.println(outputPath+ " created \ntotal "+wordList.size()+" words");
		
	}
    
	public static void main(String[] args) throws Exception
	{
		String inputPath = "G:/各大输入法词库/百度/windows单线程/电子游戏/手游/热门手游.bdict";
		String outputPath = "G:/各大输入法词库/百度/windows单线程/电子游戏/手游/热门手游.txt";
		transBdict2Txt(inputPath, outputPath);
	}
}
