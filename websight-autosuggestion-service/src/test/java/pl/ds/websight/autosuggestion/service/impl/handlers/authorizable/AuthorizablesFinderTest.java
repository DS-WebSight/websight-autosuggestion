package pl.ds.websight.autosuggestion.service.impl.handlers.authorizable;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.ds.websight.autosuggestion.service.impl.handlers.JcrOakContext;

import javax.jcr.RepositoryException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthorizablesFinderTest {

    private static UserManager userManager;

    @BeforeAll
    public static void setUp() {
        userManager = JcrOakContext.getUserManager();
    }

    @Test
    public void shouldFetchUserNamesByPattern() throws RepositoryException {
        AuthorizablesFinder usersFinder = new AuthorizablesFinder(userManager, User.class, null);
        List<Authorizable> userNames = usersFinder.findAuthorizablesByPattern("test");

        assertThat(userNames).extracting(Authorizable::getID).contains("testUser", "testUser1");
    }

    @Test
    public void shouldFetchUserNamesByWrongPattern() throws RepositoryException {
        AuthorizablesFinder usersFinder = new AuthorizablesFinder(userManager, User.class, null);
        List<Authorizable> userNames = usersFinder.findAuthorizablesByPattern("testUserWrongName");

        assertTrue(userNames.isEmpty());
    }

    @Test
    public void shouldFetchUserNamesByEliminatingPattern() throws RepositoryException {
        AuthorizablesFinder usersFinder = new AuthorizablesFinder(userManager, User.class, null);
        List<Authorizable> userNames = usersFinder.findAuthorizablesByPattern("testUser1");

        assertThat(userNames).extracting(Authorizable::getID).contains("testUser1");
    }

    @Test
    public void shouldFetchUserNamesByPatternForGroup() throws RepositoryException {
        AuthorizablesFinder usersFinder = new AuthorizablesFinder(userManager, User.class, "testGroup");
        List<Authorizable> userNames = usersFinder.findAuthorizablesByPattern("test");

        assertThat(userNames).extracting(Authorizable::getID).contains("testUser", "testUser1");
    }

    @Test
    public void shouldFetchUserNamesByPatternForEliminatingGroup() throws RepositoryException {
        AuthorizablesFinder usersFinder = new AuthorizablesFinder(userManager, User.class, "testGroup2");
        List<Authorizable> userNames = usersFinder.findAuthorizablesByPattern("test");

        assertTrue(userNames.isEmpty());
    }

    @Test
    public void shouldFetchGroupNamesByPattern() throws RepositoryException {
        AuthorizablesFinder groupsFinder = new AuthorizablesFinder(userManager, Group.class, null);
        List<Authorizable> groupNames = groupsFinder.findAuthorizablesByPattern("test");

        assertThat(groupNames).extracting(Authorizable::getID).contains("testGroup", "testGroup2", "testGroup3");
    }

    @Test
    public void shouldFetchGroupNamesByWrongPattern() throws RepositoryException {
        AuthorizablesFinder groupsFinder = new AuthorizablesFinder(userManager, Group.class, null);
        List<Authorizable> groupNames = groupsFinder.findAuthorizablesByPattern("testGroupWrongName");

        assertTrue(groupNames.isEmpty());
    }

    @Test
    public void shouldFetchGroupNamesByEliminatingPattern() throws RepositoryException {
        AuthorizablesFinder groupsFinder = new AuthorizablesFinder(userManager, Group.class, null);
        List<Authorizable> groupNames = groupsFinder.findAuthorizablesByPattern("testGroup2");

        assertThat(groupNames).extracting(Authorizable::getID).contains("testGroup2");
    }

    @Test
    public void shouldFetchGroupNamesByPatternForGroup() throws RepositoryException {
        AuthorizablesFinder groupsFinder = new AuthorizablesFinder(userManager, Group.class, "testGroup");
        List<Authorizable> groupNames = groupsFinder.findAuthorizablesByPattern("test");

        assertThat(groupNames).extracting(Authorizable::getID).contains("testGroup3");
    }

    @Test
    public void shouldFetchGroupNamesByPatternForEliminatingGroup() throws RepositoryException {
        AuthorizablesFinder groupsFinder = new AuthorizablesFinder(userManager, Group.class, "testGroup2");
        List<Authorizable> groupNames = groupsFinder.findAuthorizablesByPattern("test");

        assertTrue(groupNames.isEmpty());
    }
}
