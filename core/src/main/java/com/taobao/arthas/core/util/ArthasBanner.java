package com.taobao.arthas.core.util;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.arthas.deps.org.slf4j.Logger;
import com.alibaba.arthas.deps.org.slf4j.LoggerFactory;
import com.taobao.arthas.common.PidUtils;
import com.taobao.arthas.core.shell.ShellServerOptions;
import com.taobao.text.ui.TableElement;
import com.taobao.text.util.RenderUtil;

/**
 * @author beiwei30 on 16/11/2016.
 */
public class ArthasBanner {
    private static final String LOGO_LOCATION = "/com/taobao/arthas/core/res/logo.txt";
    private static final String CREDIT_LOCATION = "/com/taobao/arthas/core/res/thanks.txt";
    private static final String VERSION_LOCATION = "/com/taobao/arthas/core/res/version";
    private static final String GITHUB = "https://github.com/xhinliang/arthas-mvel";
    private static final String TUTORIALS = "https://alibaba.github.io/arthas/arthas-tutorials";

    private static String LOGO = "Welcome to Arthas-MVEL";
    private static String VERSION = "unknown";
    private static String THANKS = "";

    private static final Logger logger = LoggerFactory.getLogger(ArthasBanner.class);

    static {
        try {
            String logoText = IOUtils.toString(ShellServerOptions.class.getResourceAsStream(LOGO_LOCATION));
            THANKS = IOUtils.toString(ShellServerOptions.class.getResourceAsStream(CREDIT_LOCATION));
            InputStream versionInputStream = ShellServerOptions.class.getResourceAsStream(VERSION_LOCATION);
            if (versionInputStream != null) {
                VERSION = IOUtils.toString(versionInputStream).trim();
            } else {
                String implementationVersion = ArthasBanner.class.getPackage().getImplementationVersion();
                if (implementationVersion != null) {
                    VERSION = implementationVersion;
                }
            }

            LOGO = logoText;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String github() {
        return GITHUB;
    }

    public static String tutorials() {
        return TUTORIALS;
    }

    public static String credit() {
        return THANKS;
    }

    public static String version() {
        return VERSION;
    }

    public static String logo() {
        return LOGO;
    }

    public static String plainTextLogo() {
        return RenderUtil.ansiToPlainText(LOGO);
    }

    public static String welcome() {
        return welcome(Collections.<String, String>emptyMap());
    }

    public static String welcome(Map<String, String> infos) {
        logger.info("arthas-mvel version: " + version());
        TableElement table = new TableElement().rightCellPadding(1)
                        .row("github", github())
                        .row("tutorials", tutorials())
                        .row("version", version())
                        .row("pid", PidUtils.currentPid())
                        .row("time", DateUtils.getCurrentDate());
        for (Entry<String, String> entry : infos.entrySet()) {
            table.row(entry.getKey(), entry.getValue());
        }

        return logo() + "\n" + RenderUtil.render(table);
    }
}
