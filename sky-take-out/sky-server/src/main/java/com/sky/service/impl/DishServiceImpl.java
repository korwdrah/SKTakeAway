package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //分页查询
        String dishName = dishPageQueryDTO.getName();
        Integer categoryId = dishPageQueryDTO.getCategoryId();
        Page<Dish> dishPage = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<Dish> dishPageInfo = dishMapper.selectPage(dishPage,new LambdaQueryWrapper<Dish>().like(StringUtils.isNotBlank(dishName),Dish::getName, dishName).eq(categoryId!=null,Dish::getCategoryId, categoryId));
        return new PageResult(dishPageInfo.getTotal(), dishPageInfo.getRecords());
    }

    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        log.info("dish:{}",dish);
        dishMapper.insert(dish);
        //获取主键值
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //保存菜品口味数据到菜品口味表dish_flavor
            dishFlavorMapper.insert(flavors);
        }
    }
}

