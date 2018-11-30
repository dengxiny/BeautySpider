package com.feiqu.beautyspider.schuler;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

public class MyHashSetDuplicateRemover implements DuplicateRemover {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return !urls.add(getUrl(request));
    }

    protected String getUrl(Request request) {
        return request.getUrl();
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return urls.size();
    }
    
    public void initUrlsRequest(List<Request> list) {
    	for (Request request : list) {
    		urls.add(getUrl(request));
		}
    }
    
    public void initUrls(List<String> list) {
    	for (String url : list) {
    		urls.add(url);
		}
    }
}
