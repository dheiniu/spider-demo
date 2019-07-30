package com.mysteel.spider.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StepConfig {
	private Long id;
	private String name;
	private String StepType;
	private String expression;
	private String resultType;
}
