package pl.ds.websight.autosuggestion.service.impl.handlers.resource;

import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ds.websight.autosuggestion.dto.SuggestionDto;
import pl.ds.websight.autosuggestion.dto.SuggestionListDto;
import pl.ds.websight.autosuggestion.service.AutosuggestionService;
import pl.ds.websight.autosuggestion.service.impl.handlers.AbstractAutosuggestionHandlerService;
import pl.ds.websight.autosuggestion.util.PathUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Component(service = AutosuggestionService.class)
public class SlingPathAutosuggestionHandlerServiceImpl extends AbstractAutosuggestionHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(SlingPathAutosuggestionHandlerServiceImpl.class);

    private static final String AUTOSUGGESTION_SERVICE_TYPE = "sling-path";
    private static final String RESPONSE_PARAMETER_HAS_CHILDREN = "hasChildren";

    @Override
    public String getType() {
        return AUTOSUGGESTION_SERVICE_TYPE;
    }

    @Override
    public SuggestionListDto getSuggestions(ResourceResolver resourceResolver, RequestParameterMap requestParameterMap) {
        PathAutosuggestionParams pathAutosuggestionParams = new PathAutosuggestionParams(requestParameterMap);

        String basePath = pathAutosuggestionParams.getBasePath();
        if (!basePath.startsWith(PathUtil.PATH_SEPARATOR)) {
            LOG.warn("Parameter 'basePath' is not absolute path '{}'", basePath);
            return SuggestionListDto.buildEmpty();
        }

        List<SuggestionDto> results = new LinkedList<>();
        int limit = pathAutosuggestionParams.getLimit();
        results.addAll(getSuggestionsForSearchPath(pathAutosuggestionParams.getParentPath(), pathAutosuggestionParams.getName(),
                resourceResolver, pathAutosuggestionParams.isRelativePath(), limit + 1));

        return SuggestionListDto.buildFromSuggestions(results.subList(0, results.size()), results.size() > limit);
    }

    private List<SuggestionDto> getSuggestionsForSearchPath(String parentPath, String name, ResourceResolver resourceResolver,
                                                            boolean isRelativePath, int limit) {
        final Resource parentResource = resourceResolver.getResource(parentPath);
        if (parentResource == null) {
            LOG.warn("Could not find parent resource '{}'", parentPath);
            return emptyList();
        }
        return getChildrenSuggestionsByQuery(parentPath, name, parentResource, isRelativePath, limit);
    }

    private List<SuggestionDto> getChildrenSuggestionsByQuery(String parentPath, String name, Resource parentResource, boolean isRelativePath,
                                                              int limit) {
        return StreamSupport.stream(parentResource.getChildren().spliterator(), false)
                .filter(resource -> resource.getName().startsWith(name))
                .sorted(comparing(Resource::getPath))
                .limit(limit)
                .map(resource -> createSuggestion(isRelativePath ? PathUtil.getRelativePath(parentPath, resource.getPath()) :
                        resource.getPath(), resource.hasChildren()))
                .collect(toList());
    }

    private static SuggestionDto createSuggestion(String path, boolean hasChildren) {
        Map<String, Object> data = Collections.singletonMap(RESPONSE_PARAMETER_HAS_CHILDREN, hasChildren);
        return new SuggestionDto(path, data);
    }
}