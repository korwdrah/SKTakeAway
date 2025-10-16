package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SetmealDishMapper extends BaseMapper<Setmeal> {
    List<Long> getSetmealIdsByDishIds(@Param("ids")List<Long> ids);
}
