package com.yss.id.client.core.factory;

import com.yss.id.client.core.generator.IdGenerator;
import com.yss.id.client.core.generator.IdGeneratorImpl;
import com.yss.id.client.core.service.impl.HttpIdServiceImpl;

/**
 * @Description: IdGenerator 工厂类
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public class IdGeneratorFactory {

    private static IdGenerator idGenerator;

    public static IdGenerator getIdGenerator(){
        if(idGenerator == null){
            synchronized (IdGeneratorFactory.class){
                if(idGenerator == null){
                    //todo 做扩展
                    idGenerator = new IdGeneratorImpl(new HttpIdServiceImpl());
                }
            }
        }
        return  idGenerator;
    }

}
