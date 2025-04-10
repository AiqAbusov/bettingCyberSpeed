package dto.config;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SymbolsDTO {
    private Map<String, Symbol> symbols = new HashMap<>();
    @JsonAnySetter
    public void addSymbol(String key, Symbol symbol) {
        symbols.put(key, symbol);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Symbol {

        @JsonProperty("reward_multiplier")
        private Double rewardMultiplier;
        private String type;
        private String impact;
        private Double extra;
    }


}
