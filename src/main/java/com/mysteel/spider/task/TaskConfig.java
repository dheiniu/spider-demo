package com.mysteel.spider.task;

import com.mysteel.spider.entity.BaseConfig;
import lombok.Data;

import java.util.List;

@Data
public class TaskConfig extends BaseConfig {
	private String taskType;
	private TaskConfig childTaskConfig;
	private String url;
	private List<StepConfig> downloadConfig;

	private List<StepConfig> extractConfig;
	private PagingConfig pagingConfig;
	private List<StepConfig> formatConfig;

}
