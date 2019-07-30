package com.mysteel.spider.task;

import java.util.Arrays;

public enum PagingTypeEnum {
	SEQ("1","序列"),ROLL("2","滚动"),CLICK("3","点击");

	public String CODE;
	public String TEXT;
	PagingTypeEnum(String code, String text) {
		this.CODE = code;
		this.TEXT = text;
	}
	public static PagingTypeEnum of(String code){
		return Arrays.stream(PagingTypeEnum.values()).filter(item->item.CODE.equals(code)).findFirst().get();
	}
}
