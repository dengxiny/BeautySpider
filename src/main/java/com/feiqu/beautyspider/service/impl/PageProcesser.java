package com.feiqu.beautyspider.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.feiqu.beautyspider.controller.SpiderController;
import com.feiqu.beautyspider.model.Image;
import com.feiqu.beautyspider.service.downloadService;
import com.feiqu.beautyspider.util.MapToObject;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

@Component
public class PageProcesser implements PageProcessor{
	public  static Logger logger = LoggerFactory.getLogger(PageProcesser.class);
	@Value("#{'${tag-list}'.split(',')}")
	private List<String> tagList;
	@Value("${domain}")
	private String domain;
	@Autowired 
	private downloadService downloadService;
	
	public Site site=Site.me()
			.setRetryTimes(3).setSleepTime(5000).setTimeOut(10000).setCharset("gb2312")
			.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
	@Override
	public void process(Page page) {
		//System.out.println("url:"+page.getUrl().toString());
		//主页匹配tag
		if(page.getUrl().toString().equals("http://www.27270.com/tag/")) {
			for (String tagID : tagList) {
				logger.info("加载标签"+tagID);
				List<Selectable> list=page.getHtml().xpath("//div[@id='"+tagID+"']/div[@class='tags_list']/a").nodes();
				for (Selectable selectable : list) {
					String tag=selectable.xpath("/a/text()").toString();
					String url=domain+selectable.xpath("/a/@href").toString();
			/*	String tag=list.get(0).xpath("/a/text()").toString();
				String url=domain+list.get(0).xpath("/a/@href").toString();*/
					if(!tag.equals("")&&!url.equals(domain)) {
						Request request=new Request(url);
						//透传上一页信息
						Map extras=new HashMap<>();
						extras.put("tag", tag);
						request.setExtras(extras);
						page.addTargetRequest(request);
					}
				}
			}
			
		//匹配标题分页url	
		}else if(page.getUrl().regex("http://www\\.27270\\.com/tag/\\d+.html").match()) {
			List<Selectable> list=page.getHtml().xpath("//ul[@id='Tag_list']/li/a").nodes();
			Request request=page.getRequest();
			Map extras=request.getExtras();
			for (Selectable selectable : list) {
				String titleUrl=selectable.xpath("/a/@href").toString();
				String title=selectable.xpath("/a/@title").toString();
/*			String titleUrl=list.get(0).xpath("/a/@href").toString();
			String title=list.get(0).xpath("/a/@title").toString();*/
				Request nextRequest=new Request(titleUrl);
				Map map=clone(extras);
				map.put("title", title);
				map.put("titleUrl", titleUrl);
				nextRequest.setExtras(map);
				page.addTargetRequest(nextRequest);
			}
			List<Selectable> urlList=page.getHtml().xpath("//div[@class='TagPage']/ul/li/a").nodes();
			for (Selectable selectable : urlList) {
				if(selectable.xpath("/a/text()").toString().contains("末页")) {
					String endPage=selectable.xpath("/a/@href").toString().split("_")[1].split("\\.")[0];
					for (int i = 1; i <= Integer.parseInt(endPage); i++) {
						//for (int i = 1; i <= 1; i++) {
						//String url=page.getUrl().toString().split("\\.html")[0]+"_"+i+".html";
						String url=page.getUrl().toString().replace(".html", "_"+i+".html");
						Request nextRequest=new Request(url);
						//nextRequest=new Request(url);
						//extras.put("url",url);
						nextRequest.setExtras(extras);
						page.addTargetRequest(nextRequest);
					}
				}
			}
		//匹配标题分页url	
		}else if(page.getUrl().regex("http://www\\.27270\\.com/tag/\\d+_\\d+.html").match()) {
			List<Selectable> list=page.getHtml().xpath("//ul[@id='Tag_list']/li/a").nodes();
			for (Selectable selectable : list) {
				String titleUrl=selectable.xpath("/a/@href").toString();
				String title=selectable.xpath("/a/@title").toString();
	/*			String titleUrl=list.get(0).xpath("/a/@href").toString();
				String title=list.get(0).xpath("/a/@title").toString();*/
				Request nextRequest=new Request(titleUrl);
				Request request=page.getRequest();
				Map extras=request.getExtras();
				Map map=clone(extras);
				map.put("title", title);
				map.put("titleUrl", titleUrl);
				nextRequest.setExtras(map);
				page.addTargetRequest(nextRequest);
			}
		//匹配详情页
		}else if(page.getUrl().regex("http://www\\.27270\\.com/ent/\\w+/\\d+/\\d+.html").match()){
			Request request=page.getRequest();
			Map extras=request.getExtras();
			List<Selectable> list=page.getHtml().xpath("//div[@class='page-tag oh']/ul/li/a").nodes();
			for (Selectable selectable : list) {
				if(selectable.xpath("/a/text()").toString().matches("\\d+")) {
				//	String url=page.getUrl().toString().split("\\.html")[0]+"_"+selectable.xpath("/a/text()").toString()+".html";
					if(null!=selectable.xpath("/a/text()")) {
						String url=page.getUrl().toString().replace(".html", "_"+selectable.xpath("/a/text()").toString()+".html");
						Map map=clone(extras);
						map.put("url",url);
						Request nextRequest=new Request(url);
						nextRequest.setExtras(map);
						page.addTargetRequest(nextRequest);
					}
					
				}
			}
			
		//匹配详情分页
		}else if(page.getUrl().regex("http://www\\.27270\\.com/ent/\\w+/\\d+/\\d+_\\d+.html").match()) {
			Request request=page.getRequest();
			Map extras=request.getExtras();
			String name=page.getHtml().xpath("//div[@class='warp oh']/h1/text()").toString();
			String address=page.getHtml().xpath("//div[@id=\"picBody\"]/p/a[1]/img/@src").toString();
			Map map=clone(extras);
			map.put("name", name);
			map.put("address", address);
			if(StringUtils.isNotBlank(name)&&StringUtils.isNotBlank(address)) {
				Image image=(Image) MapToObject.map2Object(map, Image.class);
				downloadService.download(image);
			}
		}

	}

	@Override
	public Site getSite() {
		return site;
	}
	
	
	public Map<String, String> clone(Map<String, String> map) {
		Map<String, String> map2=new HashMap<String, String>();
		for (Map.Entry<String, String> entry : map.entrySet()) { 
			map2.put(entry.getKey(), entry.getValue());
		}
		return  map2;
	}
}
