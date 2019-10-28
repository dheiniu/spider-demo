package com.mysteel.spider.demo.bean;

import cn.hutool.script.ScriptUtil;
import lombok.Data;

import javax.script.SimpleBindings;
import java.util.Date;

/**
 * @version V1.0
 * @description:文章实体
 * @projectName spider-demo
 * @className: Article
 * @see：com.mysteel.spider.demo.bean
 * @author: 资讯研发部-黄志杰
 * @date: 2019/8/29 9:33
 */
@Data
public class Article {

  private String title;

  private String author;

  private Date date;

  private Integer pageView;

  private String content;



  private static Boolean checkTheSpecification(Article article,String exprStr){



    return false;
  }


  public static void main(String[] args) {
    SimpleBindings simpleBindings = new SimpleBindings();
    simpleBindings.put("v1",true);
    simpleBindings.put("v2",true);
    simpleBindings.put("v3",false);
    simpleBindings.put("v4",true);
    String exprStr = "(v1 || v2) && (v3 || v4)";
    Boolean result = (Boolean) ScriptUtil.eval(exprStr,simpleBindings);
    System.out.println(result);

    String str = "abc";

    System.out.println( str.replace("a","1"));


    //Article.checkTheSpecification(null,exprStr);
  }



}
