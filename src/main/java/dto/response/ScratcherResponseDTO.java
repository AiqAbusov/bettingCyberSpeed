package dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ScratcherResponseDTO {

    private String[][] matrix;
    private Double reward;
    private Map<String, List<String>> appliedWinningCombinations;
    private String appliedBonusSymbol;
}
