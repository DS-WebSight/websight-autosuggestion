package pl.ds.websight.autosuggestion.service.impl.handlers;

import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import pl.ds.websight.autosuggestion.service.AutosuggestionService;

public abstract class AbstractAutosuggestionHandlerService implements AutosuggestionService {

    public static final String QUERY_PARAMETER = "query";

    public String getParamStringValue(RequestParameterMap requestParameterMap, String paramName) {
        RequestParameter value = requestParameterMap.getValue(paramName);
        return value != null ? value.getString() : null;
    }

}
