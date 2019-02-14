package com.vdreamers.vknife.sample;


import com.vdreamers.vknife.annotations.BindAttr;

/**
 * 测试实体类
 * <p>
 * date 2019/02/14 14:35:04
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class TestModel {

    @BindAttr
    protected String title;
    @BindAttr
    protected Integer titleNum;
    @BindAttr
    protected boolean changed;
    protected int useless;
    @BindAttr
    protected String content;
    @BindAttr
    protected int contentNum;

}
