package com.summer.simplekv.model;

import com.summer.simplekv.enums.CommandTypeEnum;
import lombok.Data;

/**
 * 命令请求模型
 *
 * @author summer
 * @version : CommandRequestModel.java, v 0.1 2022年05月05日 4:02 PM summer Exp $
 */
@Data
public class CommandRequestModel {
    /**
     * 命令类型
     */
    private CommandTypeEnum commandTypeEnum;
    /**
     * key
     */
    private String key;
    /**
     * 值
     */
    private String value;
}