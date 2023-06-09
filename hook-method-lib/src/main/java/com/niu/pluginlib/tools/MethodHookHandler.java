package com.niu.pluginlib.tools;

import com.niu.pluginlib.annotation.IgnoreMethodHook;

/**
 * @author miqt
 */
@IgnoreMethodHook
public class MethodHookHandler {
    /**
     * 如果设置了impl参数，则此对象实现将被编译期替换
     */
    private static final IMethodHookHandler M_PRINT = new MethodHookPrint();

    public static void enter(Object thisObj,
                             String className,
                             String methodName,
                             String argsType,
                             String returnType,
                             Object... args
    ) {
        M_PRINT.onMethodEnter(thisObj, className, methodName, argsType, returnType, args);
    }


    public static void exit(Object returnObj,
                            Object thisObj,
                            String className,
                            String methodName,
                            String argsType,
                            String returnType,
                            Object... args) {
        M_PRINT.onMethodReturn(returnObj, thisObj, className, methodName, argsType, returnType, args);
    }
}