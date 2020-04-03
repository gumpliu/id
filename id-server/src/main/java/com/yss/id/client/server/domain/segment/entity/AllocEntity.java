package com.yss.id.client.server.domain.segment.entity;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "t_alloc")
public class AllocEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id",unique = true, nullable = false)
    private Long id;

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
    @Column(name = "create_time")
    private Date createTime;

    /** 修改时间 */
    @Column(name = "update_time")
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
