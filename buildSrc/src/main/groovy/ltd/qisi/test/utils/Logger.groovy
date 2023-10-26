package ltd.qisi.test.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.Project

class Logger {
    private static org.gradle.api.logging.Logger logger
    static boolean enabled
    private static Gson sGson = new GsonBuilder()
            .setPrettyPrinting()
            .create()

    static void make(Project project) {
        logger = project.getLogger()

    }

    static void i(String info) {
        if (!enabled) return
        if (null != info && null != logger) {
            logger.info("AppTest >>> " + info)
        }
    }

    static void e(String error) {
        if (!enabled) return
        if (null != error && null != logger) {
            logger.error("AppTest >>> " + error)
        }
    }

    static void w(String warning) {
        if (!enabled) return
        if (null != warning && null != logger) {
            logger.warn("AppTest >>> " + warning)
        }
    }

    static void json(String tag, Object o) {
        if (!enabled) return
        if (null != o && null != logger) {
            logger.warn("$tag ${sGson.toJson(o)}")
        }
    }

}