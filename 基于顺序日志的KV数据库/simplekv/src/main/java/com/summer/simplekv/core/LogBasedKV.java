package com.summer.simplekv.core;

import com.alibaba.fastjson.JSON;
import com.summer.simplekv.api.SimpleKvClient;
import com.summer.simplekv.enums.CommandTypeEnum;
import com.summer.simplekv.model.CommandRequestModel;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * 基于日志顺序写入的KV数据库实现
 *
 * @author summer
 * @version : LogBasedKV.java, v 0.1 2022年05月05日 4:06 PM summer Exp $
 */
@Log
public class LogBasedKV implements SimpleKvClient {

    /**
     * 日志文件
     */
    private File logFile;

    /**
     * 构造函数
     *
     * @param fileName 文件名
     */
    public LogBasedKV(String fileName) {
        logFile = new File(fileName);
    }

    @Override
    public void put(String key, String value) {
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            bufferedWriter = new BufferedWriter(fileWriter);

            //日志写入内容构造
            CommandRequestModel commandRequestModel = new CommandRequestModel();
            commandRequestModel.setCommandTypeEnum(CommandTypeEnum.SET);
            commandRequestModel.setKey(key);
            commandRequestModel.setValue(value);

            //往日志文件中写入内容
            bufferedWriter.write(JSON.toJSONString(commandRequestModel));
            bufferedWriter.newLine();
        } catch (Exception e) {
            log.warning("put exception,[" + key + "," + value + "]");
        } finally {
            try {
                bufferedWriter.close();
            } catch (Exception e) {
                log.warning("bufferedWriter close exception");
            }
        }
    }

    @Override
    public String get(String key) {
        try {
            FileReader fileReader = new FileReader(logFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //按行读取日志文件的内容，查找到最后一条修改类操作命令
            CommandRequestModel lastUpdateCommand = null;
            String line = bufferedReader.readLine();
            while (line != null) {
                CommandRequestModel commandRequestModel = JSON.parseObject(line, CommandRequestModel.class);
                if ((CommandTypeEnum.SET == commandRequestModel.getCommandTypeEnum()
                        || CommandTypeEnum.DEL == commandRequestModel.getCommandTypeEnum())
                    && StringUtils.equals(key, commandRequestModel.getKey())) {
                    lastUpdateCommand = commandRequestModel;
                }
                line = bufferedReader.readLine();
            }

            if (lastUpdateCommand == null || CommandTypeEnum.DEL == lastUpdateCommand.getCommandTypeEnum()) {
                return null;
            }

            return lastUpdateCommand.getValue();
        } catch (Exception e) {
            log.warning("get exception,[" + key + "]");
        }
        return null;
    }

    @Override
    public void del(String key) {
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            bufferedWriter = new BufferedWriter(fileWriter);

            //日志写入内容构造
            CommandRequestModel commandRequestModel = new CommandRequestModel();
            commandRequestModel.setCommandTypeEnum(CommandTypeEnum.DEL);
            commandRequestModel.setKey(key);

            //往日志文件中写入内容
            bufferedWriter.write(JSON.toJSONString(commandRequestModel));
            bufferedWriter.newLine();
        } catch (Exception e) {
            log.warning("del exception,[" + key + "]");
        } finally {
            try {
                bufferedWriter.close();
            } catch (Exception e) {
                log.warning("bufferedWriter close exception");
            }
        }
    }
}