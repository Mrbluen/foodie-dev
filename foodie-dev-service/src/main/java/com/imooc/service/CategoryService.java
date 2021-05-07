package com.imooc.service;

import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    //查询所有轮播图列表
    public List<Category> queryAllRootLevelCat();
    //根据一级分类信息查询子分类信息
    public List<CategoryVO> getSubCatList(Integer rootCatId);

}
