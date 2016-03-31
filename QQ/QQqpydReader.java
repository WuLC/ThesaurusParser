/*
 * 功能：解析qq词库文件(qpyd)，返回存储词语的list
 */
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.zip.InflaterOutputStream;
import java.util.List;
import java.util.ArrayList;

public class QQqpydReader 
{
    public static void main(String[] args) throws Exception 
    {  
       String qpydFile = "G:/各大输入法词库/QQ/单线程下载/QQ/城市地区/安徽/城市信息(安庆).qpyd";
       List<String> wordList = new ArrayList<String>();
       
       wordList = readQpydFile(qpydFile);
       for(int i=0;i<wordList.size();i++)
       {
    	   System.out.println(wordList.get(i));
       }
    }
    
    /**
	 *  读取qq词库文件(qpyd)，返回一个包含所以词的list
	 * @param inputPath : qpyd文件的路径
	 * @return: 包含词库文件中所有词的一个List<String>
	 * @throws Exception
	 */
    public static List<String> readQpydFile(String inputPath) throws Exception
    {
        
        List<String> wordList = new ArrayList<String>();
        // read qpyd into byte array
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
        FileChannel fChannel = new RandomAccessFile(inputPath, "r").getChannel();
        fChannel.transferTo(0, fChannel.size(), Channels.newChannel(dataOut));
        fChannel.close();

        // qpyd as bytes
        ByteBuffer dataRawBytes = ByteBuffer.wrap(dataOut.toByteArray());
        dataRawBytes.order(ByteOrder.LITTLE_ENDIAN);

        // read info of compressed data
        int startZippedDictAddr = dataRawBytes.getInt(0x38);
        int zippedDictLength = dataRawBytes.limit() - startZippedDictAddr;

        // read zipped qqyd dictionary into byte array
        dataOut.reset();
        Channels.newChannel(new InflaterOutputStream(dataOut)).write(
                ByteBuffer.wrap(dataRawBytes.array(), startZippedDictAddr, zippedDictLength));

        // uncompressed qqyd dictionary as bytes
        ByteBuffer dataUnzippedBytes = ByteBuffer.wrap(dataOut.toByteArray());
        dataUnzippedBytes.order(ByteOrder.LITTLE_ENDIAN);

        // for debugging: save unzipped data to *.unzipped file
        Channels.newChannel(new FileOutputStream(inputPath + ".unzipped")).write(dataUnzippedBytes);
     
        // stores the start address of actual dictionary data
        int unzippedDictStartAddr = -1;
        int idx = 0;
         
        byte[] byteArray = dataUnzippedBytes.array();
        while (unzippedDictStartAddr == -1 || idx < unzippedDictStartAddr) 
        {
            // read word
            int pinyinStartAddr = dataUnzippedBytes.getInt(idx + 0x6);
            int pinyinLength = dataUnzippedBytes.get(idx + 0x0) & 0xff;
            int wordStartAddr = pinyinStartAddr + pinyinLength;
            int wordLength = dataUnzippedBytes.get(idx + 0x1) & 0xff;
            if (unzippedDictStartAddr == -1) 
            {
                unzippedDictStartAddr = pinyinStartAddr;
            }
            String word = new String(Arrays.copyOfRange(byteArray, wordStartAddr, wordStartAddr + wordLength),
                    "UTF-16LE");
            wordList.add(word);
             
            // step up
            idx += 0xa;
        }
      return wordList;  
    }
}

