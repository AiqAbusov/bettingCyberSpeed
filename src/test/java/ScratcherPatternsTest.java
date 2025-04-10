import com.fasterxml.jackson.databind.ObjectMapper;
import dto.response.ScratcherResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.impl.ScratchServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScratcherPatternsTest {

    private ScratchServiceImpl service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        service = new ScratchServiceImpl();
    }

    @Test
    void testHorizontalWin() throws Exception {
        String[][] matrix = {
                {"A", "A", "A", "A"},
                {"D", "B", "C", "Z"},
                {"G", "A", "C", "FF"},
                {"A", "f", "c", "A"}
        };

        String resultJson = service.scratch(matrix, 100.0);
        ScratcherResponseDTO response = objectMapper.readValue(resultJson, ScratcherResponseDTO.class);

        assertNotNull(response);
        assertTrue(response.getAppliedWinningCombinations().containsKey("A"));

        List<String> patterns = response.getAppliedWinningCombinations().get("A");
        assertTrue(patterns.contains("same_symbols_horizontally"));
    }

    @Test
    void testVerticalWin() throws Exception {
        String[][] matrix = {
                {"A", "B", "C", "D"},
                {"A", "B", "C", "E"},
                {"A", "D", "Y", "F"},
                {"A", "C", "C", "F"}
        };

        String resultJson = service.scratch(matrix, 100.0);
        ScratcherResponseDTO response = objectMapper.readValue(resultJson, ScratcherResponseDTO.class);

        assertNotNull(response);
        assertTrue(response.getAppliedWinningCombinations().containsKey("A"));

        List<String> patterns = response.getAppliedWinningCombinations().get("A");
        assertTrue(patterns.contains("same_symbols_vertically"));
    }

    @Test
    void testDiagonalLeftToRightWin() throws Exception {
        String[][] matrix = {
                {"A", "B", "C", "D"},
                {"X", "A", "Y", "Z"},
                {"1", "2", "A", "3"},
                {"0", "f", "g", "A"}
        };

        String resultJson = service.scratch(matrix, 100.0);
        ScratcherResponseDTO response = objectMapper.readValue(resultJson, ScratcherResponseDTO.class);

        assertNotNull(response);
        assertTrue(response.getAppliedWinningCombinations().containsKey("A"));

        List<String> patterns = response.getAppliedWinningCombinations().get("A");
        assertTrue(patterns.stream().anyMatch(s -> s.contains("same_symbols_diagonally_left_to_right")));
    }

    @Test
    void testDiagonalRightToLeftWin() throws Exception {
        String[][] matrix = {
                {"A", "B", "C", "A"},
                {"1", "C", "A", "F"},
                {"D", "A", "C", "E"},
                {"A", "B", "C", "D"}
        };

        String resultJson = service.scratch(matrix, 100.0);
        ScratcherResponseDTO response = objectMapper.readValue(resultJson, ScratcherResponseDTO.class);

        assertNotNull(response);
        assertTrue(response.getAppliedWinningCombinations().containsKey("A"));

        List<String> patterns = response.getAppliedWinningCombinations().get("A");
        assertTrue(patterns.stream().anyMatch(s -> s.contains("same_symbols_diagonally_right_to_left")));
    }

    @Test
    void testSameSymbolCount() throws Exception {
        String[][] matrix = {
                {"A", "B", "C", "A"},
                {"A", "B", "C", "Z"},
                {"A", "X", "Y", "A"},
                {"A", "f", "c", "A"}
        };

        String resultJson = service.scratch(matrix, 100.0);
        ScratcherResponseDTO response = objectMapper.readValue(resultJson, ScratcherResponseDTO.class);

        assertNotNull(response);
        assertTrue(response.getAppliedWinningCombinations().containsKey("A"));

        List<String> patterns = response.getAppliedWinningCombinations().get("A");
        assertTrue(patterns.contains("same_symbol_7_times"));
    }


    //Vertical --> 5, Horizontal --> 5, Diagonal -->5, 9 repeat --> 20 , A-->50, 10x
    //5*5*5*20=2500*50=125000
    //Betting Amount =100
    //125000 * 100 = 1.25E7
    //1.25E7 * 10x= 1.25E8
    @Test
    void testMultiplePatternsAndRewardValue() throws Exception {
        String[][] matrix = {
                {"A", "A", "A", "A"},
                {"A", "B", "A", "C"},
                {"A", "A", "C", "D"},
                {"A", "E", "F", "10x"}
        };

        double betAmount = 100.0;
        String resultJson = service.scratch(matrix, betAmount);

        ScratcherResponseDTO response = objectMapper.readValue(resultJson, ScratcherResponseDTO.class);

        assertNotNull(response);
        assertEquals(1.25E8, response.getReward(), 0.001);


        assertNotNull(response.getAppliedWinningCombinations());
        assertTrue(response.getAppliedWinningCombinations().containsKey("A"));
        assertTrue(response.getAppliedBonusSymbol().equals("10x") || response.getAppliedBonusSymbol().contains("10x"));

        var patterns = response.getAppliedWinningCombinations().get("A");
        assertTrue(patterns.size() >= 3, "Should match multiple patterns like vertical, horizontal, diagonal");
    }


}
