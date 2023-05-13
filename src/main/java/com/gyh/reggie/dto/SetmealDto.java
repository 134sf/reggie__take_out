package com.gyh.reggie.dto;

import com.gyh.reggie.entity.Setmeal;
import com.gyh.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
