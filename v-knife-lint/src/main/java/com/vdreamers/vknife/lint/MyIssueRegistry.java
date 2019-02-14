package com.vdreamers.vknife.lint;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


/**
 * 自定义Issue策略
 * <p>
 * date 2019/02/13 16:51:17
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
public class MyIssueRegistry extends IssueRegistry {
    @NotNull
    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(LogDetector.ISSUE);
    }
}
