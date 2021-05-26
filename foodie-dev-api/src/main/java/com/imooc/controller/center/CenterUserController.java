package com.imooc.controller.center;
import com.imooc.controller.BaseController;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
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
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户信息接口",tags = {"用户信息接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "修改用户头像",notes = "修改用户头像",httpMethod = "POST")
    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(@ApiParam(name = "userId",value ="用户Id", required = true)
                                  @RequestParam String userId,
                                  @ApiParam(name = "file",value ="用户头像", required = true)
                                  MultipartFile file,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
            //定义头像保存地址
            //String fileSpace = IMAGE_USER_FACE_LOCATION;
            String fileSpace = fileUpload.getImageUserFaceLocation();
            //在路径上为每一个用户添加userid，用于区分不同用户上传
            String uploadPathPrefix = File.separator + userId;
            if (file != null){
                //获得文件上传名称
                FileOutputStream fileOutputStream = null;
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)){
                    String fileNameArr[] = fileName.split("\\.");
                    //获取文件名后缀
                    String suffix = fileNameArr[fileNameArr.length - 1];
                    //后端对于图片格式的校验
                    if (!suffix.equalsIgnoreCase("png")&& !suffix.equalsIgnoreCase("jpeg")&&
                            !suffix.equalsIgnoreCase("jpg")){
                        return IMOOCJSONResult.errorMsg("图片格式不正确");
                    }
                    //face-{userid}.png
                    //文件名重组
                    String newFileName = "face-" + userId + "." + suffix;
                    //上传头像最终保存地址
                    String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;
                    //用于提供给web服务的地址
                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null){
                        //创建文件夹
                        outFile.getParentFile().mkdirs();

                    }
                    //文件输出保存到目录

                    try {
                        fileOutputStream = new FileOutputStream(outFile);
                        InputStream inputStream = file.getInputStream();
                        IOUtils.copy(inputStream,fileOutputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if (fileOutputStream != null){
                            try {
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }else {
                return IMOOCJSONResult.errorMsg("头像上传不能为空");
            }
            //更新到用户头像到数据库
            //获取图片服务地址
            String imageServerUrl = fileUpload.getImageServerUrl();
            //加上时间戳，使前端及时刷新,
            String finalUserFaceUrl = imageServerUrl + uploadPathPrefix + "?t" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
            Users userResult = centerUserService.updateUserFace(userId,finalUserFaceUrl);

            userResult = setNullProperty(userResult);
            //更新cookie
            CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);
            // TODO 要改，整合分布式会话
            return IMOOCJSONResult.ok();
        }

    @ApiOperation(value = "修改用户信息",notes = "修改用户信息",httpMethod = "POST")
    @PostMapping("update")
    public IMOOCJSONResult update(@ApiParam(name = "userId",required = true)
                                    @RequestParam String userId,
                                  @RequestBody @Valid CenterUserBO centerUserBO,
                                  BindingResult result,
                                  HttpServletRequest request,
                                  HttpServletResponse response){
        if (result.hasErrors()){
            Map<String,String> errorMap = getErrors(result);

            return IMOOCJSONResult.errorMap(errorMap);
        }


        Users userResult = centerUserService.updateUserInfo(userId,centerUserBO);

        userResult = setNullProperty(userResult);
        //更新cookie
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);
        // TODO 要改，整合分布式会话
        return IMOOCJSONResult.ok();
    }
    private Map<String, String> getErrors(BindingResult result){
        Map<String, String> map = new HashMap<>();
       List<FieldError> errorList = (List<FieldError>) result.getFieldError();
        for (FieldError err:errorList
             ) {
            String errorField =  err.getField();
            String errorMsg =  err.getDefaultMessage();
            map.put(errorField,errorMsg);

        }
        return map;
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
