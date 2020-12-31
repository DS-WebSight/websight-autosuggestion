package pl.ds.websight.autosuggestion.dto;

import org.apache.jackrabbit.api.security.user.User;

import javax.jcr.RepositoryException;

public class SystemUserDetailsDto {

    private static final String AUTHORIZABLE_TYPE = "system_user";

    private final String displayName;

    public SystemUserDetailsDto(User user) throws RepositoryException {
        displayName = user.getID();
    }

    public String getType() {
        return AUTHORIZABLE_TYPE;
    }

    public String getDisplayName() {
        return displayName;
    }
}
