package com.imooc.controller;

import com.imooc.service.CarouselService;
import common.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "首页", tags = "首页展示的相关接口")
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    public IMOOCJSONResult carousel(){


        return IMOOCJSONResult.ok();
    }
}

