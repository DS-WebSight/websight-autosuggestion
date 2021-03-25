package pl.ds.websight.autosuggestion.service.impl.handlers.authorizable;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import pl.ds.websight.autosuggestion.dto.GroupDetailsDto;
import pl.ds.websight.autosuggestion.dto.SuggestionDto;
import pl.ds.websight.autosuggestion.dto.SuggestionListDto;
import pl.ds.websight.autosuggestion.dto.SystemUserDetailsDto;
import pl.ds.websight.autosuggestion.dto.UserDetailsDto;
import pl.ds.websight.autosuggestion.service.impl.handlers.AbstractAutosuggestionHandlerService;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAuthorizableAutosuggestionHandlerService extends AbstractAutosuggestionHandlerService {

    static final String GROUP_PARAMETER = "group";

    @Override
    public SuggestionListDto getSuggestions(ResourceResolver resourceResolver, RequestParameterMap requestParameterMap) {
        try {
            AuthorizablesFinder authorizablesFinder = createAuthorizableFinder(resourceResolver, requestParameterMap);
            String authorizablePattern = getParamStringValue(requestParameterMap, QUERY_PARAMETER);
            List<Authorizable> authorizables = authorizablesFinder.findAuthorizablesByPattern(authorizablePattern);
            List<SuggestionDto> suggestions = new ArrayList<>(authorizables.size());
            for (Authorizable authorizable : authorizables) {
                if (authorizable.isGroup()) {
                    suggestions.add(new SuggestionDto(authorizable.getID(), new GroupDetailsDto((Group) authorizable)));
                } else {
                    User user = (User) authorizable;
                    if (user.isSystemUser()) {
                        suggestions.add(new SuggestionDto(user.getID(), new SystemUserDetailsDto(user)));
                    } else {
                        suggestions.add(new SuggestionDto(user.getID(), new UserDetailsDto(user)));
                    }
                }
            }
            return SuggestionListDto.buildFromSuggestions(suggestions, false);
        } catch (RepositoryException e) {
            throw new IllegalStateException("Could not load authorizable autosuggestions", e);
        }
    }

    private AuthorizablesFinder createAuthorizableFinder(ResourceResolver resourceResolver, RequestParameterMap requestParameterMap)
            throws RepositoryException {
        UserManager userManager = AccessControlUtil.getUserManager(resourceResolver.adaptTo(Session.class));
        String group = getParamStringValue(requestParameterMap, GROUP_PARAMETER);
        return new AuthorizablesFinder(userManager, getResultClass(), group);
    }

    protected abstract Class<? extends Authorizable> getResultClass();

}
