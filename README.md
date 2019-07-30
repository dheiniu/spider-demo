## 爬虫架构Demo


### 前言


### 版本说明
- Spider-demo v1.0
### 运行必备环境
- JDK 8 181 + 或 OpenJDK 11 +
- Maven 3.5.3 +
- RabbitMq 3.7.8
### 技术选型
- 后端框架：SpringBoot
- 消息中间件：RabbitMq
- HTML解析器：JsoupXpath
- 自动化测试工具：Selenium
- 工具类：Hutool
- 第三方：联众打码

### 模块

项目名 | 介绍
---|---
spider-demo | 包含了整个业务(因为方便写demo所有没有分出具体木块)
spider-proxy | 代理服务器，根据业务进行了改造  https://github.com/monkeyWie/proxyee


### 使用
#### 单元测试
```java

  @Test
  public void startUp() {
    Thread task = new Thread(new Runnable() {
      @Override
      public void run() {
        //模板任务，解决特殊网站的爬取
        sendSeleniumTemplateTask();
        //用户自定义流程任务， 待完善
        //sendSeleniumStepTask();
      }
    });
    task.start();
    //启动容器，开启mq消费
    new Scanner(System.in).next();
  }

```
#### 用户自定义流程网站爬取
待续  

#### 特殊网址爬取
1. 在 STemplateEnum 添加配置   

```java
@Getter
public enum  STemplateEnum {

  
  /**
   * DfacTemplate
   */
  DFAC_COM("东风汽车股份有限公司",4,"dfacTemplate","http://www.dfac.com/cn/News/list_32.aspx"),


  /**
   * 模板名称
   */
  private String name;

  /**
   * 模板code
   */
  private Integer code;
  /**
   * 对应的类(很重要)
   */
  private String clazzName;

  /**
   * 对应网站的URL
   */
  private String domain;


  STemplateEnum(String name, Integer code,String clazzName,String domain) {
    this.name = name;
    this.clazzName = clazzName;
    this.code = code;
    this.domain = domain;
  }
```  

2. 实现 TemplateStrategy 接口, 在实现类中编写爬取过程，该过程统一由Selenium完成，不一定要按照我的思路来写  


```java
 //记得在STemplateEnum配置clazzName
 @Component("dfacTemplate")
public class DfacTemplate implements TemplateStrategy {

  /**
   * 请求地址
   */
  private static final String DOMAIN = "http://www.dfac.com/cn/News/list_32.aspx";

  /**
   * 列表xpath
   */
  private static final String LIST_URL_XPATH = "//li/h2[@class='tit']/a";

  /**
   * 标题xpath
   */
  private static final String TITLE_XPATH = "//h2[@class='tit']";

  /**
   * 时间xpath
   */
  private static final String  TIME_XPATH = "//div[@class='date']";


  @Autowired
  private RabbitMqMessageSend rabbitMqMessageSend;

  @Override
  public List<ResultMap> get(TemplateConfig templateConfig, PooledWebDriver webDriver) {
    try {
      if(DOMAIN.equals(templateConfig.getDomain()) && templateConfig.getContainSubTask()){
        webDriver.get(DOMAIN);
        //滚动到底部翻页，  这里只操作了翻一页，具体翻多少页，看业务
        webDriver.getJsExecutor().executeScript("window.scrollTo(0, document.body.scrollHeight)");
        //等待2秒
        Thread.sleep(2000);
        //获取列表链接
        List<WebElement> els =webDriver.findElements(By.xpath(LIST_URL_XPATH));
        for (WebElement el : els){
          String url = el.getAttribute("href");
          if(!StringUtils.isEmpty(url)){
            //生成子任务,推送队列
            TemplateConfig t  = new TemplateConfig();
            BeanUtils.copyProperties(templateConfig,t);
            //优先级设置
            Integer priority = t.getPriority()*10;
            //该子任务是否还存在子任务
            t.setContainSubTask(false);
            //目标网址
            t.setDomain(url);
            t.setPriority(priority);
            rabbitMqMessageSend.sendMessage(t,QueueEnum.SELENIUM_QUEUE.getSendRoutingKey(), t.getPriority());
          }
        }
        return null;
      }else{
        //解析内容
        webDriver.get(templateConfig.getDomain());
        Article article = Article.builder()
          .title(byXpath(TITLE_XPATH,webDriver))
          .time(byXpath(TIME_XPATH,webDriver))
          .url(webDriver.getCurrentUrl())
          .build();
        List<ResultMap> result = new ArrayList<>();
        result.add(ResultMapUtil.entityToMap(article));
        return result;
      }
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }

  }

  private String byXpath(String xpath,PooledWebDriver webDriver){
    try {
      return webDriver.findElement(By.xpath(xpath)).getText();
    }catch (Exception e){
      return null;
    }
  }
}
```  
3. 推送任务至队列  

```java
//天沃科技
TemplateConfig tw  = new TemplateConfig();
tw.setUuid(UUID.randomUUID().toString());
tw.setTaskType(TaskWayEnum.SELENIUM.getType());
tw.setSType(STaskType.TEMPLATE.getType());
tw.setTName(STemplateEnum.THVOW_COM.getName());
tw.setDomain(STemplateEnum.THVOW_COM.getDomain());
tw.setContainSubTask(true);
//任务级别
tw.setPriority(3);
rabbitMqMessageSend.sendMessage(tw,SELENIUM_TEST,tw.getPriority());
```

  
  
 
