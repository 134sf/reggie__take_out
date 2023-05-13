package com.gyh.reggie.service;

import com.gyh.reggie.dto.DishDto;
import com.gyh.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getWithFlavor(Long id);

    //
    public void updateWithFlavor(DishDto dishDto);
}
