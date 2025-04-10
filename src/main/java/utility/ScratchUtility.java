package utility;

import dto.config.WinCombinationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;


public final class ScratchUtility {
    private ScratchUtility() {
    }


    public static Predicate<WinCombinationDTO> isSameSymbol =
            winCombinationDTO ->
                    winCombinationDTO.getWhen().equals("same_symbols") &&
                            winCombinationDTO.getGroup().equals("same_symbols");


    public static Predicate<WinCombinationDTO> isSameSymbolsHorizontally =
            (winCombinationDTO) -> winCombinationDTO.getWhen().equals("linear_symbols") &&
                    winCombinationDTO.getGroup().equals("horizontally_linear_symbols");


    public static Predicate<WinCombinationDTO> isVerticallyLinearSymbols =
            (winCombinationDTO) ->
                    winCombinationDTO.getWhen().equals("linear_symbols") &&
                            winCombinationDTO.getGroup().equals("vertically_linear_symbols");


    public static Predicate<String> is5xBonus = bonus -> bonus.equals("5x");
    public static Function<Double, Double> function5xBonus = s -> {
        if (s == 0) {
            return 0.0;
        } else {
            return s * 5;
        }
    };


    public static Predicate<String> is10xBonus = bonus -> bonus.equals("10x");
    public static Function<Double, Double> function10xBonus = s -> {
        if (s == 0) {
            return 0.0;
        } else {
            return s * 10;
        }
    };


    public static Predicate<String> isPlus1000 = bonus -> bonus.equals("+1000");
    public static Function<Double, Double> functionPlus1000Bonus = s -> s + 1000;

    public static Predicate<String> isPlus500 = bonus -> bonus.equals("+500");
    public static Function<Double, Double> functionPlus500Bonus = s -> s + 500;

    public static final List<Map<Predicate<String>, Function<Double, Double>>> bonusList;

    static {
        bonusList = new ArrayList<>();

        bonusList.add(Map.of(is5xBonus, function5xBonus));
        bonusList.add(Map.of(is10xBonus, function10xBonus));
        bonusList.add(Map.of(isPlus1000, functionPlus1000Bonus));
        bonusList.add(Map.of(isPlus500, functionPlus500Bonus));
    }

}

