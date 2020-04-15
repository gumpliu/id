package com.yss.id.server.domain.segment.entity;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "t_alloc")
public class AllocEntity {

    @Id
    @Column(name = "id",unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "custom-id")
    @GenericGenerator(name="custom-id", strategy = "com.yss.id.server.util.CustomIDGenerator")
    private String id;

    /** 业务类型 */
    @Column(name = "biz_tag",unique = true, nullable = false)
    private String bizTag;

    /** 最大值 */
    @Column(name = "max_id", nullable = false)
    private BigInteger maxId;

    /** 号段步长 */
    @Column(name = "step")
    private Integer step;

    /** 号段步长 */
    @Version
    @Column(name = "version")
    private Long version;

    /** 创建时间 */
    @CreatedDate
    @Column(name = "create_time")
    private Date createTime;

    /** 修改时间 */
    @LastModifiedDate
    @Column(name = "update_time")
    private Date updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBizTag() {
        return bizTag;
    }

    public void setBizTag(String bizTag) {
        this.bizTag = bizTag;
    }

    public BigInteger getMaxId() {
        return maxId;
    }

    public void setMaxId(BigInteger maxId) {
        this.maxId = maxId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
