package com.common.vaild;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
//校验注解使用的哪个校验器
@Constraint(validatedBy = {ListValueConstraintValidator.class})
//注解可以标注在哪些位置
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
//校验注解获取的时机
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {

    /**
     * jrs303的规范一：当校验出错时的错误信息去哪里取
     */
    String message() default "{com.stonebridge.common.valid.ListValue.message}";

    /**
     * jrs303的规范二：支持分组校验的功能
     */
    Class<?>[] groups() default {};

    /**
     * jrs303的规范三：自定义校验可以支持负载信息
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 添加注解时设置的属性
     */
    int[] values() default {}; //自己添加的属性
}

