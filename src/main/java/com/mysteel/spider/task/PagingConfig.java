package com.mysteel.spider.task;

import lombok.Data;

@Data
public abstract class PagingConfig {
	private String pagingType;
	abstract int limit();
}
