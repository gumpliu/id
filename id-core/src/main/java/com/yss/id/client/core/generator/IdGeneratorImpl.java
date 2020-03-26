package com.yss.id.client.core.generator;

import com.yss.id.client.core.factory.IdGeneratorFactory;
import com.yss.id.client.core.service.IdService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: id生成器
 * @Author gumpLiu
 * @Date 2020-03-25
 * @Version V1.0
 **/
public class IdGeneratorImpl extends AbstractIdGenerator {

    public IdGeneratorImpl(IdService idService){
        super(idService);
    }

    @Override
    public String getSegmentNextId(String bizTag) {
        return nextId(bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String bizTag) {
        return fixedLengthNextId(bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String prefix, String bizTag) {
        return prefix + fixedLengthNextId(bizTag);
    }

    @Override
    public String getSegmentFixedLengthNextId(String bizTag, int length) {
        return fixedLengthNextId(bizTag, length);
    }

    @Override
    public String getSegmentFixedLengthNextId(String prefix, String bizTag, int length) {
        return prefix + fixedLengthNextId(bizTag, length);
    }

    @Override
    public String getSnowflakeNextId() {
        return null;
    }

    @Override
    public String getSnowflakeNextId(Enum format) {
        return null;
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
