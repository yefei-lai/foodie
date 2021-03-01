package com.imooc.controller;


import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import com.imooc.service.center.CenterUserService;
import common.imooc.utils.CookieUtils;
import common.imooc.utils.DateUtil;
import common.imooc.utils.IMOOCJSONResult;
import common.imooc.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("fdfs")
public class CenterUserController extends BaseController{

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private FdfsService fdfsService;

    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(
            String userId,
            MultipartFile file,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws Exception{

        String path = null;
        // 开始文件上传
        if (null != file){
            // 获取文件上传的文件名称
            String fileName = file.getOriginalFilename();

            if (StringUtils.isNoneBlank(fileName)){

                // 文件重命名 imooc-face.png -> ["imooc-face", "png"]
                String fileNameArr[] = fileName.split("\\.");

                // 获取文件后缀名
                String suffix = fileNameArr[fileNameArr.length - 1];

                if(!suffix.equalsIgnoreCase("png") &&
                    !suffix.equalsIgnoreCase("jpg") &&
                    !suffix.equalsIgnoreCase("jpeg")){
                    return IMOOCJSONResult.errorMsg("图片格式不正确");
                }

//                path = fdfsService.upload(file, suffix);
                path = fdfsService.uploadOSS(file, userId, suffix);
            }

        }else {
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }

        if (StringUtils.isNotBlank(path)){

//            String finalUserFaceUrl = fileResource.getHost() + path;
            String finalUserFaceUrl = fileResource.getOssHost() + path;

            // 更新用户头像到数据库
            Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);

            // 增加令牌token, 会整合redis, 分布式会话
            UsersVO usersVO = conventUsersVo(userResult);

            CookieUtils.setCookie(httpServletRequest, httpServletResponse, "user",
                    JsonUtils.objectToJson(usersVO), true);

        }else {
            return IMOOCJSONResult.errorMsg("上传图片失败");
        }

        return IMOOCJSONResult.ok();
    }

}
