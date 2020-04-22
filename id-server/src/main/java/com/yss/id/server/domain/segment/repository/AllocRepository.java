package com.yss.id.server.domain.segment.repository;

import com.yss.id.server.domain.segment.entity.AllocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @Author: gumpLiu
 *
 * @Date: 2020-03-24 10:59
 */
public interface AllocRepository extends JpaRepository<AllocEntity, String> {

    @Query("SELECT a from AllocEntity a where a.bizTag = :bizTag ")
    AllocEntity findByBizTag(@Param("bizTag") String bizTag);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update AllocEntity a set a.maxId = :maxId, a.step = :step, a.version = a.version + 1  " +
            " where a.bizTag = :bizTag and a.version = :version")
    int updateByBizTag(@Param("bizTag") String bizTag, @Param("maxId") BigInteger maxId, @Param("step") Integer step, @Param("version") Long version);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update AllocEntity a set a.maxId = :maxId, a.step = :step, a.version = a.version + 1  " +
            " where a.bizTag = :bizTag and a.version = :version ")
    int initSegmentByBizTag(@Param("bizTag") String bizTag, @Param("maxId") BigInteger maxId, @Param("step") Integer step, @Param("version") Long version);

}
