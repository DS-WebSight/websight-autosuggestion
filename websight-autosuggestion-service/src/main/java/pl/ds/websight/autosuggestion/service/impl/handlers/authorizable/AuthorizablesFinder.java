package pl.ds.websight.autosuggestion.service.impl.handlers.authorizable;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.QueryBuilder;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import pl.ds.websight.autosuggestion.util.QueryUtil;

import javax.jcr.RepositoryException;
import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.apache.jackrabbit.oak.spi.security.user.UserConstants.REP_AUTHORIZABLE_ID;
import static org.apache.jackrabbit.oak.spi.security.user.UserConstants.REP_PRINCIPAL_NAME;

public class AuthorizablesFinder {

    private static final int RESULTS_LIMIT = 10;
    private static final int DEFAULT_OFFSET = 0;

    private final UserManager userManager;
    private final String groupName;
    private final Class<? extends Authorizable> type;
    private final boolean includeUsers;
    private final boolean includeGroups;

    protected AuthorizablesFinder(@NotNull UserManager userManager, @NotNull Class<? extends Authorizable> type, String groupName) {
        this.userManager = userManager;
        this.groupName = groupName;
        this.type = type;
        boolean assignableFromAuthorizable = type.isAssignableFrom(Authorizable.class);
        this.includeUsers = type.isAssignableFrom(User.class) || assignableFromAuthorizable;
        this.includeGroups = type.isAssignableFrom(Group.class) || assignableFromAuthorizable;
    }

    protected List<Authorizable> findAuthorizablesByPattern(String pattern) throws RepositoryException {
        Iterator<Authorizable> resourceIterator = userManager.findAuthorizables(new Query() {
            @Override
            public <Q> void build(QueryBuilder<Q> builder) {
                builder.setSelector(type);
                if (pattern != null && !pattern.isEmpty()) {
                    Stream.of(
                            QueryUtil.caseInsensitiveLike(builder, '@' + REP_AUTHORIZABLE_ID, pattern),
                            QueryUtil.caseInsensitiveLike(builder, '@' + REP_PRINCIPAL_NAME, pattern),
                            includeGroups ? QueryUtil.caseInsensitiveLike(builder, "@displayName", pattern) : null,
                            includeUsers ? QueryUtil.caseInsensitiveLike(builder, "profile/@firstName", pattern) : null,
                            includeUsers ? QueryUtil.caseInsensitiveLike(builder, "profile/@lastName", pattern) : null)
                            .filter(Objects::nonNull)
                            .reduce(builder::or)
                            .ifPresent(builder::setCondition);
                }
                if (groupName != null) {
                    builder.setScope(groupName, true);
                }
                builder.setLimit(DEFAULT_OFFSET, RESULTS_LIMIT);
            }
        });
        List<Authorizable> result = new LinkedList<>();
        resourceIterator.forEachRemaining(result::add);
        return result;
    }

}
