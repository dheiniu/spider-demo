package com.mysteel.spider.task;

import cn.hutool.core.collection.CollUtil;
import com.mysteel.spider.entity.ResultMap;
import com.mysteel.spider.provider.RabbitMqMessageSend;
import com.mysteel.spider.util.ExpressionTypeEnum;
import com.mysteel.spider.util.SeleniumUtil;
import com.mysteel.spider.util.SpringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class TaskRunner implements Callable<List<ResultMap>> {
	private TaskConfig config;
	private WebDriver driver;

	@Override
	public List<ResultMap> call() {
		try {
			List<ResultMap> resultMaps = new ArrayList<>();
			do {
				//下载
				String htmlPage = download();
				//抽取
				List<ResultMap> partition = extract(htmlPage);
				resultMaps.addAll(partition);
				//翻页
				paging();
			} while (isPaging());
			//转换
			resultMaps = format(config.getDownloadConfig(), resultMaps);
			pipLine(resultMaps);
			return resultMaps;
		} catch (Exception e) {
			log.error("一点小问题:" + e.getMessage(), e);
		} finally {
			driver.quit();
		}
		return Collections.emptyList();
	}

	private boolean isPaging() {
		PagingConfig paging = config.getPagingConfig();
		return paging != null && paging.limit() > 0;
	}

	private String download() {
		List<StepConfig> downloadConfig = config.getDownloadConfig();
		driver.get(config.getUrl());
		SeleniumUtil.sleep(300);
		if (CollUtil.isNotEmpty(downloadConfig)) {
			for (StepConfig step : downloadConfig) {
				//等待指定的元素加载
				new WebDriverWait(driver, 2)
						.until(dr -> dr.findElement(SeleniumUtil.by(ExpressionTypeEnum.XPATH.CODE, step.getExpression())) != null);
				//触发点击事件
				driver.findElement(SeleniumUtil.by(ExpressionTypeEnum.XPATH.CODE, step.getExpression())).click();
				SeleniumUtil.sleep(300);
			}
		}
		return driver.getPageSource();
	}

	private List<ResultMap> extract(String htmlPage) {
		List<StepConfig> extractConfig = config.getExtractConfig();
		Document doc = Jsoup.parse(htmlPage);
		doc.setBaseUri(config.getUrl());
		ArrayList<ResultMap> resultMaps = new ArrayList<>();
		if (CollUtil.isNotEmpty(extractConfig)) {
			for (StepConfig step : extractConfig) {
				Elements elements = Xsoup.compile(step.getExpression()).evaluate(doc).getElements();
				elements.stream().map(el -> getVal(el, step.getResultType())).forEach(val -> {
					ResultMap resultMap = new ResultMap();
					resultMap.put(step.getName(), val);
					resultMaps.add(resultMap);
				});
			}
		}
		return resultMaps;
	}


	private String getVal(Element el, String resultType) {
		switch (resultType) {
			case "html":
				return el.html();
			case "text":
				return el.text();
			default:
				return el.attr(resultType);
		}
	}

	private void pipLine(List<ResultMap> resultMaps) {
		switch (HandleTypeEnum.of(config.getHandleType())) {
			case PERSIST:
				// todo persist
				System.out.println(resultMaps);
				break;
			case REDIRECT:
				for (ResultMap resultMap : resultMaps) {
					TaskConfig childConfig = config.getChildTaskConfig();
					childConfig.setUrl(resultMap.get("url"));
					RabbitMqMessageSend msgSend = SpringUtil.getBean(RabbitMqMessageSend.class);
					msgSend.sendMessage(childConfig, childConfig.getTaskType(), childConfig.getPriority());
				}
				break;
		}
	}

	private List<ResultMap> format(List<StepConfig> format, List<ResultMap> resultMaps) {
//		config.getHandleType()
		return resultMaps;
	}

	private void paging() {
		if (!isPaging()) {
			return;
		}
		PagingConfig paging = config.getPagingConfig();
		switch (PagingTypeEnum.of(paging.getPagingType())) {
			case SEQ:
				SequencePagingConfig seq = (SequencePagingConfig) paging;
				List<String> urls = seq.getUrls();
				String url = urls.get(0);
				urls.remove(url);
				config.setUrl(url);
				break;
			case ROLL:
				break;
			default:
				break;
		}
	}
}
