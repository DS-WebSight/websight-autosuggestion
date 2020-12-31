package pl.ds.websight.autosuggestion.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ds.websight.autosuggestion.dto.SuggestionListDto;
import pl.ds.websight.autosuggestion.service.AutosuggestionService;
import pl.ds.websight.autosuggestion.service.impl.AutosuggestionHandlerService;
import pl.ds.websight.rest.framework.RestAction;
import pl.ds.websight.rest.framework.RestActionResult;
import pl.ds.websight.rest.framework.annotations.SlingAction;

import static pl.ds.websight.rest.framework.annotations.SlingAction.HttpMethod.GET;

@Component
@SlingAction(GET)
public class GetAutosuggestionRestAction implements RestAction<GetAutosuggestionRestModel, SuggestionListDto> {

    private static final Logger LOG = LoggerFactory.getLogger(GetAutosuggestionRestAction.class);

    @Reference
    private AutosuggestionHandlerService autosuggestionHandlerService;

    @Override
    public RestActionResult<SuggestionListDto> perform(GetAutosuggestionRestModel model) {
        String autosuggestionType = model.getAutosuggestionType();
        AutosuggestionService autosuggestion = autosuggestionHandlerService.getAutosuggestionByType(autosuggestionType);
        if (autosuggestion == null) {
            String messageDetails = "Autosuggestion type not supported: " + autosuggestionType;
            LOG.warn(messageDetails);
            return RestActionResult.failure("Cannot load autosuggestion", messageDetails);
        }
        SuggestionListDto suggestionListDto = autosuggestion.getSuggestions(model.getResourceResolver(), model.getRequestParameters());
        return RestActionResult.success(suggestionListDto);
    }
}
