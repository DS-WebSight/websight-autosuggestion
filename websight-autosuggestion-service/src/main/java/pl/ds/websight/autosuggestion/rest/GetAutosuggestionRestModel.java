package pl.ds.websight.autosuggestion.rest;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import pl.ds.websight.request.parameters.support.annotations.RequestParameter;
import pl.ds.websight.rest.framework.Errors;
import pl.ds.websight.rest.framework.Validatable;

import javax.validation.constraints.NotNull;

@Model(adaptables = SlingHttpServletRequest.class)
public class GetAutosuggestionRestModel implements Validatable {

    @Self
    private SlingHttpServletRequest request;

    @NotNull(message = "Parameter 'autosuggestionType' cannot be null")
    @RequestParameter
    private String autosuggestionType;

    @Override
    public Errors validate() {
        Errors errors = Errors.createErrors();
        if (autosuggestionType.isEmpty()) {
            errors.add("autosuggestionType", autosuggestionType, "Parameter 'autosuggestionType' should not be empty");
        }
        return errors;
    }

    public String getAutosuggestionType() {
        return autosuggestionType;
    }

    public ResourceResolver getResourceResolver() {
        return request.getResourceResolver();
    }

    public RequestParameterMap getRequestParameters() {
        return request.getRequestParameterMap();
    }
}