package org.smartwork.job;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.forbes.comm.constant.CommonConstant;
import org.forbes.comm.model.BasePageDto;
import org.forbes.comm.utils.ConvertUtils;
import org.forbes.comm.vo.Result;
import org.forbes.pay.comm.enums.BizSceneEnum;
import org.forbes.pay.comm.model.PayeeInfo;
import org.smartwork.model.dto.TransferDto;
import org.smartwork.model.vo.SettApplVo;
import org.smartwork.service.ISettApplService;
import org.smartwork.service.ITransferApiOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
@Component
@Lazy(value = false)
@Slf4j
@ConditionalOnProperty(value = "smartwork.settl.transfer.cron")
public class SettlTransferJob {


    private static final ReentrantReadWriteLock REENTT_WRITE_LOCK = new ReentrantReadWriteLock();
    @Autowired
    ISettApplService settApplService;
    @Autowired
    ITransferApiOrderService transferApiOrderService;

    /***转账定时任务
     */
    @Scheduled(cron = "${smartwork.settl.transfer.cron}")
    public void settlTransfer(){
            boolean isLock = false;
            String prefix = "定时转账";
            try {
                if(REENTT_WRITE_LOCK.writeLock().tryLock()){
                    isLock = true;
                    BasePageDto pageDto = new BasePageDto();
                    Result<IPage<SettApplVo>>  resultPageAppl = settApplService.selectSettList(pageDto);
                    if(CommonConstant.SC_OK_200.equals(resultPageAppl.getCode())
                            && "0000".equalsIgnoreCase(resultPageAppl.getBizCode())){
                        IPage<SettApplVo> page =  resultPageAppl.getResult();
                        List<SettApplVo> settApplVos = page.getRecords();
                        if(ConvertUtils.isNotEmpty(settApplVos)){
                            List<Long> ids =  settApplVos.stream().map(SettApplVo::getId).collect(Collectors.toList());
                            Result<Boolean> booleanResult = settApplService.upInSettl(ids);
                            if(CommonConstant.SC_OK_200.equals(booleanResult.getCode())
                                    && "0000".equalsIgnoreCase(booleanResult.getBizCode())){
                                settApplVos.forEach(settApplVo -> {
                                    PayeeInfo payeeInfo = new PayeeInfo();
                                    payeeInfo.setIdentity(settApplVo.getSettlAccount());
                                    payeeInfo.setName(settApplVo.getAccountName());
                                    Result<Map<String,Object>> result = transferApiOrderService.transferOrder(TransferDto.TransferDtoBuild
                                            .build()
                                            .setMchId(settApplVo.getMchId())
                                            .setCurrency("cny")
                                            .setDevice("")
                                            .setNotifyUrl("topicTransfer")
                                            .setOrderTitle(settApplVo.getTitle())
                                            .setOutBizNo(settApplVo.getApplOrderNo())
                                            .setPayeeInfo(payeeInfo)
                                            .setTransAmount(settApplVo.getActualAmount().toString())
                                            .setProductCode(settApplVo.getSettlType())
                                            .setBizScene(BizSceneEnum.receCode(settApplVo.getSettlType())));
                                    if(CommonConstant.SC_OK_200.equals(result.getCode())
                                            && !"0000".equalsIgnoreCase(result.getBizCode())){
                                        Result<Boolean> resultFailure = settApplService.upfailureSettl(settApplVo.getId(),result.getMessage());
                                        if(CommonConstant.SC_OK_200.equals(resultFailure.getCode())
                                                && !"0000".equalsIgnoreCase(resultFailure.getBizCode())){
                                            log.error("====={}======{}",prefix,resultFailure.toString());
                                        }
                                        log.error("====={}======{}",prefix,result.toString());
                                    }
                                });
                            } else {
                                log.error("====={}==={}====={}===",prefix,booleanResult.getBizCode(),booleanResult.getMessage());
                            }
                        }
                    } else {
                        log.error("====={}==={}====={}===",prefix,resultPageAppl.getBizCode(),resultPageAppl.getMessage());
                    }
                }
            }catch (Exception e){
                    log.error("===={}=====",prefix,e);
            } finally {
                if(isLock){
                    REENTT_WRITE_LOCK.writeLock().unlock();
                }
            }
    }
}
