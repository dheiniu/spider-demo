package com.mysteel.spider.consumer;


import com.mysteel.spider.entity.BaseConfig;
import com.mysteel.spider.entity.ResultMap;

import java.util.List;

/**
 * @version V1.0
 * @description:${TODO}
 * @projectName spring-boot-demo
 * @className: ${TYPE_NAME}
 * @see：com.example.demo.rabbitmq.service
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/10 9:55
 */
public interface Parser {

  List<ResultMap> execute(BaseConfig baseConfig);

  //Article execute(TemplateConfig spiderConfig);
}
