package com.mysteel.spider.util;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

@Slf4j
public class SeleniumUtil {
	/**
	 * 构建选择器
	 *
	 * @param expressionType
	 * @param expression
	 * @return
	 */
	public static By by(String expressionType, String expression) {
		switch (ExpressionTypeEnum.of(expressionType)) {
			case CSS:
				return By.cssSelector(expression);
			case XPATH:
				return By.xpath(expression);
			default:
				throw new RuntimeException();
		}
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.warn("把异常吃了", e);
		}
	}
}
