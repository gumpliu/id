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
    private Long id;

    @Basic
    @Column(name = "test_id")
    private String testId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }
}
