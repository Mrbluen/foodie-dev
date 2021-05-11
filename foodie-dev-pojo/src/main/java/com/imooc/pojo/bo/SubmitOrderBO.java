package com.imooc.pojo.bo;
//用于创建订单的BO对象
public class SubmitOrderBO {
    private String userId;
    private String itemSpecIds;
    private String choosedAddressId;
    private Integer choosedPayMethod;
    private String leftMsg;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemSpecIds() {
        return itemSpecIds;
    }

    public void setItemSpecIds(String itemSpecIds) {
        this.itemSpecIds = itemSpecIds;
    }

    public String getChoosedAddressId() {
        return choosedAddressId;
    }

    public void setChoosedAddressId(String choosedAddressId) {
        this.choosedAddressId = choosedAddressId;
    }

    public Integer getChoosedPayMethod() {
        return choosedPayMethod;
    }

    public void setChoosedPayMethod(Integer choosedPayMethod) {
        this.choosedPayMethod = choosedPayMethod;
    }

    public String getLeftMsg() {
        return leftMsg;
    }

    public void setLeftMsg(String leftMsg) {
        this.leftMsg = leftMsg;
    }
}
