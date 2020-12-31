package pl.ds.websight.autosuggestion.service;

import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolver;
import pl.ds.websight.autosuggestion.dto.SuggestionListDto;

public interface AutosuggestionService {

    String getType();

    SuggestionListDto getSuggestions(final ResourceResolver resourceResolver,
                                     final RequestParameterMap requestParameterMap) throws IllegalStateException;
}
