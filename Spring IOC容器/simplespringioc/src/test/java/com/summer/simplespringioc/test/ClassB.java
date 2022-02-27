package com.summer.simplespringioc.test;

import lombok.Data;

/**
 * 测试类
 *
 * @author summer
 * @version $Id: ClassA.java, v 0.1 2022年02月13日 7:47 PM summer Exp $
 */
@Data
public class ClassB {
    private ClassA fieldA1;

    public ClassB(){}

    public ClassB(ClassA fieldA1) {
        this.fieldA1 = fieldA1;
    }
}