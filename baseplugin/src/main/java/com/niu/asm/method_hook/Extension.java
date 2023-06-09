package com.niu.asm.method_hook;

public abstract class Extension {
   public boolean enable = true;
   public boolean justDebug = false;
   //是否关注jar包进行字节码处理
   public boolean injectJar = true;
   //是否输出log日志
   public boolean buildLog = false;

   public abstract String getExtensionName();
}
