package pl.ds.websight.autosuggestion.service.impl.handlers.authorizable;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.osgi.service.component.annotations.Component;
import pl.ds.websight.autosuggestion.service.AutosuggestionService;

@Component(service = AutosuggestionService.class)
public class UserAutosuggestionHandlerServiceImpl extends AbstractAuthorizableAutosuggestionHandlerService {

    @Override
    public String getType() {
        return "user";
    }

    @Override
    protected Class<? extends Authorizable> getResultClass() {
        return User.class;
    }

}
