package com.gyh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.gyh.reggie.common.R;
import com.gyh.reggie.entity.User;
import com.gyh.reggie.service.UserService;
import com.gyh.reggie.utils.SMSUtils;
import com.gyh.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //先获取到手机号
        String phone = user.getPhone();
        //判断是否为空
        if (StringUtils.isNotBlank(phone)) {
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码 + {}",code);

            //调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, code);

            //需要将生成的验证码保存到session
            session.setAttribute(phone,code);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("手机验证码短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    //返回值为User是因为需要将用户信息返回给浏览器，在浏览器保存用户信息
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取手机号码
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        //从session中获取保存的验证码
        String codeInSeesion = (String) session.getAttribute(phone);

        //进行验证码的对比（页面提交的验证码与Session中保存的验证码对比）
         if (codeInSeesion != null && codeInSeesion.equals(code)){
             //如果对比成功，则说明成功登录
             LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
             queryWrapper.eq(User::getPhone, phone);

             User user = userService.getOne(queryWrapper);

             //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
             if (user == null){
                 user = new User();
                 user.setPhone(phone);
                userService.save(user);
             }
             session.setAttribute("user",user.getId());
             return R.success(user);
        }

        return R.error("登录失败");
    }
}
