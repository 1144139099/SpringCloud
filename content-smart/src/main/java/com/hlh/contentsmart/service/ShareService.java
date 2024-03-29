package com.hlh.contentsmart.service;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.hlh.contentsmart.domain.dto.AuditShareDto;
import com.hlh.contentsmart.domain.dto.ExchangeRecordDto;
import com.hlh.contentsmart.domain.dto.ShareQueryDto;
import com.hlh.contentsmart.domain.entity.Share;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

/**
 * @author w2gd
 */
public interface ShareService {
    /**
     * 111
     * @param id 1
     * @return 1
     */
    Share findById(Integer id);

    /**
     * 分页资源
     * @param pageNum  当前页
     * @param pageSize 每页数量
     * @param shareQueryDto 模糊查询参数
     * @param userId 用户id
     * @return Page<Share>
     */
    Page<Share> getAll(int pageNum, int pageSize, ShareQueryDto shareQueryDto, Integer userId);



    /**
     * getNumber
     * @param number 。
     * @return 。
     */
    String getNumber(int number);

    /**
     * 审核分享内容
     * @param auditShareDto 分享内容dto
     * @return 分享内容详情
     */
    Share auditShare(AuditShareDto auditShareDto);

    /**
     * a
     * @param number .
     * @param e .
     * @return .
     */
    String blockHandlerGetNumber(int number, BlockException e);

    /**
     * 获取分页资源
     * @param pageNum 当前页
     * @param pageSize 每页数量
     * @return 分页数据
     */
    Page<Share> getPageShare(int pageNum, int pageSize);

    /**
     * 根据审核状态显示查询
     * @param pageNum 当前页
     * @param pageSize 每页数量
     * @param status 审核状态
     * @return 分页数据
     */
    Page<Share> getPageShareByAudit(int pageNum, int pageSize,String status);

    /**
     * 根据用户ID返回shares
     * @param userId 用户ID
     * @return sharesList
     */
    List<Share> getSharesByUserId(Integer userId);

    /**
     * 新增
     * @param share share
     * @return share
     */
    Share addShare(Share share);


    // ResponseResult shareList(Integer pageIndex, Integer pageSize, ShareQueryDto shareQueryDto, Integer userId);

    /**
     * 兑换资源
     * @param shareId 资源id
     * @param userId 用户id
     * @return 兑换的资源
     */
    Share exchange(Integer shareId,Integer userId,String token) throws Exception;

    /**
     * 查询兑换记录
     * @param pageNum 分页
     * @param pageSize 分页
     * @param userId 用户id
     * @return 分页兑换记录
     */
    Page<ExchangeRecordDto> getExchangeRecord(Integer pageNum, Integer pageSize, Integer userId);
}
