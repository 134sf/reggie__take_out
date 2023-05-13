package com.gyh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.reggie.common.R;
import com.gyh.reggie.dto.DishDto;
import com.gyh.reggie.entity.Category;
import com.gyh.reggie.entity.Dish;
import com.gyh.reggie.entity.DishFlavor;
import com.gyh.reggie.service.CategoryService;
import com.gyh.reggie.service.DishFlavorService;
import com.gyh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功！");

    }

    @PostMapping("/status/{status}")
    public R<String> update(@PathVariable Integer status, @RequestParam Long ids) {
        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(Dish::getId,ids).set(Dish::getStatus,status);

        dishService.update(queryWrapper);

        return R.success("更新成功");
    }

    /**
     * 菜品分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>();
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(name != null, Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            //创建一个Dishdto对象
            DishDto dishDto = new DishDto();
            //需要拷贝，因为这是新创建的类，属性都没有赋值
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            //根据id查询对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
//            String categoryName = category.getName();
//            dishDto.setCategoryName(categoryName);
            //别忘记了需要返回
            return dishDto;
            //遍历之后，收集dto对象
        }).collect(Collectors.toList());

        //在设置分页里面的records数据
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getWithFlavor(id);

        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        return R.success("修改成功");
    }

//    /**
//     * 根据条件查询对应的菜品信息--添加套餐
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //添加条件,完善一下，只查询状态为1的  即为在售状态
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime).eq(Dish::getStatus,1);
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }
    /**
     * 根据条件查询对应的菜品信息--添加套餐
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件,完善一下，只查询状态为1的  即为在售状态
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime).eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);

            Long categoryId = item.getCategoryId();
            //根据id查询对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaUpdateWrapper<DishFlavor> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(dishId != null,DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaUpdateWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;

        }).collect(Collectors.toList());

        return R.success(dishList);
    }


}
