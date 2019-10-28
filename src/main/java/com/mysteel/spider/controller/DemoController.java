package com.mysteel.spider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @version V1.0
 * @description:${TODO}
 * @projectName spider-demo
 * @className: DemoController
 * @see：com.mysteel.spider.controller
 * @author: 资讯研发部-黄志杰
 * @date: 2019/8/23 15:08
 */
@Controller
@RequestMapping("/index")
public class DemoController {

  @RequestMapping("/demo")
  public String demo(ModelMap map){
    map.addAttribute("name","huangzj");
    map.addAttribute("time",new Date());
    return "demo";
  }
}
