package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;

import java.util.List;

public interface ItemService {
    //根据商品Id查询商品详情
    public Items queryItemById(String itemId);
    //根据商品Id查询商品图片列表
    public List<ItemsImg> queryItemImgList(String itemId);
    //根据商品Id查询商品规格
    public List<ItemsSpec> queryItemSpecList(String itemId);
    //根据商品Id查询商品参数
    public ItemsParam queryItemParam(String itemId);
}
