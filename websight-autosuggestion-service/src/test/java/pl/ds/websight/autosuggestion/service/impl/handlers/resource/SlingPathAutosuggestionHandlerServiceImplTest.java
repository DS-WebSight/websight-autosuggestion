package pl.ds.websight.autosuggestion.service.impl.handlers.resource;

import org.apache.jackrabbit.JcrConstants;
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

public class SlingPathAutosuggestionHandlerServiceImplTest {

    private static final String TYPE_PARAMETER = "type";
    private static final String BASE_PATH_PARAMETER = "basePath";
    private static final String RETURN_RELATIVE_PATH_PARAMETER = "returnRelativePath";

    private static ResourceResolver resourceResolver;
    private static SlingPathAutosuggestionHandlerServiceImpl handler;

    @BeforeAll
    public static void setUp() {
        handler = new SlingPathAutosuggestionHandlerServiceImpl();
        resourceResolver = JcrOakContext.getResourceResolver();
    }

    @Test
    public void shouldGetPathsForPattern() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "e"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .containsOnly("/etc");
    }

    @Test
    public void shouldGetRelativePathsForPatternWithPrefix() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "par"),
                new AbstractMap.SimpleEntry<>(BASE_PATH_PARAMETER, "/etc"),
                new AbstractMap.SimpleEntry<>(RETURN_RELATIVE_PATH_PARAMETER, "true"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .containsOnly("parent");
    }

    @Test
    public void shouldGetPathsForEliminatingPattern() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "qweewrretdsf"),
                new AbstractMap.SimpleEntry<>(BASE_PATH_PARAMETER, "/etc"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertTrue(result.getSuggestions().isEmpty());
    }

    @Test
    public void shouldGetPathsForPatternAndNotAllowedType() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "/etc/parent"),
                new AbstractMap.SimpleEntry<>(TYPE_PARAMETER, JcrConstants.NT_RESOURCE));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .containsOnly("/etc/parent");
    }

    @Test
    public void shouldGetPathsForPatternWithPrefixAndNotAllowedType() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "f"),
                new AbstractMap.SimpleEntry<>(BASE_PATH_PARAMETER, "/etc/parent/parent2/parent3"),
                new AbstractMap.SimpleEntry<>(TYPE_PARAMETER, JcrConstants.NT_RESOURCE));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .contains("/etc/parent/parent2/parent3/folder1",
                        "/etc/parent/parent2/parent3/file3",
                        "/etc/parent/parent2/parent3/file4");
    }

    @Test
    public void shouldGetPathsForEliminatingPatternAndNotAllowedType() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "qweewrretdsf"),
                new AbstractMap.SimpleEntry<>(BASE_PATH_PARAMETER, "/etc"),
                new AbstractMap.SimpleEntry<>(TYPE_PARAMETER, JcrConstants.NT_RESOURCE));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertTrue(result.getSuggestions().isEmpty());
    }
}
