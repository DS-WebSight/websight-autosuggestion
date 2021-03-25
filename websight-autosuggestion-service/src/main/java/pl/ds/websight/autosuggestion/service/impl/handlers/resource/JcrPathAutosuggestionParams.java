package pl.ds.websight.autosuggestion.service.impl.handlers.resource;

import org.apache.sling.api.request.RequestParameterMap;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

class JcrPathAutosuggestionParams extends PathAutosuggestionParams {

    private static final String DEEP_PARAMETER = "deep";
    private static final String TYPE_PARAMETER = "type";
    private static final String NT_BASE = "nt:base";

    private String type;
    private RequestParameterMap requestParameterMap;

    JcrPathAutosuggestionParams(RequestParameterMap requestParameterMap) {
        super(requestParameterMap);
        this.requestParameterMap = requestParameterMap;
    }

    boolean isDeep() {
        return Boolean.parseBoolean(defaultIfBlank(getParamStringValue(requestParameterMap, DEEP_PARAMETER), "false"));
    }

    String getType() {
        if (type == null) {
            this.type = defaultIfBlank(getParamStringValue(requestParameterMap, TYPE_PARAMETER), NT_BASE);
        }
        return type;
    }
}
