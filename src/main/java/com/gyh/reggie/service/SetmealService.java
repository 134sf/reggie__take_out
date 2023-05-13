package com.gyh.reggie.service;

import com.gyh.reggie.dto.SetmealDto;
import com.gyh.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品得关联信息
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和相关菜品信息
     * @param ids
     */
    public void removeWithDish(List ids);

}
