package com.gyh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.reggie.common.R;
import com.gyh.reggie.entity.Employee;
import com.gyh.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录方法
     *
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.将提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2。根据页面提交的用户名username查询数据库

        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        //只返回一条数据，返回多条就报错
        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        //3.如果过没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登陆失败");
        }

        //4.密码对比，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }

        //5.查看员工状态是否为禁用状态，如果已为禁用，则返回禁用结果
        if (emp.getStatus() == 0) {
            return R.error("该用户已禁用");
        }

        //6.登录成功，并将用户id存入到SESSION，并返回登录结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);


    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工信息{}", employee.toString());
        //因为添加员工时没有密码，所以设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //元对象处理器会帮住我们去设置公告字段
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获取当前登录用户id
        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        //构造调教构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name)
        .orderByDesc(Employee::getUpdateTime);
        //添加排序条件

        //执行查询
        employeeService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 更新员工数据
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        log.info("请求信息{}",employee.toString());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("更新成功");

    }

    /**
     * 通过id查询员工数据并回显
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
