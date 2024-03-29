package com.hlh.upload.controller;


import com.hlh.upload.common.ResponseResult;
import com.hlh.upload.common.ResultCode;
import com.hlh.upload.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Date 2022-09-27-21-48
 * @Author qianzhikang
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    @Resource
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseResult uploadFile(MultipartFile file) throws Exception {
        String path = null;
        if (file != null) {
            log.info(file.toString());
            String fileName = file.getOriginalFilename();
            log.info(fileName);
            path = uploadService.uploadFile(file);
            return ResponseResult.success(path);
        }else{
            return ResponseResult.failure(ResultCode.UPLOAD_ERROR,"文件上传失败");
        }

    }
}
