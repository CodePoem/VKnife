package com.vdreamers.vknife.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.vdreamers.vknife.annotations.BindAttr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Attribute注解处理器
 * <p>
 * date 2019/02/13 16:18:04
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@AutoService(Processor.class)
public class VKnifeProcessor extends AbstractProcessor {

    /**
     * 待生成java文件的的集合，key为被注解的类的类名，value为GenerateJavaFile对象
     */
    private HashMap<String, GenerateJavaFile> mGenerateJavaFiles = new HashMap<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();

        annotations.add(BindAttr.class);

        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        // 解析被BindAttr注解的元素
        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindAttr.class)) {
            try {
                parseBindAttr(element);
            } catch (Exception e) {
                logParsingError(element, BindAttr.class, e);
            }
        }

        // 生成Java文件
        createJavaFile();
        return false;
    }

    private void parseBindAttr(Element element) {
        // 获取到注解BindAttr所在类的TypeElement对象
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        Name qualifiedName = enclosingElement.getQualifiedName();
        String[] splitQualifiedName = qualifiedName.toString().split("\\.");
        String className = splitQualifiedName[splitQualifiedName.length - 1];

        // 获取被注解类对应的GenerateJavaFile对象，如果没有，则创建
        GenerateJavaFile generateJavaFile = mGenerateJavaFiles.get(className);
        if (generateJavaFile == null) {
            GenerateJavaFile file = new GenerateJavaFile();
            // 设置待生成java文件的包名
            file.setPackageName(processingEnv.getElementUtils().getPackageOf(element).toString());
            // 设置待生成java文件的类名
            file.setClassName(className + "Attr");
            // 初始化元素集合
            file.setElements(new ArrayList<>());
            file.getElements().add(element);
            // 保存被注解类所对应要生成java类的GenerateJavaFile对象
            mGenerateJavaFiles.put(className, file);
        } else {
            // 将注解元素添加到有的generateJavaFile对象中
            generateJavaFile.getElements().add(element);
        }
    }

    private void createJavaFile() {

        for (String className : mGenerateJavaFiles.keySet()) {
            // 获取一个GenerateJavaFile对象
            GenerateJavaFile file = mGenerateJavaFiles.get(className);

            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(file.getClassName())
                    .superclass(ClassName.bestGuess(file.getPackageName() + "." + className))
                    .addModifiers(Modifier.PUBLIC);

            // 构造setAttribute方法
            MethodSpec.Builder setAttributeBuilder = MethodSpec.methodBuilder("setAttribute")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "attributeName")
                    .addParameter(Object.class, "value");

            String simpleName;
            int size = file.getElements().size();
            // 遍历该类中需要处理的注解元素
            for (int i = 0; i < size; i++) {
                Element element = file.getElements().get(i);
                simpleName = element.getSimpleName().toString();
                String typeString = element.asType().toString();
                if (i == 0) {
                    setAttributeBuilder.addCode("if (\"" + simpleName + "\".equals(attributeName)" +
                            ") {\n  this.set" +
                            captureName(simpleName) + "" + "((" + typeString + ") value);\n}");
                } else {
                    setAttributeBuilder.addCode(" else if (\"" + simpleName + "\".equals" +
                            "(attributeName)) {\n  this.set" +
                            captureName(simpleName) + "(" + "(" + typeString + ") value);\n}");
                }

                // 构造set方法
                MethodSpec.Builder setBuilder =
                        MethodSpec.methodBuilder("set" + captureName(simpleName))
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(TypeVariableName.get(typeString), simpleName)
                                .addCode("super." + simpleName + " = " + simpleName + ";\n");
                typeSpecBuilder.addMethod(setBuilder.build());
            }

            setAttributeBuilder.addCode("\n");

            typeSpecBuilder.addMethod(setAttributeBuilder.build());

            TypeSpec typeSpec = typeSpecBuilder.build();

            JavaFile javaFile = JavaFile.builder(file.getPackageName(), typeSpec)
                    .build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印解析错误
     *
     * @param element    解析元素
     * @param annotation 注解
     * @param e          错误信息
     */
    private void logParsingError(Element element, Class<? extends Annotation> annotation,
                                 Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        error(element, "Unable to parse @%s.\n\n%s", annotation.getSimpleName(), stackTrace);
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[]
            args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }

    /**
     * 首字母大写
     *
     * @param name 变换前字符串
     * @return 变换后字符串
     */
    private static String captureName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
