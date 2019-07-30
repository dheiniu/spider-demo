package com.mysteel.spider.task;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TaskTypeEnum {

	LIST("1", "列表"),
  CONTENT("2", "内容");

	public String CODE;
	public String TEXT;

	TaskTypeEnum(String code, String text) {
		this.CODE = code;
		this.TEXT = text;
	}

	public static TaskTypeEnum of(String code) {
		return Arrays.stream(TaskTypeEnum.values()).filter(item -> item.CODE.equals(code)).findFirst().get();
	}
}
