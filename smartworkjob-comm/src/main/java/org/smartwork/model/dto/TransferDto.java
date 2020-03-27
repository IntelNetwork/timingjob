package org.smartwork.model.dto;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.forbes.comm.vo.Result;
import org.forbes.pay.comm.model.PayeeInfo;
import java.io.Serializable;
import java.util.Map;
@Data
@Accessors(chain = true)
@ApiModel(description = "转账请求对象")
public class TransferDto implements Serializable {


    private static final long serialVersionUID = -569719186361363071L;


    @ApiModelProperty(value = "商户ID")
    private String mchId;

    @ApiModelProperty(value = "渠道ID")
    private String channelId;


    @ApiModelProperty(value = "币种")
    private  String currency;


    @ApiModelProperty(value = "设备")
    private  String device;

    @ApiModelProperty(value = "产品编码")
    private String productCode;

    @ApiModelProperty(value = "通知地址")
    private String notifyUrl;

    @ApiModelProperty(value = "商户订单号")
    private String outBizNo;


    @ApiModelProperty(value = "订单总金额，单位为元，精确到小数点后两位，")
    private String transAmount;

    @ApiModelProperty(value = "描述特定的业务场景")
    private String bizScene;

    @ApiModelProperty(value = "转账业务的标题")
    private  String orderTitle;


    @ApiModelProperty(value = "收款方信息")
    public PayeeInfo payeeInfo;


    /***
     * 建造模式
     */
    public static class TransferDtoBuild {

        /**
         *
         * @return
         */
       public static TransferDto build(){
            return new TransferDto();
        }
    }
}
