package com.taobao.arthas.core.command.express;

import static com.taobao.arthas.core.command.express.MvelContext.AUTO_LOAD_FUNCTIONS;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;
import org.mvel2.optimizers.OptimizerFactory;

/**
 * @author xhinliang <xhinliang@gmail.com>
 * Created on 2019-04-16
 */
public class MvelEvalKiller {

    // local context 现在没啥用
    private final HashMap<String, Object> localContext = new HashMap<String, Object>();

    private final MvelContext globalContext;

    static {
        // see https://github.com/mvel/mvel/issues/234
        OptimizerFactory.setDefaultOptimizer("reflective");
    }

    public MvelEvalKiller(ClassLoader classLoader) {
        this.globalContext = new MvelContext(this, classLoader);
    }

    public Object eval(String command) {
        return MVEL.eval(command, localContext, globalContext);
    }

    public Object evalWithoutContext(String command) {
        Map<String, Object> systemContext = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : globalContext.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (AUTO_LOAD_FUNCTIONS.contains(key)) {
                systemContext.put(key, val);
            }
        }
        Map<String, Object> tempContext = new HashMap<String, Object>();
        return MVEL.eval(command, tempContext, systemContext);
    }

    public MvelContext getGlobalContext() {
        return globalContext;
    }
}
