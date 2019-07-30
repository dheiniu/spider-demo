package com.mysteel.spider.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description:配置文件信息
 * @className: Config
 * @see： com.mysteel.spider.constant
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:34
 */
@Component
@Data
public class Config {

  /**
   * 线程池大小
   */
  @Value("${executor.pool.size}")
  private Integer executorSize;

  /**
   * 积分数
   */
  @Value("${engine.score.count}")
  private Integer scoreCount;

  /**
   * html积分占比
   */
  @Value("${engine.score.html}")
  private Integer htmlScore;

  /**
   * selenium积分占比
   */
  @Value("${engine.score.selenium}")
  private Integer seleniumScore;

  /**
   * 优先执行
   */
  @Value("${engine.driver.key}")
  private String precedence;

  /**
   * 游览器可视化
   */
  @Value("${brower.visualization}")
  private Boolean browerVisualization;

  /**
   * 浏览器引擎选项
   */
  @Value("${brower.use}")
  private String browserDriverKey;

  /**
   * 火狐驱动
   */
  @Value("${brower.geckodriver.path}")
  private String  geckodriverPath;

  /**
   * 谷歌驱动
   */
  @Value("${brower.chromedriver.path}")
  private String  chromedriverPath;

  /**
   * 游览器是否启用代理
   */
  @Value("${browser.proxy.open}")
  private Boolean browserProxyOpen;

  /**
   * IP
   */
  @Value("${browser.proxy.ip}")
  private String browserProxyIp;

  /**
   * 端口
   */
  @Value("${browser.proxy.host}")
  private String browserProxyHost;

}
