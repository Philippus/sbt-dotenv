package au.com.onegeek.sbtdotenv;

import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
 * Change the actual program environment so that changes propagate to child processes
 *
 * Taken from: http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java/7201825#7201825
 *
 * Created by mfellows on 20/07/2014.
 */
public abstract class NativeEnvironmentManager {
    public abstract void setEnv(String key, String value);

    static class EnvironmentException extends RuntimeException {
        EnvironmentException(String key) {
            super("Failed to set environment variable: " + key);
        }
    }

    static class WindowsNativeEnvironmentManagerImpl extends NativeEnvironmentManager {
        public interface WindowsEnvironmentLibC extends Library {
            WindowsEnvironmentLibC INSTANCE = (
                (WindowsEnvironmentLibC) Native.load("msvcrt",
                    WindowsEnvironmentLibC.class)
            );

            int _putenv(String name);
        }

        private WindowsEnvironmentLibC libc = WindowsEnvironmentLibC.INSTANCE;

        @Override
        public void setEnv(String name, String value) {
            String s = name + "=";
            if (value != null)
                s += value;

            if (libc._putenv(s) != 0)
                throw new EnvironmentException(name);
        }
    }

    static class PosixNativeEnvironmentManagerImpl extends NativeEnvironmentManager {
        public interface PosixEnvironmentLibC extends Library {
            PosixEnvironmentLibC INSTANCE = (
                (PosixEnvironmentLibC) Native.load("c",
                    PosixEnvironmentLibC.class)
            );

            int setenv(String name, String value, int overwrite);
            int unsetenv(String name);
        }

        private PosixEnvironmentLibC libc = PosixEnvironmentLibC.INSTANCE;

        @Override
        public void setEnv(String name, String value) {
            int result;
            if (value != null)
                result = libc.setenv(name, value, 1);
            else
                result = libc.unsetenv(name);

            if (result != 0)
                throw new EnvironmentException(name);
        }
    }

    private static NativeEnvironmentManager instance = null;
    private static NativeEnvironmentManager getInstance() {
        if (instance == null) {
            if (Platform.isWindows())
                instance = new WindowsNativeEnvironmentManagerImpl();
            else
                instance = new PosixNativeEnvironmentManagerImpl();
        }

        return instance;
    }

    public static void setEnv(Map<String, String> env) {
        NativeEnvironmentManager envManager = NativeEnvironmentManager.getInstance();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            envManager.setEnv(entry.getKey(), entry.getValue());
        }
    }
}
