package com.imooc.service.impl;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;

import java.util.List;

public interface CategoryService {
    //查询所有轮播图列表
    public List<Category> queryAllRootLevelCat();
}
