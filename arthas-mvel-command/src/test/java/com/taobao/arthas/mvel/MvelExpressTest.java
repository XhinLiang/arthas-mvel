package com.taobao.arthas.mvel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.taobao.arthas.core.command.express.Express;
import com.taobao.arthas.core.command.express.ExpressException;
import org.junit.jupiter.api.Test;

/**
 * Guardrail for the fork's MVEL feature.
 *
 * <p>The MVEL feature once broke silently for months because an upstream merge dropped its
 * dependencies and wiring and nothing tested it. This test fails the build the moment MVEL
 * evaluation stops working (missing mvel2 dep, mvel2 API change, etc.).
 *
 * @author xhinliang
 */
public class MvelExpressTest {

    @Test
    public void evaluates_arithmetic() throws ExpressException {
        Express express = new MvelExpress(getClass().getClassLoader());
        assertEquals(2, express.get("1 + 1"));
    }

    @Test
    public void evaluates_static_method_call() throws ExpressException {
        Express express = new MvelExpress(getClass().getClassLoader());
        assertEquals("4", express.get("String.valueOf(2 * 2)"));
    }

    @Test
    public void is_returns_boolean_result() throws ExpressException {
        Express express = new MvelExpress(getClass().getClassLoader());
        assertTrue(express.is("1 < 2"));
    }

    @Test
    public void factory_caches_per_classloader() {
        Express a = MvelExpressFactory.mvelExpress(getClass().getClassLoader());
        Express b = MvelExpressFactory.mvelExpress(getClass().getClassLoader());
        assertEquals(a, b);
    }
}
