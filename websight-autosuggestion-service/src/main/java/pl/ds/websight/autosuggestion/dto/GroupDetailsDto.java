package pl.ds.websight.autosuggestion.dto;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.oak.commons.PropertiesUtil;

import javax.jcr.RepositoryException;

public class GroupDetailsDto {

    private static final String AUTHORIZABLE_TYPE = "group";

    private final String displayName;

    public GroupDetailsDto(Group group) throws RepositoryException {
        displayName = PropertiesUtil.toString(group.getProperty("displayName"), null);
    }

    public String getType() {
        return AUTHORIZABLE_TYPE;
    }

    public String getDisplayName() {
        return displayName;
    }

}
