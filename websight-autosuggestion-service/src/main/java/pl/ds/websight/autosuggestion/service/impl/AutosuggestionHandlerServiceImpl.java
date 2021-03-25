package pl.ds.websight.autosuggestion.service.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ds.websight.autosuggestion.service.AutosuggestionService;

import java.util.HashMap;
import java.util.Map;

@Component(immediate = true)
public class AutosuggestionHandlerServiceImpl implements AutosuggestionHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(AutosuggestionHandlerServiceImpl.class);

    private final Map<String, AutosuggestionService> autosuggestions = new HashMap<>();

    @Reference(
            service = AutosuggestionService.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC)
    private synchronized void bindAutosuggestion(AutosuggestionService autosuggestion) {
        String autosuggestionType = autosuggestion.getType();
        LOG.info("Binding autosuggestion: {}", autosuggestionType);
        if (autosuggestions.containsKey(autosuggestionType)) {
            LOG.warn("Autosuggestion type '{}' is already registered", autosuggestionType);
        } else {
            autosuggestions.put(autosuggestionType, autosuggestion);
        }
    }

    private synchronized void unbindAutosuggestion(AutosuggestionService autosuggestion) {
        String autosuggestionType = autosuggestion.getType();
        LOG.info("Unbinding autosuggestion for type: {}", autosuggestionType);
        autosuggestions.remove(autosuggestionType);
    }

    @Override
    public synchronized AutosuggestionService getAutosuggestionByType(String autosuggestionType) {
        return autosuggestions.get(autosuggestionType);
    }
}