package org.smartwork.service;

import io.swagger.annotations.ApiOperation;
import org.forbes.comm.vo.Result;
import org.smartwork.model.dto.TransferDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/***
 * 转账订单
 */
@ConditionalOnProperty(name = "spring.application.paycenter")
@FeignClient(name = "transferApiOrderService",url="http://${spring.application.paycenter}",path = "/api/v1.0/transfer")
public interface ITransferApiOrderService {


    /***
     * 创建转账订单
     * @return
     */
    @RequestMapping(value = "/create-order",method = RequestMethod.POST)
    public Result<Map<String,Object>> transferOrder(@RequestBody TransferDto transferDto);
}
