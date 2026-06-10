package com.taobao.arthas.mvel;

import java.util.Collections;
import java.util.List;

import com.taobao.arthas.core.shell.command.Command;
import com.taobao.arthas.core.shell.command.CommandResolver;

/**
 * Exposes the {@code mvel} command to Arthas via the external command SPI
 * ({@code META-INF/services/com.taobao.arthas.core.shell.command.CommandResolver}).
 *
 * @author xhinliang
 */
public class MvelCommandResolver implements CommandResolver {

    @Override
    public List<Command> commands() {
        return Collections.singletonList(Command.create(MvelCommand.class));
    }
}
