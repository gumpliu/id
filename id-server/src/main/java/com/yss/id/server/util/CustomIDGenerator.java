package com.yss.id.server.util;

import com.yss.id.core.constans.IDFormatEnum;
import com.yss.id.server.domain.snowflake.SnowflakeIdWorkerFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

public class CustomIDGenerator extends IdentityGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Object id = SnowflakeIdWorkerFactory.getInstance(IDFormatEnum.ID_FORMAT_SHOT_YEAR_SECOND).nextId();
        return (Serializable) id;
    }

}
