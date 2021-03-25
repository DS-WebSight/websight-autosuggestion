package pl.ds.websight.autosuggestion.dto;

public class SuggestionDto {

    private final String value;
    private final Object data;

    public SuggestionDto(String value) {
        this.value = value;
        this.data = null;
    }

    public SuggestionDto(String value, Object data) {
        this.value = value;
        this.data = data;
    }

    public String getValue() {
        return value;
    }

    public Object getData() {
        return data;
    }
}