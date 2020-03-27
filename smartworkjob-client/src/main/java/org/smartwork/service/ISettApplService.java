package org.smartwork.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.models.auth.In;
import org.forbes.comm.model.BasePageDto;
import org.forbes.comm.vo.Result;
import org.smartwork.model.vo.SettApplVo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@ConditionalOnProperty(name = "spring.application.membercenter")
@FeignClient(name = "${spring.application.membercenter}" ,path = "/api/v1.0/sett-appl")
public interface ISettApplService {


    /***查询待转账列表
     * @param pageDto
     * @return
     */
    @RequestMapping(value = "/select-sett-list", method = RequestMethod.GET)
    public Result<IPage<SettApplVo>> selectSettList(BasePageDto pageDto);


    /***
     * 更新为转账中
     * @param ids
     * @return
     */
    @RequestMapping(value = "/up-in-settl", method = RequestMethod.PUT)
    Result<Boolean> upInSettl(@RequestParam(value = "ids",required = true) List<Long>  ids);


    /***
     * 转账失败更新
     * @param id
     * @param failure
     * @return
     */
    @RequestMapping(value = "/up-failure-settl", method = RequestMethod.PUT)
    Result<Boolean> upfailureSettl(@RequestParam(value = "id",required = true)Long  id,
                                   @RequestParam(value = "failure",required = true)String failure);
}
