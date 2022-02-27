package com.summer.simplespringioc;

import com.summer.simplespringioc.model.BeanPropertyConfig;
import com.summer.simplespringioc.model.ConstructorPropertyConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * bean定义领域模型
 *
 * @author summer
 * @version $Id: SimpleBeanDefinition.java, v 0.1 2022年02月10日 9:09 AM summer Exp $
 */
@Data
public class SimpleBeanDefinition {
    private String                          id;
    private String                          className;
    private List<ConstructorPropertyConfig> constructArgs = new ArrayList<>();
    private List<BeanPropertyConfig>        properties    = new ArrayList<>();
}