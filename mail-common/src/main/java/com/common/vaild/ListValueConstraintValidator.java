package com.common.vaild;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * 校验器必须要实现ConstraintValidator接口
 * ConstraintValidator接口有两个泛型
 * 1.第一个泛型指定注解
 * 2.第二个泛型指定要校验什么类型的数据
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    //泛型第一个为注解类型，第二个为注解标注的属性的类型
    private Set<Integer> set = new HashSet<>();

    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        for (int value : values
        ) {
            set.add(value);
        }
    }

    //判断是否校验成功
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (set.contains(integer)) {
            return true;
        } else {
            return false;
        }
    }
}
