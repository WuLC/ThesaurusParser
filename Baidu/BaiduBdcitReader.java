/**
 * 功能：解析百度词库文件(bdict)，返回存储词语的list
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.*;

public class BaiduBdcitReader
{   
	/**
	 *  读取百度词库文件(bdict)，返回一个包含所以词的list
	 * @param bdictFilePath : bdict文件的路径
	 * @return: 包含词库文件中所有词的一个List<String>
	 * @throws Exception
	 */
    public static List<String> readBdictFile(String bdictFilePath) throws Exception 
    {
    	 // read bdict into byte array
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        FileChannel fChannel = new RandomAccessFile(bdictFilePath, "r").getChannel();
        fChannel.transferTo(0, fChannel.size(), Channels.newChannel(dataOut));
        fChannel.close();
        ByteBuffer dataRawBytes = ByteBuffer.wrap(dataOut.toByteArray());
        dataRawBytes.order(ByteOrder.LITTLE_ENDIAN);

        byte[] buf = new byte[1024];
        byte[] pinyin = new byte[1024];  
        // dictionary offset
        dataRawBytes.position(0x350);
        
        List<String> wordList = new ArrayList<String>();
        String word = null;
        while (dataRawBytes.position() < dataRawBytes.capacity())
        {   
            int length = dataRawBytes.getShort(); //得到词的字节长度
            dataRawBytes.getShort(); 
            try
            {
			dataRawBytes.get(pinyin,0,2 * length); //跳过拼音
			dataRawBytes.get(buf, 0, 2 * length);  //得到实际的词		     
            word = new String(buf, 0, 2 * length, "UTF-16LE");   
            wordList.add(word);                  
            }
            catch (Exception e)
            {
            	return wordList;
            }
            
        }
        return wordList;
            
    }
    
    public static void main(String[] args) 
    {
        
        String bdictFile = "G:/各大输入法词库/百度/windows单线程/电子游戏/手游/热门手游.bdict";
        List<String> wordList = new ArrayList<String>();
        try
        {
        wordList = readBdictFile(bdictFile);
        }
        catch (Exception e)
        {
        	System.out.println(bdictFile+"not parsed successfully");
        }
        
        for(int i=0;i<wordList.size();i++)
        {
        	System.out.println(wordList.get(i));
        }
        System.out.println("total words:"+wordList.size());
    }
       
}