package com.yss.id.client.server.domain.segment.repository;

import com.yss.id.client.server.domain.segment.entity.AllocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

/**
 * @Author: gumpLiu
 *
 * todo 悲观锁优化
 *
 * @Date: 2020-03-24 10:59
 */
public interface AllocRepository extends JpaRepository<AllocEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a from AllocEntity a where a.bizTag = :bizTag ")
    AllocEntity findByBizTag(@Param("bizTag") String bizTag);

    @Modifying
    @Query("update AllocEntity a set a.maxId = a.maxId + a.step where a.bizTag = :bizTag")
    int updateByBizTag(@Param("bizTag") String bizTag);
}
