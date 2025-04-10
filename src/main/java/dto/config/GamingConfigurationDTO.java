package dto.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
public class GamingConfigurationDTO implements Serializable {
    private Integer columns;
    private Integer rows;

    private SymbolsDTO symbols;
    private ProbabilitiesDTO probabilities;

    @JsonProperty(value = "win_combinations")
    private WinCombinationsDTO winCombinations;
}
