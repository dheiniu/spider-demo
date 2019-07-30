package com.mysteel.spider;

import cn.hutool.core.collection.CollUtil;
import com.mysteel.spider.constant.QueueEnum;
import com.mysteel.spider.constant.STaskType;
import com.mysteel.spider.constant.STemplateEnum;
import com.mysteel.spider.constant.TaskWayEnum;
import com.mysteel.spider.entity.TemplateConfig;
import com.mysteel.spider.provider.RabbitMqMessageSend;
import com.mysteel.spider.task.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Scanner;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

  private static final String SELENIUM_TEST = QueueEnum.SELENIUM_QUEUE.getSendRoutingKey();

  private static final String HTML_TEST = QueueEnum.HTML_QUEUE.getSendRoutingKey();

  @Autowired
  RabbitMqMessageSend rabbitMqMessageSend;


  @Test
  public void startUp() {
    Thread task = new Thread(new Runnable() {
      @Override
      public void run() {
        //模板任务，解决特殊网站的爬取
//        sendSeleniumTemplateTask();
        //用户自定义流程任务， 待完善
        sendSeleniumStepTask();
      }
    });
    task.start();
    //启动容器，开启mq消费
    new Scanner(System.in).next();
  }


  /**
   * html任务
   */
  public void sendHtmlTask(){
    TemplateConfig templateConfig  = new TemplateConfig();
    templateConfig.setUuid(UUID.randomUUID().toString());
    templateConfig.setTaskType(TaskWayEnum.HTML.getType());
    rabbitMqMessageSend.sendMessage(templateConfig,HTML_TEST,3);
  }


  /**
   * selenium模板任务
   */
  public void sendSeleniumTemplateTask(){
    //微信模板
    TemplateConfig weiXin  = new TemplateConfig();
    weiXin.setUuid(UUID.randomUUID().toString());
    weiXin.setTaskType(TaskWayEnum.SELENIUM.getType());
    weiXin.setSType(STaskType.TEMPLATE.getType());
    weiXin.setTName(STemplateEnum.WEI_XIN.getName());
    weiXin.setDomain(STemplateEnum.WEI_XIN.getDomain());
    weiXin.setIsProxy(true);
    weiXin.setContainSubTask(false);
    weiXin.setPriority(1);
    rabbitMqMessageSend.sendMessage(weiXin,SELENIUM_TEST,weiXin.getPriority());


    //掘金
    TemplateConfig juejing  = new TemplateConfig();
    juejing.setUuid(UUID.randomUUID().toString());
    juejing.setTaskType(TaskWayEnum.SELENIUM.getType());
    juejing.setSType(STaskType.TEMPLATE.getType());
    juejing.setTName(STemplateEnum.JUE_JIN.getName());
    juejing.setDomain(STemplateEnum.JUE_JIN.getDomain());
    juejing.setContainSubTask(true);
    juejing.setPriority(2);
    juejing.setIsProxy(true);
    rabbitMqMessageSend.sendMessage(juejing,SELENIUM_TEST,juejing.getPriority());


    //天沃科技
    TemplateConfig tw  = new TemplateConfig();
    tw.setUuid(UUID.randomUUID().toString());
    tw.setTaskType(TaskWayEnum.SELENIUM.getType());
    tw.setSType(STaskType.TEMPLATE.getType());
    tw.setTName(STemplateEnum.THVOW_COM.getName());
    tw.setDomain(STemplateEnum.THVOW_COM.getDomain());
    tw.setContainSubTask(true);
    tw.setIsProxy(true);
    tw.setPriority(3);
    rabbitMqMessageSend.sendMessage(tw,SELENIUM_TEST,tw.getPriority());

    //东风汽车股份
    TemplateConfig df  = new TemplateConfig();
    df.setUuid(UUID.randomUUID().toString());
    df.setTaskType(TaskWayEnum.SELENIUM.getType());
    df.setSType(STaskType.TEMPLATE.getType());
    df.setTName(STemplateEnum.DFAC_COM.getName());
    df.setDomain(STemplateEnum.DFAC_COM.getDomain());
    df.setContainSubTask(true);
    df.setIsProxy(true);
    df.setPriority(4);
    rabbitMqMessageSend.sendMessage(df,SELENIUM_TEST,df.getPriority());


    //搜狗微信(反扒策略-验证码)
    TemplateConfig sg  = new TemplateConfig();
    sg.setUuid(UUID.randomUUID().toString());
    sg.setTaskType(TaskWayEnum.SELENIUM.getType());
    sg.setSType(STaskType.TEMPLATE.getType());
    sg.setTName(STemplateEnum.SOU_GOU_WEI_XIN.getName());
    sg.setDomain(STemplateEnum.SOU_GOU_WEI_XIN.getDomain());
    sg.setContainSubTask(true);
    sg.setIsProxy(true);
    sg.setPriority(5);
    rabbitMqMessageSend.sendMessage(sg,SELENIUM_TEST,sg.getPriority());

  }

  /**
   * selenium步骤任务
   */
  public void sendSeleniumStepTask(){
    // 列表配置
    TaskConfig taskConfig = new TaskConfig();
    taskConfig.setHandleType(HandleTypeEnum.REDIRECT.CODE);
    StepConfig stepConfig = StepConfig.builder()
      .name("url")
      .resultType("href")
      .StepType(StepTypeEnum.FIELD.CODE)
      .expression("//html/body/div/div/div[2]/div[2]/div/div[2]/div/div/div/p/span[2]/a")
      .build();
    taskConfig.setExtractConfig(CollUtil.newArrayList(stepConfig));
    PagingConfig pagingConfig = new SequencePagingConfig(1,10,1,"http://blog.sina.com.cn/s/articlelist_1455643221_0_{}.html");
    taskConfig.setPagingConfig(pagingConfig);
    taskConfig.setUrl("http://blog.sina.com.cn/s/articlelist_1455643221_0_1.html");
    // 内容配置
    TaskConfig childTaskConfig = new TaskConfig();
    childTaskConfig.setUrl("http://blog.sina.com.cn/s/blog_56c35a550102yw8c.html");
    StepConfig titleConfig = StepConfig.builder()
      .name("title")
      .resultType("text")
      .StepType(StepTypeEnum.FIELD.CODE)
      .expression("//*[@class='articalTitle']")
      .build();
    StepConfig contentConfig = StepConfig.builder()
      .name("content")
      .resultType("html")
      .StepType(StepTypeEnum.FIELD.CODE)
      .expression("//*[@id=\"sina_keyword_ad_area2\"]")
      .build();
    childTaskConfig.setExtractConfig(CollUtil.newArrayList(titleConfig,contentConfig));
    childTaskConfig.setHandleType(HandleTypeEnum.PERSIST.CODE);
    childTaskConfig.setTaskType(SELENIUM_TEST);
    taskConfig.setChildTaskConfig(childTaskConfig);

    //配置
    taskConfig.setUuid(UUID.randomUUID().toString());
    taskConfig.setTaskType(TaskWayEnum.SELENIUM.getType());
    taskConfig.setSType(STaskType.STEP.getType());

    rabbitMqMessageSend.sendMessage(taskConfig,SELENIUM_TEST,2);
  }
}
