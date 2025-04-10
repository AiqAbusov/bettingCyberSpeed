package dto.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProbabilitiesDTO {


    @JsonProperty("standard_symbols")
    private StandardSymbolsDTO[] standardSymbols;

    @JsonProperty("bonus_symbols")
    private BonusSymbolsDTO bonusSymbols;


}
