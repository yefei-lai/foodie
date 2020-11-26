package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.CenterUserService;
import common.imooc.utils.CookieUtils;
import common.imooc.utils.IMOOCJSONResult;
import common.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "用户信息头像修改", notes = "用户信息头像修改", httpMethod = "POST")
    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
            MultipartFile file,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse){

        // 定义头像保存的地址
        String fileSpace = fileUpload.getImageUserFaceLocation();
        // 在路径上为每个用户增加一个userId，用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;

        // 开始文件上传
        if (null != file){
            FileOutputStream fileOutputStream = null;
            try {
                // 获取文件上传的文件名称
                String fileName = file.getOriginalFilename();

                if (StringUtils.isNoneBlank(fileName)){

                    // 文件重命名 imooc-face.png -> ["imooc-face", "png"]
                    String fileNameArr[] = fileName.split("\\.");

                    // 获取文件后缀名
                    String suffix = fileNameArr[fileNameArr.length - 1];

                    // face-{userId}.png
                    // 文件名称重组 覆盖式上传，增量式：额外拼接当前时间
                    String newFileName = "face-" + userId + "." + suffix;

                    // 上传的头像最终保存的位置
                    String finalFacePath = fileSpace + uploadPathPrefix +  File.separator + newFileName;

                    File outFile = new File(finalFacePath);
                    if (null != outFile.getParentFile()){
                        // 创建文件夹
                        outFile.getParentFile().mkdir();
                    }

                    // 文件输出保存到目录
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != fileOutputStream){
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else {
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }

        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    public IMOOCJSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse){

        // 判断字段验证信息
        if (result.hasErrors()){
            Map<String, String> map = getErrors(result);
            return IMOOCJSONResult.errorMap(map);
        }

        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);

        //更新cookie里的用户信息
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(httpServletRequest, httpServletResponse, "user",
                JsonUtils.objectToJson(userResult), true);

        // TODO 后续要改，增加令牌token, 会整合redis, 分布式会话

        return IMOOCJSONResult.ok();
    }

    private Map<String, String> getErrors(BindingResult result){
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList){
            String errorField = error.getField();
            String errorMsg = error.getDefaultMessage();

            map.put(errorField, errorMsg);
        }
        return map;
    }

    private Users setNullProperty(Users users){
        users.setPassword(null);
        users.setMobile(null);
        users.setEmail(null);
        users.setCreatedTime(null);
        users.setUpdatedTime(null);
        users.setBirthday(null);
        return users;
    }
}
