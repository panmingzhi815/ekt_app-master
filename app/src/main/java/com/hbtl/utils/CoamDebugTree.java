package com.hbtl.utils;

import timber.log.Timber;

/**
 * Created by yzhang on 2017/5/2.
 * [Log method name and line number in Timber](http://stackoverflow.com/questions/38689399/log-method-name-and-line-number-in-timber)
 * [Android / Log / Timber - print line number on timber logs. source](https://gist.github.com/caipivara/a5c8197252b6db17cdd6)
 */

public class CoamDebugTree extends Timber.DebugTree {
    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return String.format("[L:%s] [M:%s] [C:%s]",
                element.getLineNumber(),
                element.getMethodName(),
                super.createStackElementTag(element));
    }
}