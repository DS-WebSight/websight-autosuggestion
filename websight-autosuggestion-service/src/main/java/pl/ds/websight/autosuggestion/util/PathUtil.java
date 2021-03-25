package pl.ds.websight.autosuggestion.util;

import static org.apache.commons.lang3.StringUtils.removeStart;

public class PathUtil {

    public static final String PATH_SEPARATOR = "/";

    private PathUtil() {
        //no instances
    }

    public static String getRelativePath(String basePath, String path) {
        return removeStart(path.substring(basePath.length()), "/");
    }

    public static String getParentPath(String fullPath) {
        int lastPathSeparatorIndex = fullPath.lastIndexOf(PATH_SEPARATOR);
        return lastPathSeparatorIndex == 0 ? PATH_SEPARATOR : fullPath.substring(0, lastPathSeparatorIndex);
    }

    public static String getName(String fullPath) {
        int lastPathSeparatorIndex = fullPath.lastIndexOf(PATH_SEPARATOR);
        return lastPathSeparatorIndex < fullPath.length() - 1 ? fullPath.substring(lastPathSeparatorIndex + 1) : "";
    }

    public static String getFullPath(String basePath, String query) {
        return (basePath.endsWith(PATH_SEPARATOR) ? basePath.substring(0, basePath.length() - 1) : basePath)
                + PATH_SEPARATOR + (query.startsWith(PATH_SEPARATOR) ? query.substring(1) : query);
    }
}
