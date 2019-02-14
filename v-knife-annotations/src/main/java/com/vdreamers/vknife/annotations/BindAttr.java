package com.vdreamers.vknife.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * 被这个注解标注的字段将生成 setAttribute 和 setAttribute 方法
 * <p>
 * date 2019/02/13 16:16:37
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@Retention(CLASS)
@Target(FIELD)
public @interface BindAttr {

}
