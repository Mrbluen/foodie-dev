package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.utils.PagedGridResult;

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

    //根据商品id查询商品的评价等级
    public CommentLevelCountsVO queryCommentCounts(String itemId);
    //根据商品id查询商品的评价(分页)
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    //搜索商品列表
    public PagedGridResult searhItems(String keywords, String  sort, Integer page, Integer pageSize);
}
