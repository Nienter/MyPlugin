package com.niu.plugin.hookmethod;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;

import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.niu.asm.method_hook.BasePlugin;

//import org.gradle.internal.impldep.org.apache.http.util.TextUtils;
//import org.gradle.internal.impldep.org.apache.http.util.TextUtils;
import org.gradle.api.Action;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;

public class HookMethodPlugin extends BasePlugin<HookMethodExtension> implements Action {
    @Override
    public HookMethodExtension initExtension() {
        return new HookMethodExtension();
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        super.transform(transformInvocation);
    }

    @Override
    public byte[] transform(byte[] classBytes, File classFile) {
        String name = classFile.getName();
        if (!isEmpty(getExtension().impl) &&
                classFile.getAbsolutePath().contains(getExtension().impl.replace(".", File.separator))) {
            return classBytes;
        }
        if (name.endsWith(".class") && !name.startsWith("R$") &&
                !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
            getLogger().log("[class]" + classFile.getName());
            return processClass(classBytes);
        }
        return classBytes;
    }

    public static boolean isEmpty(CharSequence s) {
        if (s == null) {
            return true;
        } else {
            return s.length() == 0;
        }
    }
    @Override
    public byte[] transformJar(byte[] classBytes, File jarFile, JarEntry entry) {
        //如果是impl类，直接跳过
        if (!isEmpty(getExtension().impl) &&
                entry.getName().contains(getExtension().impl.replace(".", "/"))) {
            return classBytes;
        }
        getLogger().log("[jar class]" + jarFile.getName() + ":" + entry.getName());
        //跳过自己的类库
        if(entry.getName().contains("com/niu/pluginlib/")){
            //如果有impl，替换处理实现类
            if (!isEmpty(getExtension().impl)
                    && entry.getName().equals("com/niu/pluginlib/tools/MethodHookHandler.class")) {
                try {
                    getLogger().log("[dump impl]" + jarFile.getName() + ":" + entry.getName());
                    return DumpClazz.dump(getExtension().impl);
                } catch (Exception e) {
                    getLogger().log(e);
                    return classBytes;
                }
            }
            return classBytes;
        }
        //注解+正则判断是否插桩
        return processClass(classBytes);
    }

    private byte[] processClass(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        MethodHookVisitor cv = new MethodHookVisitor(cw, this);
        cr.accept(cv, EXPAND_FRAMES);
        return cw.toByteArray();
    }

    @Override
    public String getName() {
        return "hook-method-plugin";
    }

    @Override
    public void execute(Object o) {
        getLogger().log("start");

    }
}
