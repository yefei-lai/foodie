package com.imooc.service;

import org.springframework.web.multipart.MultipartFile;

public interface FdfsService {

    public String upload(MultipartFile file, String fileExtName) throws Exception;

    /**
     * 阿里云oss上传
     * @param file
     * @param userId
     * @return
     * @throws Exception
     */
    public String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception;

}
