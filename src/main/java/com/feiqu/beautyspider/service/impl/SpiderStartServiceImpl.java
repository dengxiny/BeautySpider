package com.feiqu.beautyspider.service.impl;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.feiqu.beautyspider.schuler.MyHashSetDuplicateRemover;
import com.feiqu.beautyspider.service.StartService;
import com.feiqu.beautyspider.service.UrlInitService;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Spider.Status;
import us.codecraft.webmagic.scheduler.QueueScheduler;

@Service
public class SpiderStartServiceImpl implements StartService{
	public static Logger logger = LoggerFactory.getLogger(SpiderStartServiceImpl.class);
	@Autowired
	private PageProcesser pageProcesser;
	@Autowired
	private UrlInitService urlInitService;

	@Override
	public void start() {
		Spider spider=Spider.create(pageProcesser).addUrl("http://www.27270.com/tag/").thread(10);
		//增量爬取 默认从文件读
		QueueScheduler queueScheduler=new QueueScheduler();
		MyHashSetDuplicateRemover myHashSetDuplicateRemover=new MyHashSetDuplicateRemover();
		List<String> list=urlInitService.urlInit();
		if(!CollectionUtils.isEmpty(list)) {
			myHashSetDuplicateRemover.initUrls(list);
		}
		queueScheduler.setDuplicateRemover(myHashSetDuplicateRemover);
		spider.setScheduler(queueScheduler).run();
		if(spider.getStatus()!=Status.Stopped) {
			logger.info("run status error");
		}
		
	}

}
