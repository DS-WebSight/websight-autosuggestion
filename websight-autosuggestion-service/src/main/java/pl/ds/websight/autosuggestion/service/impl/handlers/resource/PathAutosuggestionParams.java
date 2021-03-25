package pl.ds.websight.autosuggestion.service.impl.handlers.resource;

import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import pl.ds.websight.autosuggestion.util.PathUtil;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

class PathAutosuggestionParams {

    private static final String QUERY_PARAMETER = "query";
    private static final String BASE_PATH_PARAMETER = "basePath";
    private static final String RETURN_RELATIVE_PATH_PARAMETER = "returnRelativePath";
    private static final String LIMIT_PARAMETER = "limit";

    private static final int MAX_RESULTS_SIZE = 100;

    private String basePath;
    private String query;
    private String name;
    private String parentPath;
    private String fullPath;

    private RequestParameterMap requestParameterMap;

    PathAutosuggestionParams(RequestParameterMap requestParameterMap) {
        this.requestParameterMap = requestParameterMap;
        this.fullPath = PathUtil.getFullPath(getBasePath(), getQuery());
    }

    String getParamStringValue(RequestParameterMap requestParameterMap, String paramName) {
        RequestParameter value = requestParameterMap.getValue(paramName);
        return value != null ? value.getString() : null;
    }

    String getBasePath() {
        if (basePath == null) {
            basePath = defaultIfBlank(getParamStringValue(requestParameterMap, BASE_PATH_PARAMETER), "/");
        }
        return basePath;
    }

    String getQuery() {
        if (query == null) {
            query = defaultIfBlank(getParamStringValue(requestParameterMap, QUERY_PARAMETER), "");
        }
        return query;
    }

    String getName() {
        if (name == null) {
            name = PathUtil.getName(fullPath);
        }
        return name;
    }

    String getParentPath() {
        if (parentPath == null) {
            this.parentPath = PathUtil.getParentPath(fullPath);
        }
        return parentPath;
    }

    boolean isRelativePath() {
        return Boolean.parseBoolean(defaultIfBlank(getParamStringValue(requestParameterMap, RETURN_RELATIVE_PATH_PARAMETER),
                "false"));
    }

    int getLimit() {
        return Math.min(Integer.parseInt(defaultIfBlank(getParamStringValue(requestParameterMap, LIMIT_PARAMETER), "10")),
                MAX_RESULTS_SIZE);
    }
}
