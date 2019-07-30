package com.mysteel.spider.task;

import cn.hutool.core.collection.CollUtil;

import java.util.HashMap;
import java.util.List;

public class Item extends HashMap<String, List> {
	public void append(Item partition) {
		for (Entry<String,List> kv:partition.entrySet()){
			List list = this.get(kv.getKey());
			if(CollUtil.isEmpty(list)){
				this.put(kv.getKey(),kv.getValue());
			}else{
				list.addAll(kv.getValue());
				this.put(kv.getKey(),list);
			}
		}
	}
}
