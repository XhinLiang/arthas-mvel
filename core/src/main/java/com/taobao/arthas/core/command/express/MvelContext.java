package com.taobao.arthas.core.command.express;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xhinliang
 */
public class MvelContext extends HashMap<String, Object> {

    private static final String GET_BEAN_BY_NAME = "getBeanByName";
    private static final String GET_BEAN_BY_CLASS = "getBeanByClass";
    private static final String GET_CLASS_BY_NAME = "getClassByName";

    static final Set<String> AUTO_LOAD_FUNCTIONS = new HashSet<String>();

    static {
        AUTO_LOAD_FUNCTIONS.add(GET_BEAN_BY_NAME);
        AUTO_LOAD_FUNCTIONS.add(GET_BEAN_BY_CLASS);
        AUTO_LOAD_FUNCTIONS.add(GET_CLASS_BY_NAME);
    }

    private final MvelEvalKiller evalKiller;

    private final ClassLoader classLoader;

    public MvelContext(MvelEvalKiller evalKiller, ClassLoader classLoader) {
        this.evalKiller = evalKiller;
        this.classLoader = classLoader;
    }

    @Override
    public boolean containsKey(Object k) {
        if (k == null) {
            return false;
        }
        String key = (String) k;
        return super.containsKey(key) || getBean(key) != null;
    }

    @Override
    public Object get(Object k) {
        String key = (String) k;
        Object bean = super.get(key);
        if (bean == null) {
            bean = getBean(key);
        }
        return bean;
    }

    private Object getBean(String beanName) {
        if (AUTO_LOAD_FUNCTIONS.contains(beanName)) {
            return null;
        }
        Object bean = null;
        Class<?> clazz = null;
        try {
            if (this.containsKey(GET_BEAN_BY_NAME)) {
                bean = getBeanByNameInternal(beanName);
            }
            if (bean == null) {
                String getClassEvalString = String.format("%s(\"%s\")", GET_CLASS_BY_NAME, beanName);
                if (this.containsKey(GET_CLASS_BY_NAME)) {
                    clazz = (Class<?>) evalKiller.evalWithoutContext(getClassEvalString);
                    if (this.containsKey(GET_BEAN_BY_CLASS)) {
                        bean = evalKiller.evalWithoutContext(String.format("%s(%s)", GET_BEAN_BY_CLASS, getClassEvalString));
                    }
                } else {
                    try {
                        clazz = classLoader.loadClass(beanName);
                    } catch (Exception ignored) {
                        // pass
                    }
                    clazz = Class.forName(beanName);
                }
            }
        } catch (Exception e) {
            // pass
        }
        if (bean != null) {
            return bean;
        }
        if (clazz != null) {
            return clazz;
        }
        return null;
    }

    private Object getBeanByNameInternal(final String beanName) {
        Object bean;
        bean = catching(new Supplier<Object>() {

            @Override
            public Object get() {
                return evalKiller.evalWithoutContext(String.format("%s(\"%s\")", GET_BEAN_BY_NAME, beanName));
            }
        });
        if (bean == null) {
            // 首字母小写再来一遍
            final String retryBeanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
            bean = catching(new Supplier<Object>() {

                @Override
                public Object get() {
                    return evalKiller.evalWithoutContext(String.format("%s(\"%s\")", GET_BEAN_BY_NAME, retryBeanName));
                }
            });
        }
        if (bean == null) {
            // 加上 Impl 再来一遍
            final String retryBeanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1) + "Impl";
            bean = catching(new Supplier<Object>() {

                @Override
                public Object get() {
                    return evalKiller.evalWithoutContext(String.format("%s(\"%s\")", GET_BEAN_BY_NAME, retryBeanName));
                }
            });
        }
        return bean;
    }

    private static <T> T catching(Supplier<T> r) {
        try {
            return r.get();
        } catch (Exception e) {
            return null;
        }
    }

    public interface Supplier<T> {

        T get();
    }
}
