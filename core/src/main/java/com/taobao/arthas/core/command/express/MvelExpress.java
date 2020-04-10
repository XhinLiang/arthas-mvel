package com.taobao.arthas.core.command.express;

import com.alibaba.arthas.deps.org.slf4j.Logger;
import com.alibaba.arthas.deps.org.slf4j.LoggerFactory;

/**
 * @author xhinliang
 */
public class MvelExpress implements Express {

    private static final Logger logger = LoggerFactory.getLogger(MvelExpress.class);

    private final MvelEvalKiller evalKiller;

    public MvelExpress(ClassLoader classLoader) {
        evalKiller = new MvelEvalKiller(classLoader);
    }

    @Override
    public Object get(String express) throws ExpressException {
        try {
            return evalKiller.eval(express);
        } catch (Exception e) {
            logger.error("Error during evaluating the expression:", e);
            throw new ExpressException(express, e);
        }
    }

    @Override
    public boolean is(String express) throws ExpressException {
        final Object ret = get(express);
        return null != ret && ret instanceof Boolean && (Boolean) ret;
    }

    @Override
    public Express bind(Object object) {
        // TODO 现在没啥用...
        return this;
    }

    @Override
    public Express bind(String name, Object value) {
        evalKiller.getGlobalContext().put(name, value);
        return this;
    }

    @Override
    public Express reset() {
        evalKiller.getGlobalContext().clear();
        return this;
    }
}
