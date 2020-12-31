package pl.ds.websight.autosuggestion.service.impl.handlers.resource;

import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ds.websight.autosuggestion.dto.SuggestionDto;
import pl.ds.websight.autosuggestion.dto.SuggestionListDto;
import pl.ds.websight.autosuggestion.service.AutosuggestionService;
import pl.ds.websight.autosuggestion.service.impl.handlers.AbstractAutosuggestionHandlerService;
import pl.ds.websight.autosuggestion.util.PathUtil;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component(service = AutosuggestionService.class)
public class JcrPathAutosuggestionHandlerServiceImpl extends AbstractAutosuggestionHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(JcrPathAutosuggestionHandlerServiceImpl.class);

    private static final String AUTOSUGGESTION_SERVICE_TYPE = "jcr-path";
    private static final String RESPONSE_PARAMETER_HAS_CHILDREN = "hasChildren";

    @Override
    public String getType() {
        return AUTOSUGGESTION_SERVICE_TYPE;
    }

    @Override
    public SuggestionListDto getSuggestions(ResourceResolver resourceResolver, RequestParameterMap requestParameterMap) {
        Session session = resourceResolver.adaptTo(Session.class);
        if (session == null) {
            LOG.warn("Could not adapt to session, session is null");
            return SuggestionListDto.buildEmpty();
        }

        JcrPathAutosuggestionParams jcrPathAutosuggestionParams = new JcrPathAutosuggestionParams(requestParameterMap);
        String basePath = jcrPathAutosuggestionParams.getBasePath();
        if (!basePath.startsWith(PathUtil.PATH_SEPARATOR)) {
            LOG.warn("Parameter 'basePath' is not absolute path '{}'", basePath);
            return SuggestionListDto.buildEmpty();
        }

        int limit = jcrPathAutosuggestionParams.getLimit();
        boolean isRelativePath = jcrPathAutosuggestionParams.isRelativePath();

        try {
            String parentPath = jcrPathAutosuggestionParams.getParentPath();
            Node node = session.getNode(parentPath);
            if (node == null) {
                LOG.warn("Could not find parent node '{}'", parentPath);
                return SuggestionListDto.buildEmpty();
            }
            List<SuggestionDto> results = new LinkedList<>();
            if (jcrPathAutosuggestionParams.isDeep()) {
                results.addAll(getSuggestionsForType(node, jcrPathAutosuggestionParams.getName(), jcrPathAutosuggestionParams.getType(),
                        isRelativePath, limit + 1));
            } else {
                results.addAll(getSuggestionsForSearchPath(node, jcrPathAutosuggestionParams.getName(), isRelativePath, limit + 1));
            }
            Collections.sort(results, Comparator.comparing(SuggestionDto::getValue));
            return SuggestionListDto.buildFromSuggestions(results.subList(0, results.size()), results.size() > limit);
        } catch (RepositoryException e) {
            String message = "Could not load path autosuggestions";
            LOG.warn(message, e);
            throw new IllegalStateException(message, e);
        }
    }

    private List<SuggestionDto> getSuggestionsForType(Node node, String name, String type, boolean isRelativePath, int limit)
            throws RepositoryException {

        List<SuggestionDto> results = new ArrayList<>();
        addChildSuggestions(results, node, name, type, isRelativePath, limit);
        return results;
    }

    private void addChildSuggestions(List<SuggestionDto> results, Node parent, String name, String type, boolean isRelativePath, int limit)
            throws RepositoryException {

        if (parent.hasNodes()) {
            NodeIterator nodeIterator = parent.getNodes();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                addChildSuggestions(results, node, name, type, isRelativePath, limit);
            }
        }
        if (limit < results.size()) {
            return;
        }
        if (parent.getName().startsWith(name) && parent.getPrimaryNodeType().isNodeType(type)) {
            results.add(createSuggestion(isRelativePath ? PathUtil.getRelativePath(parent.getParent().getPath(), parent.getPath()) :
                    parent.getPath(), parent.hasNodes()));
        }
    }

    private List<SuggestionDto> getSuggestionsForSearchPath(Node parent, String name, boolean isRelativePath, int limit)
            throws RepositoryException {

        NodeIterator nodeIterator = parent.getNodes();
        List<SuggestionDto> results = new ArrayList<>();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            if (node.getName().startsWith(name)) {
                results.add(createSuggestion(isRelativePath ? PathUtil.getRelativePath(parent.getPath(), node.getPath()) : node.getPath()
                        , node.hasNodes()));
            }
            if (limit < results.size()) {
                return results;
            }
        }
        return results;
    }

    private static SuggestionDto createSuggestion(String path, boolean hasChildren) {
        Map<String, Object> data = Collections.singletonMap(RESPONSE_PARAMETER_HAS_CHILDREN, hasChildren);
        return new SuggestionDto(path, data);
    }
}