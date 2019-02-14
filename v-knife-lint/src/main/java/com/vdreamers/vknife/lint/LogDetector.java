package com.vdreamers.vknife.lint;

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintFix;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.PsiMethod;

import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UExpression;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;


/**
 * 日志检测器
 * <p>
 * date 2019/02/13 16:51:26
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class LogDetector extends Detector implements Detector.UastScanner {

    /**
     * Issue唯一Id
     */
    private static final String ISSUE_ID = "LogUseError";
    /**
     * Issue描述
     */
    private static final String ISSUE_DESCRIPTION = "真香警告:You should use our {LogUtils}";
    /**
     * Issue详解
     */
    private static final String ISSUE_EXPLANATION = "You should NOT use Log directly. Instead you" +
            " should use LogUtils we offered.";

    private static final Category ISSUE_CATEGORY = Category.CORRECTNESS;
    /**
     * Issue优先级 1-10
     */
    private static final int ISSUE_PRIORITY = 9;
    /**
     * Issue级别 {@link Severity}
     */
    private static final Severity ISSUE_SEVERITY = Severity.WARNING;
    /**
     * 检测器class
     */
    private static final Class<? extends Detector> DETECTOR_CLASS = LogDetector.class;
    /**
     * 扫描范围
     */
    private static final EnumSet<Scope> DETECTOR_SCOPE = Scope.JAVA_FILE_SCOPE;
    /**
     * 检测器与扫描范围之前的联系
     */
    private static final Implementation IMPLEMENTATION = new Implementation(
            DETECTOR_CLASS,
            DETECTOR_SCOPE
    );
    private static final String CHECK_CODE = "System.out.println";
    private static final String CHECK_PACKAGE = "android.util.Log";

    public static final Issue ISSUE = Issue.create(
            ISSUE_ID,
            ISSUE_DESCRIPTION,
            ISSUE_EXPLANATION,
            ISSUE_CATEGORY,
            ISSUE_PRIORITY,
            ISSUE_SEVERITY,
            IMPLEMENTATION
    );

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList("v", "d", "i", "w", "e", "wtf");
    }

    @Override
    public void visitMethodCall(JavaContext context, UCallExpression call, PsiMethod method) {
        JavaEvaluator evaluator = context.getEvaluator();
        if (!evaluator.isMemberInClass(method, CHECK_PACKAGE)) {
            return;
        }
        LintFix fix = quickFixIssueLog(call);
        context.report(ISSUE, call, context.getLocation(call), ISSUE_DESCRIPTION, fix);
    }

    private LintFix quickFixIssueLog(UCallExpression logCall) {
        List<UExpression> arguments = logCall.getValueArguments();
        String methodName = logCall.getMethodName();
        UExpression tag = arguments.get(0);
        String fixSource = "LogUtils." + methodName + "(" + tag.asSourceString();

        int numArguments = arguments.size();
        if (numArguments == 2) {
            UExpression msgOrThrowable = arguments.get(1);
            fixSource += ", " + msgOrThrowable.asSourceString() + ")";
        } else if (numArguments == 3) {
            UExpression msg = arguments.get(1);
            UExpression throwable = arguments.get(2);
            fixSource += ", " + throwable.asSourceString() + ", " + msg.asSourceString() + ")";
        } else {
            throw new IllegalStateException("android.util.Log overloads should have 2 or 3 " +
                    "arguments");
        }
        String logCallSource = logCall.asSourceString();
        LintFix.GroupBuilder fixGrouper = fix().group();
        fixGrouper.add(fix().replace().text(logCallSource).shortenNames().reformat(true).with(fixSource).build());
        return fixGrouper.build();
    }

}
