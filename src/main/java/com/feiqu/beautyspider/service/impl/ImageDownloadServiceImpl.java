package com.feiqu.beautyspider.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.feiqu.beautyspider.model.Image;
import com.feiqu.beautyspider.service.downloadService;

@Service
public class ImageDownloadServiceImpl implements downloadService{
	@Value("${downloadFileDir}")
	private String downloadFileDir ;
	
	
	@Override
	public void download(Image image)  {
		try {
			System.out.println(JSONObject.toJSONString(image));
			String suffix=image.getAddress().split("\\.")[image.getAddress().split("\\.").length-1];
			File downloadFileDirdest = new File(downloadFileDir);
			if (!downloadFileDirdest.exists() && !downloadFileDirdest.isDirectory()) {
				downloadFileDirdest.mkdir();
			}
			File dest2 = new File(downloadFileDir + "/" + image.getTag());
			if (!dest2.exists() && !dest2.isDirectory()) {
				dest2.mkdir();
			}
			File dest3 = new File(downloadFileDir + "/" + image.getTag() + "/" + image.getTitle());
			if (!dest3.exists() && !dest3.isDirectory()) {
				dest3.mkdir();
			}
			File dest = new File(downloadFileDir + "/" + image.getTag() + "/" + image.getTitle() + "/" + image.getName()
					+ "." + suffix);
			if (!dest.exists()) {
				dest.createNewFile();
			}
			InputStream is;
			//字节输出流
			FileOutputStream fos = new FileOutputStream(dest);
			URL temp;
			String imgurl = image.getAddress();
			temp = new URL(imgurl.trim());
			HttpURLConnection uc = (HttpURLConnection) temp.openConnection();
			uc.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
			//必须加refer 防封 这个比较烂 写成百度地址也可以
			//	uc.addRequestProperty("Referer", "https://manhua.dmzj.com/");
			is = uc.getInputStream();
			//为字节输入流加缓冲
			BufferedInputStream bis = new BufferedInputStream(is);
			//为字节输出流加缓冲
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int length;
			byte[] bytes = new byte[1024 * 20];
			while ((length = bis.read(bytes, 0, bytes.length)) != -1) {
				fos.write(bytes, 0, length);
			}
			bos.close();
			fos.close();
			bis.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
