package dto.config;

import lombok.Data;

import java.util.Map;


@Data
public class StandardSymbolsDTO {
    private int row;
    private int column;
    private Map<String, Integer> symbols;

}
