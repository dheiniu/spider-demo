package com.mysteel.spider.task;

import java.util.Arrays;

public enum HandleTypeEnum {
	PERSIST("1","持久化"),REDIRECT("2","重定向");

	public String CODE;
	public String TEXT;
	HandleTypeEnum(String code, String text) {
		this.CODE = code;
		this.TEXT = text;
	}
	public static HandleTypeEnum of(String code){
		return Arrays.stream(HandleTypeEnum.values()).filter(item->item.CODE.equals(code)).findFirst().get();
	}
}
