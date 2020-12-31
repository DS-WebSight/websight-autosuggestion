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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.ds.websight.autosuggestion.service.impl.handlers.AbstractAutosuggestionHandlerService.QUERY_PARAMETER;
import static pl.ds.websight.autosuggestion.service.impl.handlers.JcrOakContext.AUTOSUGGESTION_TYPE_PARAMETER;
import static pl.ds.websight.autosuggestion.service.impl.handlers.authorizable.AbstractAuthorizableAutosuggestionHandlerService.GROUP_PARAMETER;

public class AuthorizableAutosuggestionHandlerServiceImplTest {

    private static AuthorizableAutosuggestionHandlerServiceImpl handler;
    private static ResourceResolver resourceResolver;

    @BeforeAll
    public static void init() {
        handler = new AuthorizableAutosuggestionHandlerServiceImpl();
        resourceResolver = JcrOakContext.getResourceResolver();
    }

    @Test
    public void shouldGetAuthorizablesByPattern() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "test"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .contains("testUser", "testUser1", "testGroup", "testGroup2", "testGroup3");
    }

    @Test
    public void shouldGetAuthorizablesByWrongPattern() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "wrongPattern"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertTrue(result.getSuggestions().isEmpty());
    }

    @Test
    public void shouldGetAuthorizablesByEliminatingPattern() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "testUser1"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .contains("testUser1");
    }

    @Test
    public void shouldGetAuthorizablesByPatternAndGroup() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "test"),
                new AbstractMap.SimpleEntry<>(GROUP_PARAMETER, "testGroup"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .contains("testUser", "testUser1", "testGroup3");
    }
}
