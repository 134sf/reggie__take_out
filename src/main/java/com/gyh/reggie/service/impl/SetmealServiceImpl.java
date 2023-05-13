package com.gyh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyh.reggie.common.CustomException;
import com.gyh.reggie.dto.SetmealDto;
import com.gyh.reggie.entity.Setmeal;
import com.gyh.reggie.entity.SetmealDish;
import com.gyh.reggie.service.SetmealDishService;
import com.gyh.reggie.service.SetmealService;
import com.gyh.reggie.mapper.SetmealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishService;

    //两张表需要开启事务
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);


        //保存套餐和菜品的关联信息操作setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return  item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和相关菜品信息
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1;
        //查询套餐状态，判断是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            new CustomException("套餐正在出售，删除失败");
        }

        //如果可以删除，先删除套餐表中的数据  setmeal
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in (1,2,3)  并不是id
        //所以需要条件构造器不能通过原有的方法removeByIds删除
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        //删除关系表中的数据 ---setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);

    }


}




