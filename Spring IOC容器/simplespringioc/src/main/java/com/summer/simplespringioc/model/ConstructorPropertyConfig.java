package com.summer.simplespringioc.model;

import lombok.Data;

/**
 * constructor arg配置
 *
 * @author summer
 * @version $Id: ConstructorPropertyConfig.java, v 0.1 2022年02月14日 9:27 AM summer Exp $
 */
@Data
public class ConstructorPropertyConfig {
    //属性值
    private String value;
    //属性的值是否引用其他bean
    private Boolean isValueRef;
}