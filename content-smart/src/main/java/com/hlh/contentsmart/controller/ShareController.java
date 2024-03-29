package com.hlh.contentsmart.controller;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSONObject;
import com.hlh.contentsmart.auth.CheckLogin;
import com.hlh.contentsmart.common.ResponseResult;
import com.hlh.contentsmart.common.ResultCode;
import com.hlh.contentsmart.domain.dto.AddShareDto;
import com.hlh.contentsmart.domain.dto.ShareDto;
import com.hlh.contentsmart.domain.dto.ShareQueryDto;
import com.hlh.contentsmart.domain.entity.Share;
import com.hlh.contentsmart.domain.entity.User;
import com.hlh.contentsmart.openfeign.UserService;
import com.hlh.contentsmart.service.ShareService;
import com.hlh.contentsmart.utils.JwtOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

/**
 * @author w2gd
 */
@RestController
@Slf4j
@RequestMapping("/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {
    private final ShareService shareService;
    private final UserService userService;

    private final JwtOperator jwtOperator;
    final Integer MAX_SIZE = 50;

    /**
     * 获取所有资源
     * @param pageNum 当前页
     * @param pageSize 每页数量
     * @param shareQueryDto shareQueryDto
     * @param token token
     * @return 符合条件的数据
     */
    @PostMapping("/all")
    @SentinelResource(value = "getAllShares")
    public ResponseResult getAllShares(
            @RequestParam(required = false,defaultValue = "0") int pageNum,
            @RequestParam(required = false,defaultValue = "7") int pageSize,
            @RequestBody(required = false) ShareQueryDto shareQueryDto,
            @RequestHeader(required = false,value = "X-Token") String token
    ){
        if (pageSize > MAX_SIZE) {
            pageSize = MAX_SIZE;
        }
        Integer userId = null;
        if (token != null) {
            userId = getUserIdFromToken(token);
        }
        // String result = shareService.getNumber(2022);
        // log.info(result);
        // if ("BLOCKED".equals(result)) {
        //     return ResponseResult.failure(ResultCode.INTERFACE_EXCEED_LOAD);
        // }
        return ResponseResult.success(shareService.getAll(pageNum,pageSize,shareQueryDto,userId));
    }

    @GetMapping("{id}")
    @SentinelResource(value = "getSharesById")
    public ResponseResult getShareById(@PathVariable Integer id,@RequestHeader(value = "X-Token") String token) {
        String result = shareService.getNumber(2025);
        log.info(result);
        if ("BLOCKED".equals(result)) {
            return ResponseResult.failure(ResultCode.INTERFACE_EXCEED_LOAD);
        }
        Share share = shareService.findById(id);
        Integer userId = share.getUserId();
        ResponseResult res = userService.getUser(userId,token);
        String jsonString = JSONObject.toJSONString(res.getData());
        JSONObject obj = JSONObject.parseObject(jsonString);
        User user = JSONObject.toJavaObject(obj,User.class);

        System.out.println(user);

        ShareDto shareDto = ShareDto.builder().share(share).nickName(user.getNickname()).avatar(user.getAvatar()).build();

        return ResponseResult.success(shareDto);
    }

    public ResponseResult getAllSharesFallback(){
        log.info("接口被熔断降级");
        return ResponseResult.failure(ResultCode.INTERFACE_FALLBACK);
    }

    /**
     * 返回 findByShowFlag = 1 的分享，分页
     * @param pageNum 页数
     * @param pageSize 每页数量
     * @return sharesList
     */
    @GetMapping("/page-shares")
    public ResponseResult getShares(@RequestParam int pageNum, @RequestParam int pageSize) {
        return ResponseResult.success(shareService.getPageShare(pageNum, pageSize));
    }

    /**
     * 根据审核状态返回shares列表
     * @param pageNum 页
     * @param pageSize 数
     * @param status 状态
     * @return sharesList
     */
    @GetMapping("/audit-status")
    public ResponseResult getSharesByAuditStatus(@RequestParam int pageNum,@RequestParam int pageSize, @RequestParam String status) {
        return ResponseResult.success(shareService.getPageShareByAudit(pageNum,pageSize,status));
    }

    /**
     * 根据用户id 返回shares
     * @param userId yhu id
     * @return .
     */
    @GetMapping("/my-shares")
    public ResponseResult getSharesByUserId(@RequestParam int userId) {
        return ResponseResult.success(shareService.getSharesByUserId(userId));
    }
    @PostMapping("/add")
    @CheckLogin
    public ResponseResult addShares(@RequestBody AddShareDto asd){
        Date date = new Date();
        Share share = Share.builder()
                .userId(asd.getUserId())
                .title(asd.getTitle())
                .createTime(date)
                .updateTime(date)
                .isOriginal(asd.getIsOriginal())
                .author(asd.getAuthor())
                .cover(asd.getCover())
                .summary(asd.getSummary())
                .price(asd.getPrice())
                .downloadUrl(asd.getDownloadUrl())
                .buyCount(0)
                .showFlag(0)
                .auditStatus("NOT_YET")
                .reason("")
                .build();
        return ResponseResult.success(shareService.addShare(share));
    }

    // 根据条件获取数据
    // @GetMapping("/list")
    // public ResponseResult shareList(
    //         @RequestParam(defaultValue = "0",required = false,value = "pageIndex") Integer pageIndex,
    //         @RequestParam(defaultValue = "10",required = false,value = "pageSize") Integer pageSize,
    //         @RequestParam(defaultValue = "",required = false,value = "title") String title,
    //         @RequestParam(defaultValue = "",required = false,value = "name") String name,
    //         @RequestParam(defaultValue = "",required = false,value = "summary") String summary,
    //         @RequestHeader(value = "X-Token",required = false) String token
    // ) {
    //     Integer userId =0;
    //     if (token != null) {
    //         userId = getUserIdFromToken(token);
    //     }
    //     if (pageSize > 100) {
    //         pageSize = 10;
    //     }
    //     ShareQueryDto shareQueryDto = ShareQueryDto.builder().title(title).summary(summary).name(name).build();
    //     return shareService.shareList(pageIndex,pageSize,shareQueryDto, userId);
    // }

    /**
     * 解析 token 返回 id
     * @param token token
     * @return id
     */
    private Integer getUserIdFromToken(String token) {
        return Integer.parseInt(jwtOperator.getClaimsFromToken(token).get("id").toString());
    }

    /**
     * 兑换接口
     * @param shareId 内容id
     * @param token token
     * @return 兑换内容详情
     * @throws Exception
     */
    @PostMapping("/exchange")
    public ResponseResult exchange(@RequestParam int shareId,@RequestHeader(name = "X-Token") String token) throws Exception {
        System.out.println(token);
        Integer userId = getUserIdFromToken(token);
        Share exchange = shareService.exchange(shareId, userId, token);
        return ResponseResult.success(exchange);
    }

    /**
     * 我的兑换接口
     * @param token token
     * @param pageNum  分页
     * @param pageSize  分页
     * @return 返回
     */
    @GetMapping("/exchange-record")
    public ResponseResult getExchangeRecord(@RequestHeader(name = "X-Token") String token,
                                            @RequestParam(required = false,defaultValue = "0") Integer pageNum,
                                            @RequestParam(required = false,defaultValue = "5") Integer pageSize) {
        Integer userId = getUserIdFromToken(token);
        return ResponseResult.success(shareService.getExchangeRecord(pageNum,pageSize,userId));
    }

}
