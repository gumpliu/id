package com.yss.acs.iddemo.domain;

import javax.persistence.*;

/**
 * @Author: xpr
 * @Date: 2019-12-19
 */
@Entity
@Table(name = "t_id_test")
public class TIdTest{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id",unique = true, nullable = false)
    private Long id;

    @Basic
    @Column(name = "uid")
    private String uid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
