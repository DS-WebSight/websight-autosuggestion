package pl.ds.websight.autosuggestion.dto;

import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.oak.commons.PropertiesUtil;

import javax.jcr.RepositoryException;

public class UserDetailsDto {

    private static final String AUTHORIZABLE_TYPE = "user";

    private final String displayName;

    public UserDetailsDto(User user) throws RepositoryException {
        String firstName = PropertiesUtil.toString(user.getProperty("profile/firstName"), "");
        String lastName = PropertiesUtil.toString(user.getProperty("profile/lastName"), "");
        if (firstName.isEmpty() && lastName.isEmpty()) {
            displayName = null;
        } else {
            displayName = firstName + (lastName.isEmpty() ? "" : " " + lastName);
        }
    }

    public String getType() {
        return AUTHORIZABLE_TYPE;
    }

    public String getDisplayName() {
        return displayName;
    }
}
