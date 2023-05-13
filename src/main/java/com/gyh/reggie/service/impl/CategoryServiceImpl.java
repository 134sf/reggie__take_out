package com.gyh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gyh.reggie.common.CustomException;
import com.gyh.reggie.entity.Category;
import com.gyh.reggie.entity.Dish;
import com.gyh.reggie.entity.Setmeal;
import com.gyh.reggie.service.CategoryService;
import com.gyh.reggie.mapper.CategoryMapper;
import com.gyh.reggie.service.DishService;
import com.gyh.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long ids) {
        //添加条件查询，根据分类id进行查询
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，如已关联，抛出业务异常
        if (count > 0){
            // 已经关联菜品，抛出业务异常
            throw new CustomException("已关联菜品，不能进行删除");
        }
        //添加条件查询，根据分类id进行查询
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        //查询当前分类是否关联了套餐，如已关联，抛出业务异常
        if (count1 > 0){
            // 已经关联套餐，抛出业务异常
            throw new CustomException("已关联套餐，不能进行删除");
        }


        //正常删除分类,super是这个实现类的父类就是service接口
        super.removeById(ids);
    }
}




