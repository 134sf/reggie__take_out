package com.gyh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyh.reggie.entity.Employee;
import com.gyh.reggie.service.EmployeeService;
import com.gyh.reggie.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}




