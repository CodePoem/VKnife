package com.vdreamers.vknife.compiler;

import java.util.List;

import javax.lang.model.element.Element;

/**
 * 待生成的Java文件描述
 * <p>
 * date 2019/02/13 16:18:15
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class GenerateJavaFile {

    /**
     * 待生成的Java文件包名
     */
    private String mPackageName;
    /**
     * 待生成的Java文件类名
     */
    private String mClassName;
    /**
     * 待生成的Java文件元素结合
     */
    private List<Element> mElements;

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public List<Element> getElements() {
        return mElements;
    }

    public void setElements(List<Element> elements) {
        mElements = elements;
    }
}
