package com.imooc.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FdfsServiceImpl implements FdfsService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FileResource fileResource;

    @Override
    public String upload(MultipartFile file, String fileExtName) throws Exception {

        // 上传
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
        String path = storePath.getFullPath();

        return path;
    }


    @Override
    public String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception {


        OSS ossClient = new OSSClientBuilder().build(fileResource.getEndpoint(), fileResource.getAccessKeyId(), fileResource.getAccessKeySecret());
        InputStream inputStream = file.getInputStream();
        // 上传路径
        String myObjectName = fileResource.getObjectName() + "/" + userId + "/" + userId + "." + fileExtName;
        ossClient.putObject(fileResource.getBucketName(), myObjectName, inputStream);
        ossClient.shutdown();

        return myObjectName;
    }
}
