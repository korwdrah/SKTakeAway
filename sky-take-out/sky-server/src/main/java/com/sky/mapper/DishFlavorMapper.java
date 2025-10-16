package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.DishFlavor;

import java.util.List;

public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
    List<DishFlavor> selectFlavorsByDishId(Long id);
}
