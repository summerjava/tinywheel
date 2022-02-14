package com.summer.simplerpc.io;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 序列化工具类（Hessian序列化协议）
 *
 * @author summer
 * @version $Id: HessianUtils.java, v 0.1 2022年01月16日 5:00 PM summer Exp $
 */
@Slf4j
public class HessianUtils {

    /**
     * 序列化
     *
     * @param object
     * @param <T>
     * @return
     */
    public final static <T> byte[] serialize(T object) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        HessianOutput hessianOutput = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ioe) {
            log.error("serialize io exception,object=" + object, ioe);
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException ioe) {
                    log.error("serialize byteArrayOutputStream close io exception,object=" + object, ioe);
                }
            }

            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException ioe) {
                    log.error("serialize hessianOutput close io exception,object=" + object, ioe);
                }
            }
        }

        return null;
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @param <T>
     * @return
     */
    public final static <T> T deserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = null;
        HessianInput hessianInput = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            hessianInput = new HessianInput(byteArrayInputStream);
            return (T)hessianInput.readObject();
        } catch (IOException ioe) {
            log.error("deserialize io exception,bytes=" + bytes, ioe);
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException ioe) {
                    log.error("deserialize byteArrayOutputStream close io exception,bytes=" + bytes, ioe);
                }
            }
            if (hessianInput != null) {
                try {
                    hessianInput.close();
                } catch (Exception ioe) {
                    log.error("deserialize hessianInput close io exception,bytes=" + bytes, ioe);
                }
            }
        }
        return null;
    }
}