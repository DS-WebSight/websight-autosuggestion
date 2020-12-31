package pl.ds.websight.autosuggestion.service.impl.handlers;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.jcr.session.SessionImpl;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit5.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.osgi.framework.BundleContext;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class JcrOakContext {

    public static final String AUTOSUGGESTION_TYPE_PARAMETER = "autosuggestionType";

    private static JcrOakContext INSTANCE;
    private static ResourceResolver resourceResolver;
    private static UserManager userManager;
    private static BundleContext bundleContext;

    private final SlingContext context = new SlingContext(ResourceResolverType.JCR_OAK);

    private JcrOakContext() {
        init();
    }

    private void init() {
        resourceResolver = context.resourceResolver();
        bundleContext = context.bundleContext();
        try {
            setUpAuthorizable();
            setUpResources();
        } catch (RepositoryException | PersistenceException e) {
            throw new IllegalStateException(e);
        }
    }

    private void setUpAuthorizable() throws RepositoryException, PersistenceException {
        userManager = createUserManager(resourceResolver);
        final User user = userManager.createUser("testUser", "pass");
        final User user1 = userManager.createUser("testUser1", "pass");
        final Group testGroup = userManager.createGroup("testGroup");
        testGroup.addMember(user);
        testGroup.addMember(user1);
        userManager.createGroup("testGroup2");
        final Group testGroup3 = userManager.createGroup("testGroup3");
        testGroup.addMember(testGroup3);
        context.resourceResolver().commit();
    }

    private void setUpResources() throws PersistenceException {
        HashMap<String, Object> fileProp = new HashMap<>();
        fileProp.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);
        HashMap<String, Object> fileContent = new HashMap<>();
        fileContent.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
        fileContent.put(JcrConstants.JCR_DATA, "");

        HashMap<String, Object> folderProp = new HashMap<>();
        folderProp.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FOLDER);

        final Resource root = resourceResolver.getResource("/");
        final Resource etc = resourceResolver.create(root, "etc", null);
        final Resource parent = resourceResolver.create(etc, "parent", null);
        resourceResolver.create(parent, "parent1", null);
        final Resource parent2 = resourceResolver.create(parent, "parent2", null);
        resourceResolver.create(parent2, "folder2", folderProp);
        final Resource parent3 = resourceResolver.create(parent2, "parent3", null);
        resourceResolver.create(parent3, "folder1", folderProp);
        final Resource file1 = resourceResolver.create(parent, "file1", fileProp);
        resourceResolver.create(file1, "jcr:content", fileContent);
        final Resource file2 = resourceResolver.create(parent2, "file2", fileProp);
        resourceResolver.create(file2, "jcr:content", fileContent);
        final Resource file3 = resourceResolver.create(parent3, "file3", fileProp);
        resourceResolver.create(file3, "jcr:content", fileContent);
        final Resource file4 = resourceResolver.create(parent3, "file4", fileProp);
        resourceResolver.create(file4, "jcr:content", fileContent);
        resourceResolver.commit();
    }

    private static UserManager createUserManager(ResourceResolver resourceResolver) throws RepositoryException {
        SessionImpl sessionImpl = (SessionImpl) resourceResolver.adaptTo(Session.class);
        return sessionImpl.getUserManager();
    }

    private static JcrOakContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JcrOakContext();
        }
        return INSTANCE;
    }

    public static ResourceResolver getResourceResolver() {
        return getInstance().resourceResolver;
    }

    public static UserManager getUserManager() {
        return getInstance().userManager;
    }

    public static RequestParameterMap generateRequestParamMap(AbstractMap.SimpleEntry<String, Object>... entries) {
        Map<String, Object> params = Arrays.stream(entries)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resourceResolver, bundleContext);
        request.setParameterMap(params);
        return request.getRequestParameterMap();
    }
}
