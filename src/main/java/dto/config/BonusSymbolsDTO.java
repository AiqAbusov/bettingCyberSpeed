package dto.config;

import lombok.Data;

import java.util.Map;

@Data
public class BonusSymbolsDTO {
    private Map<String, Integer> symbols;
    private Integer limit;
}
