package com.yss.id.client.server.domain.segment.repository;

import com.yss.id.client.server.domain.segment.entity.AllocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.math.BigInteger;

/**
 * @Author: gumpLiu
 *
 * @Date: 2020-03-24 10:59
 */
public interface AllocRepository extends JpaRepository<AllocEntity, Long> {

    @Query("SELECT a from AllocEntity a where a.bizTag = :bizTag ")
    AllocEntity findByBizTag(@Param("bizTag") String bizTag);

    @Modifying
    @Query("update AllocEntity a set a.maxId = a.maxId + :step, a.step = :step, a.version = a.version + 1  " +
            " where a.bizTag = :bizTag and a.version = :version")
    int updateByBizTag(@Param("bizTag") String bizTag, @Param("step") BigInteger step, @Param("version") Long version);


    @Modifying
    @Query("update AllocEntity a set a.maxId = a.step, a.step = :step, a.version = a.version + 1  " +
            " where a.bizTag = :bizTag and a.version = :version ")
    int initSegmentByBizTag(@Param("bizTag") String bizTag, @Param("step") BigInteger step, @Param("version") Long version);

}
