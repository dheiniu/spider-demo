package com.mysteel.spider.util;

import java.util.Arrays;

public enum ExpressionTypeEnum {
	CSS("1", "CSS"), XPATH("2", "XPath");

	public String CODE;
	public String TEXT;

	ExpressionTypeEnum(String code, String text) {
		this.CODE = code;
		this.TEXT = text;
	}

	public static ExpressionTypeEnum of(String code) {
		return Arrays.stream(ExpressionTypeEnum.values()).filter(item -> item.CODE.equals(code)).findFirst().get();
	}
}
