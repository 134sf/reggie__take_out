package com.gyh.reggie.service;

import com.gyh.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 */

public interface CategoryService extends IService<Category> {
        void remove(Long ids);
}
