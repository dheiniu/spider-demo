package com.mysteel.spider.consumer.html;


import com.mysteel.spider.consumer.Parser;
import com.mysteel.spider.entity.BaseConfig;
import com.mysteel.spider.entity.ResultMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:html解析器
 * @className: HParser
 * @see： com.mysteel.spider.consumer.html
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:06
 */
@Component
public class HParser implements Parser {

  private static Logger logger = LoggerFactory.getLogger(HParser.class);


  @Override
  public List<ResultMap> execute(BaseConfig baseConfig) {
    logger.info("【Html执行器】:{}", baseConfig);
    ResultMap  result = new ResultMap();
    result.put("name","黄志杰");
    List<ResultMap> list = new ArrayList();
    list.add(result);
    return list;
  }
}
