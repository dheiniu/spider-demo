package com.mysteel.spider.task;

import java.util.Arrays;

public enum StepTypeEnum {
	FIELD("1","属性"),EVENT("2","事件");

	public String CODE;
	public String TEXT;
	StepTypeEnum(String code, String text) {
		this.CODE = code;
		this.TEXT = text;
	}
	public static StepTypeEnum of(String code){
		return Arrays.stream(StepTypeEnum.values()).filter(item->item.CODE.equals(code)).findFirst().get();
	}
}
