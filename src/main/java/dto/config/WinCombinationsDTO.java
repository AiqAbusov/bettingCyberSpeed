package dto.config;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WinCombinationsDTO {

    private Map<String, WinCombinationDTO> winCombinations = new HashMap<>();
    @JsonAnySetter
    public void addSymbol(String key, WinCombinationDTO symbol) {
        winCombinations.put(key, symbol);
    }

}
