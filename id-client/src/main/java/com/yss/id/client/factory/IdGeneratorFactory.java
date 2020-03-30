package com.yss.id.client.factory;

import com.yss.id.client.core.generator.IdGenerator;
import com.yss.id.client.core.generator.IdGeneratorImpl;
import com.yss.id.client.core.generator.segment.IdSegmentGenerator;
import com.yss.id.client.core.generator.snowflake.IdSnowflakeGenerator;
import com.yss.id.client.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: IdGenerator 工厂类
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public class IdGeneratorFactory {

    private static IdGenerator idGenerator;

    private IdGeneratorFactory(){}

    public static IdGenerator getIdGenerator(){
        if(idGenerator == null){
            synchronized (IdGeneratorFactory.class){
                if(idGenerator == null){
                    IdSegmentGenerator segmentGenerator = BeanUtil.getBean(IdSegmentGenerator.class);
                    IdSnowflakeGenerator snowflakeGenerator = BeanUtil.getBean(IdSnowflakeGenerator.class);
                    idGenerator = new IdGeneratorImpl(segmentGenerator, snowflakeGenerator);
                }
            }
        }
        return  idGenerator;
    }

    public static void main(String[] args){
        IdGenerator idGenerator =  IdGeneratorFactory.getIdGenerator();
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for(int i = 0 ; i < 5; i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(idGenerator.getSegmentNextId("lsp"));

                }
            });
        }

    }

}
