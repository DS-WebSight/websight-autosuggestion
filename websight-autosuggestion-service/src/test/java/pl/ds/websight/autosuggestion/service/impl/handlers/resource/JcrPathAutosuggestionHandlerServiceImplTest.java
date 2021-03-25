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
import static pl.ds.websight.autosuggestion.service.impl.handlers.AbstractAutosuggestionHandlerService.QUERY_PARAMETER;
import static pl.ds.websight.autosuggestion.service.impl.handlers.JcrOakContext.AUTOSUGGESTION_TYPE_PARAMETER;

public class JcrPathAutosuggestionHandlerServiceImplTest {

    private static final String TYPE_PARAMETER = "type";
    private static final String BASE_PATH_PARAMETER = "basePath";
    private static final String DEEP_PARAMETER = "deep";
    private static final String LIMIT_PARAMETER = "limit";

    private static ResourceResolver resourceResolver;
    private static JcrPathAutosuggestionHandlerServiceImpl handler;

    @BeforeAll
    public static void setUp() {
        handler = new JcrPathAutosuggestionHandlerServiceImpl();
        resourceResolver = JcrOakContext.getResourceResolver();
    }

    @Test
    public void shouldGetPathsForPatternWithPrefixAndAllowedTypeWithDeep() {
        final RequestParameterMap requestParameterMap = JcrOakContext.generateRequestParamMap(
                new AbstractMap.SimpleEntry<>(AUTOSUGGESTION_TYPE_PARAMETER, handler.getType()),
                new AbstractMap.SimpleEntry<>(QUERY_PARAMETER, "fo"),
                new AbstractMap.SimpleEntry<>(BASE_PATH_PARAMETER, "/etc/parent/parent2"),
                new AbstractMap.SimpleEntry<>(TYPE_PARAMETER, JcrConstants.NT_FOLDER),
                new AbstractMap.SimpleEntry<>(LIMIT_PARAMETER, 5),
                new AbstractMap.SimpleEntry<>(DEEP_PARAMETER, "true"));

        final SuggestionListDto result = handler.getSuggestions(resourceResolver, requestParameterMap);

        assertThat(result.getSuggestions()).extracting(SuggestionDto::getValue)
                .contains("/etc/parent/parent2/parent3/folder1",
                        "/etc/parent/parent2/folder2");
    }
}
