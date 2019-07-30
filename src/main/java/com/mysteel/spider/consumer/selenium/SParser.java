package com.mysteel.spider.consumer.selenium;


import com.mysteel.spider.constant.*;
import com.mysteel.spider.consumer.Parser;
import com.mysteel.spider.consumer.selenium.pool.BrowserDriver;
import com.mysteel.spider.consumer.selenium.pool.ChromeDriverBean;
import com.mysteel.spider.consumer.selenium.pool.FirefoxDriverBean;
import com.mysteel.spider.consumer.selenium.pool.PooledWebDriver;
import com.mysteel.spider.consumer.selenium.template.TemplateContext;
import com.mysteel.spider.consumer.selenium.template.TemplateStrategy;
import com.mysteel.spider.entity.BaseConfig;
import com.mysteel.spider.entity.BrowerProxy;
import com.mysteel.spider.entity.ResultMap;
import com.mysteel.spider.entity.TemplateConfig;
import com.mysteel.spider.task.TaskConfig;
import com.mysteel.spider.task.TaskRunner;
import com.mysteel.spider.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * @description:selenium解析器
 * @className: SParser
 * @see： com.mysteel.spider.consumer.selenium
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:07
 */
@Component
public class SParser implements Parser {

  private static Logger logger = LoggerFactory.getLogger(SParser.class);

  //浏览器
  private BrowserDriver browserDriver;


  @Autowired
  private Config config;

  @Autowired
  private RedisTemplate redisTemplate;


  @Override
  public List<ResultMap> execute(BaseConfig baseConfig) {
    logger.info("【Selenium执行器】:{}", baseConfig);
    //加载游览器
    if(BrowserDriverEnum.FIRE_FOX.getKey().equals(config.getBrowserDriverKey())){
      this.browserDriver =  SpringUtil.getBean(FirefoxDriverBean.class);
    }else{
      this.browserDriver =  SpringUtil.getBean(ChromeDriverBean.class);
    }
    try (PooledWebDriver driver = browserDriver.get()) {
      //使用代理
      if(Boolean.TRUE.equals(baseConfig.getIsProxy())){
        BrowerProxy browerProxy = BrowerProxy.builder()
          .browerId(driver.getId())
          .taskUuid(baseConfig.getUuid())
          .cookies(baseConfig.getCookies())
          .headers(baseConfig.getHeaders())
          .userAgent(baseConfig.getUserAgent())
          .build();
        //设置redis
        redisTemplate.opsForValue().set(RedisKey.BROWER_KEY+driver.getId(),browerProxy);
      }
      //
      if(STaskType.TEMPLATE.getType().equals(baseConfig.getSType())){
        return executeTemplate((TemplateConfig)baseConfig,driver);
      }else{
        return executeStep((TaskConfig)baseConfig,driver);
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("ERROR:{}", baseConfig.toString());
      return null;
    }
  }

  /**
   * 执行模板返回结果集
   * @param driver
   * @return
   */
  private List<ResultMap> executeTemplate(TemplateConfig templateConfig, PooledWebDriver driver){
  //选择相应的模板执行任务
    TemplateContext templateContext = null;
    for(STemplateEnum sTemplateEnum : STemplateEnum.values()){
      if(sTemplateEnum.getName().equals(templateConfig.getTName())){
        //加载bean
        templateContext = new TemplateContext(SpringUtil.getBean(sTemplateEnum.getClazzName(), TemplateStrategy.class));
      }
    }
    //结果集返回
    return templateContext == null ? new ArrayList<>() : templateContext.getResult(templateConfig,driver);
  }


  /**
   * 执行步骤
   * @param taskConfig
   * @param driver
   * @return
   */
  private List<ResultMap> executeStep(TaskConfig taskConfig, PooledWebDriver driver){
    return new TaskRunner(taskConfig,driver).call();
  }
}

