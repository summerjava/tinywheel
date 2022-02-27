package com.summer.simplespringioc.model;

import lombok.Data;

/**
 * bean property配置
 *
 * @author summer
 * @version $Id: BeanPropertyConfig.java, v 0.1 2022年02月14日 9:27 AM summer Exp $
 */
@Data
public class BeanPropertyConfig {
    //属性名
    private String name;
    //属性值
    private String value;
    //属性的值是否引用其他bean
    private Boolean isValueRef;
}