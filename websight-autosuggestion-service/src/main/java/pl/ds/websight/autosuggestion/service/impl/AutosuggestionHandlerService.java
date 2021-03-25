package pl.ds.websight.autosuggestion.service.impl;

import pl.ds.websight.autosuggestion.service.AutosuggestionService;

public interface AutosuggestionHandlerService {

    AutosuggestionService getAutosuggestionByType(String autosuggestionType);
}
