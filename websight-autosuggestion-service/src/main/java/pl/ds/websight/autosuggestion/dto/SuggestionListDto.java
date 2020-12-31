package pl.ds.websight.autosuggestion.dto;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class SuggestionListDto {

    private final boolean hasMoreResults;
    private final List<SuggestionDto> suggestions;

    private SuggestionListDto(List<SuggestionDto> suggestions, boolean hasMoreResults) {
        this.suggestions = suggestions;
        this.hasMoreResults = hasMoreResults;
    }

    public static SuggestionListDto buildEmpty() {
        return new SuggestionListDto(emptyList(), false);
    }

    public static SuggestionListDto buildFromValues(List<String> suggestions, boolean hasMoreResults) {
        return new SuggestionListDto(suggestions.stream()
                .map(SuggestionDto::new)
                .collect(toList()), hasMoreResults);
    }

    public static SuggestionListDto buildFromSuggestions(List<SuggestionDto> suggestions, boolean hasMoreResults) {
        return new SuggestionListDto(suggestions, hasMoreResults);
    }

    public List<SuggestionDto> getSuggestions() {
        return suggestions;
    }

    public boolean getHasMoreResults() {
        return hasMoreResults;
    }
}
