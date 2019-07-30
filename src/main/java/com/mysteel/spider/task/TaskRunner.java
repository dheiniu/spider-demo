package com.mysteel.spider.task;

import cn.hutool.core.collection.CollUtil;
import com.mysteel.spider.entity.ResultMap;
import com.mysteel.spider.util.ExpressionTypeEnum;
import com.mysteel.spider.util.SeleniumUtil;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TaskRunner implements Callable<List<ResultMap>> {
	private TaskConfig config;
	private WebDriver driver;

	@Override
	public List<ResultMap> call() {
		try {
			Item item = new Item();
			PagingConfig pagingConfig = config.getPagingConfig();
			do {
				//下载
				String htmlPage = download(config.getDownloadConfig());
				//抽取
				Item partition = extract(config.getExtractConfig(), htmlPage);
				item.append(partition);
				//翻页
				paging();
			} while (isPaging());
			//转换
			item = format(config.getDownloadConfig(), item);
			// todo
//			item.get("url").forEach(System.out::println);
//			item.get("title").forEach(System.out::println);
//			item.get("content").forEach(System.out::println);
      List<ResultMap> resultMapList = new ArrayList<>();
      item.get("url").forEach( s -> {
        resultMapList.add(new ResultMap("url",String.valueOf(s)));
      });
      return resultMapList;
		} finally {
			driver.quit();
		}
	}

	private boolean isPaging() {
		PagingConfig paging = config.getPagingConfig();
		return paging != null && paging.limit() > 0;
	}

	private String download(List<StepConfig> downloadConfig) {
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

	private Item extract(List<StepConfig> extractConfig, String htmlPage) {
		Document doc = Jsoup.parse(htmlPage);
		doc.setBaseUri(config.getUrl());
		Item item = new Item();
		if (CollUtil.isNotEmpty(extractConfig)) {
			for (StepConfig step : extractConfig) {
				Elements elements = Xsoup.compile(step.getExpression()).evaluate(doc).getElements();
				List<String> values = elements.stream().map(el -> getVal(el,step.getResultType())).collect(Collectors.toList());
				item.put(step.getName(), values);
			}
		}
		return item;
	}

	private String getVal(Element el,String resultType){
		switch (resultType){
			case "html":return el.html();
			case "text":return el.text();
			default: return el.attr(resultType);
		}
	}

	private Item format(List<StepConfig> format, Item item) {
		return item;
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
