package pl.ds.websight.autosuggestion.service.impl.handlers.authorizable;

import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.ds.websight.autosuggestion.dto.SuggestionDto;
import pl.ds.websight.autosuggestion.dto.SuggestionListDto;
import pl.ds.websight.autosuggestion.service.impl.handlers.JcrOakContext;

import java.util.AbstractMap;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.ds.websight.autosuggestion.service.impl.handlers.AbstractAutosuggestionHandlerService.QUERY_PARAMETER;
import static pl.ds.websight.autosuggestion.service.impl.handlers.JcrOakContext.AUTOSUGGESTION_TYPE_PARAMETER;

public class GroupAutosuggestionHandlerServiceImplTest {

    private static GroupAutosuggestionHandlerServiceImpl handler;
    private static ResourceResolver resourceResolver;

    @BeforeAll
    public static void init() {
        handler = new GroupAutosuggestionHandlerServiceImpl();
        resourceResolver = JcrOakContext.getResourceResolver();
    }

    @Test
    public void shouldGetGroupsByPattern() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "test"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .contains("testGroup", "testGroup2", "testGroup3");
    }
}
