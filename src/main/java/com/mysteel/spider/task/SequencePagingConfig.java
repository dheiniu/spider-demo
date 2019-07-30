package com.mysteel.spider.task;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 序列分页
 */
@Data
public class SequencePagingConfig extends PagingConfig {
	private List<String> urls;

	public SequencePagingConfig(int offset, int limit, int step, String templateUrl) {
		this.urls = Stream.iterate(offset, i -> i + step).limit(limit).map(i -> StrUtil.format(templateUrl, i)).collect(Collectors.toList());
		urls.forEach(System.out::println);
		this.setPagingType(PagingTypeEnum.SEQ.CODE);
	}

	@Override
	int limit() {
		return urls.size();
	}
}
