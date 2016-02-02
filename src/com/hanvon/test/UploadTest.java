package com.hanvon.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.hanvon.sulupen.application.HanvonApplication;
import com.hanvon.sulupen.db.bean.NoteBookRecord;
import com.hanvon.sulupen.utils.Base64Utils;
import com.hanvon.sulupen.utils.LogUtil;

import junit.framework.TestCase;

public class UploadTest extends TestCase {

	private static int BUF_SIZE = 1024;
	public void ParsehtmlFile() throws MalformedURLException, IOException{
		Document doc;

		doc = Jsoup.parse(new URL("http://cloud.hwyun.com/dweb-cloud/wa8z3eod.html"), 5000);

		Elements content = doc.getElementsByTag("h1");
		LogUtil.i("--title: "+content.text());
		content = doc.getElementsByTag("p");
		LogUtil.i("--content: "+content.text());

	//	Elements images = doc.getElementsByClass("tupian");
		Elements imagesrc = doc.select("img");
		LogUtil.i("---------imageSize = "+imagesrc.size());

		int count = 0;
		File file = new File("/sdcard/"+"234567.txt");
		FileOutputStream fos = new FileOutputStream(file);
		for(Element image:imagesrc){
			if(count == 0){
				count++;
				continue;
			}
			String aa = image.attr("src");
			LogUtil.i("--image:-"+aa);
			fos.write(aa.getBytes());
			fos.write("------------------------------------\r\n".getBytes());
			
			
		}	
		fos.close();
		
	}
	
	public void CreateDir(){
		String path = "/sdcard/sulupen/";
		File file=new File(path); 
		if(!file.exists()){ 
		   file.mkdir();
	    }
	}
	
    public static void ParseFileContent() throws IOException, JSONException{
		
	    String base64File = "/sdcard/sulupen/test2345/7654321.txt";
		int blocknum;
		byte[] buffer;
		int readBytes = BUF_SIZE;
		String fileContent = null;
	    File file = new File(base64File);

		FileInputStream fis = new FileInputStream(file); 
		int length = fis.available();

		if (length%BUF_SIZE != 0){
            blocknum = length/BUF_SIZE + 1;
        }else{
            blocknum = length/BUF_SIZE;
        }
        if (blocknum <= 1){
        //	buffer =  new byte[length];
        }else{
        //	buffer =  new byte[BUF_SIZE];
        }
        buffer =  new byte[length];
        fis.read(buffer);
        fileContent = new String(buffer);
        fis.close();

        JSONObject json = new JSONObject(fileContent);
        String title = json.getString("title");
        String content = json.getString("content");
        JSONArray imgArray = new JSONArray(json.getString("images"));
        for (int i = 0;i < imgArray.length();i++){
        	JSONObject imgJson = imgArray.getJSONObject(i);
        	String imagsrc = imgJson.getString("image");
        	byte[]bitmapArray;
            bitmapArray=Base64.decode(imagsrc, Base64.DEFAULT);
            Bitmap  bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        //	Bitmap bmp = BitmapFactory.decodeByteArray(imagsrc.getBytes(), 0, imagsrc.getBytes().length);
        	saveBitmapFile(bitmap,"7654321",i);
        }
	}

	public static void saveBitmapFile(Bitmap bitmap,String contentid,int index){
        File file=new File("/sdcard/sulupen/test2345/"+contentid+"_"+index+".jpg");//将要保存图片的路径  头像文件
        try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
	
	
	public static void unZip() throws IOException {
        // 创建解压目标目录
		String outputDirectory = "/sdcard/sulupen/";
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        String assetName = "/sdcard/11111.zip";
        File src = new File(assetName);
        InputStream inputStream = new FileInputStream(src);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        // 使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            // 如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者是文件不存在
                if (!file.exists()) {
                    file.mkdir();
                }
            } else {
                // 如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者文件不存在，则解压文件
                if (!file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }
	
	
	public static Vector<String> GetVideoFileName() {
		String fileAbsolutePath = "/sdcard/sulupen/";
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
 
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                vecFile.add(filename);
            }
        }
        return vecFile;
    }

	 public static void deleteDir() {
		 String SDPATH = "/sdcard/sulupen/11111";
	        File dir = new File(SDPATH);
	        if (dir == null || !dir.exists() || !dir.isDirectory())
	            return;
	         
	        for (File file : dir.listFiles()) {
	            if (file.isFile())
	                file.delete(); // 删除所有文件
	        }
	        dir.delete();// 删除目录本身
	    }


	 public static void HvnCloudUploadTags() throws JSONException{

		 JSONArray array = new JSONArray();
		 
		 for(int i = 0;i < 2;i++){
		    JSONObject JSuserInfoJson1 = new JSONObject();
		    JSuserInfoJson1.put("tagId", "111100"+i);
	  	    JSuserInfoJson1.put("tagName", "测试笔记"+i);
	  	    array.put(JSuserInfoJson1);
		 }

			    JSONObject JSuserInfoJson = new JSONObject();
			    JSuserInfoJson.put("uid", HanvonApplication.AppUid);
		  	    JSuserInfoJson.put("sid", HanvonApplication.AppSid);
		  	    JSuserInfoJson.put("ver", HanvonApplication.AppVer);
		  	    JSuserInfoJson.put("userid", HanvonApplication.hvnName);
		  	    JSuserInfoJson.put("devid", "wwww");
		  	    JSuserInfoJson.put("tags", array);
			    //String tags = "{\"tags\":[{\"tagId\":\"11111\", \"tagName\":\"笔记本上传测试1\"}, {\"tagId\":\"22222\", \"tagName\":\"笔记本上传测试2\"}]\",\"userid\":\"test2345\",\"uid\":\"10046\",\"sid\":\"SuluPen_Software\",\"ver\":\"1.0.1.021\",\"devid\":\"wwww\"}";
			
			   LogUtil.i("======"+JSuserInfoJson.toString());
		   
		}
}
