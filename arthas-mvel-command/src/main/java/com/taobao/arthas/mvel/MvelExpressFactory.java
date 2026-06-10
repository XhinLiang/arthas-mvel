package com.taobao.arthas.mvel;

import java.util.concurrent.ConcurrentHashMap;

import com.taobao.arthas.core.command.express.Express;

/**
 * Caches one {@link MvelExpress} per target ClassLoader.
 *
 * <p>This used to live in {@code arthas-core}'s {@code ExpressFactory.mvelExpress}; it was moved
 * here so the MVEL command can be shipped as an external command without patching arthas-core.
 *
 * @author xhinliang
 */
public final class MvelExpressFactory {

    private static final ConcurrentHashMap<String, MvelExpress> MVEL_EXPRESS = new ConcurrentHashMap<String, MvelExpress>();

    private MvelExpressFactory() {
    }

    public static synchronized Express mvelExpress(ClassLoader classloader) {
        String classLoaderName = classloader.getClass().getName();
        MvelExpress express = MVEL_EXPRESS.get(classLoaderName);
        if (express == null) {
            express = new MvelExpress(classloader);
            MVEL_EXPRESS.put(classLoaderName, express);
        }
        return express;
    }
}
