package com.gyh.reggie.controller;

import com.gyh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 *文件上传和下载
 */
@RequestMapping("/common")
@Slf4j
@RestController
public class CommonController {
    /**
     * 文件上传
     * @param file
     * @return
     */
    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    //方法参数类型必须为MultipartFile,参数名必须与前端页面传递参数值name=file保持一致
    public R<String> upload(@RequestPart MultipartFile file){
        //file是一个临时文件，需要指定位置存储，否则本次请求完成后临时文件将被删除
        log.info(file.toString());
        //后去源文件名
        String originalFilename = file.getOriginalFilename();
        //获取后缀名.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        // 判断目录是否存在
        if (!dir.exists()){
            //目录不存在，创建目录
            dir.mkdir();
        }

        try {
            //将文件存放到指定位置
            file.transferTo(new File(basePath+ fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    @RequestMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
//            int len = 0;
//            byte[] bytes = new byte[1024];
            byte[] bytes = new byte[fileInputStream.available()];
            //输出流, 通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应格式
            response.setContentType("image/jpeg");

//            while ((len = fileInputStream.read(bytes)) != -1 ){
//                outputStream.write(bytes,0,len);
//            }
            int read = fileInputStream.read(bytes);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
