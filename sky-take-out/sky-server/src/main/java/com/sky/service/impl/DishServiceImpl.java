package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //分页查询 这里直接用MP的分页查询的封装对象进行查询，
        Page<DishVO> dishPage = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        IPage<DishVO> page = dishMapper.pageQuery(dishPage, dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getRecords());
    }

    @Override
    public void delete(List<Long> ids) {
//        在dish表中删除菜品基本数据时，同时，也要把关联在dish_flavor表中的数据一块删除。
//        setmeal_dish表为菜品和套餐关联的中间表。
//        若删除的菜品数据关联着某个套餐，此时，删除失败。
//        若要删除套餐关联的菜品数据，先解除两者关联，再对菜品进行删除
        //首先遍历所有删除的菜品 id，如果说套餐有的话直接返回错误信息
        for(Long id : ids){
            //检查菜品是不是在套餐中
            Dish dish = dishMapper.selectById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //在售卖，不允许删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

            List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
            //如果套餐里有任何一个菜品id都不允许删除
            if(setmealIdsByDishIds != null && setmealIdsByDishIds.size() > 0){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }

            //集体删除
            dishMapper.deleteByIds(ids);
        }


    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //进行dish的更新
        //要更新dish_flavor表中的数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateById(dish);
        //删除完所有的再重新插入
        dishFlavorMapper.delete(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,dish.getId()));
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
                dishFlavorMapper.insert(dishFlavor);
            });

        }
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.selectById(id);
        //获取口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectFlavorsByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
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

