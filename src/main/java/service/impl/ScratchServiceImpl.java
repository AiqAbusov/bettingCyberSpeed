package service.impl;

import cache.GameConfigurationContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.config.*;
import dto.response.ScratcherResponseDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import service.ScratcherService;
import utility.ScratchUtility;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Data
@Slf4j
public class ScratchServiceImpl implements ScratcherService {
    private GamingConfigurationDTO gamingConfigurationDTO;

    private static final int HORIZONTAL_KEY = 10;
    private static final int VERTICAL_KEY = 11;

    private HashMap<String, Double> symbolMultiplierMap = new HashMap<>();
    private HashMap<String, Double> symbolRewardMultiplier = new HashMap<>();
    private HashMap<Integer, Double> winningPatternMultiplier = new HashMap<>();
    private HashMap<Integer, String> winningPatternNames = new HashMap<>();
    private HashSet<String> bonuses = new HashSet<>();
    private String appliedBonus = "NONE";

    private HashMap<String, List<String>> appliedPatterns = new HashMap<>();

    @Override
    public String scratch(String[][] scratcher, Double betAmount) {
        log.info("Starting the process for checking same symbol 3 times win combination...");
        loadPatterns();

        HashMap<String, Integer> counterMap = new HashMap<>();
        log.debug("Initialized counterMap: {}", counterMap);

        checkHorizontalPattern(scratcher);
        checkVerticalPattern(scratcher);

        countSymbols(scratcher, counterMap);
        log.debug("Symbol counts after scanning the scratcher: {}", counterMap);

        Double result = 0.0;
        HashSet<String> bonusSet = new HashSet<>();

        generateMultiplerForSymbols(counterMap, bonusSet); // Generate multipliers for symbols
        log.debug("Symbol multipliers after processing: {}", symbolMultiplierMap);

        result = applyMultipliers(betAmount, result); // Apply the multipliers to the result
        log.debug("Result after applying multipliers: {}", result);

        result = applyBonuses(result, bonusSet); // Apply the bonus multipliers if any
        log.debug("Result after applying bonuses: {}", result);

        log.info("Final calculated result: {}", result);

        System.err.println(appliedPatterns);
        return generateResponse(result, scratcher);
    }

    public void sameSymbolThreeTimes(String[][] scratcher, Double betAmount) {
        log.info("Starting the process for checking same symbol 3 times win combination...");
        loadPatterns();

        HashMap<String, Integer> counterMap = new HashMap<>();
        log.debug("Initialized counterMap: {}", counterMap);

        checkHorizontalPattern(scratcher);
        checkVerticalPattern(scratcher);

        countSymbols(scratcher, counterMap);
        log.debug("Symbol counts after scanning the scratcher: {}", counterMap);

        Double result = 0.0;
        HashSet<String> bonusSet = new HashSet<>();

        generateMultiplerForSymbols(counterMap, bonusSet); // Generate multipliers for symbols
        log.debug("Symbol multipliers after processing: {}", symbolMultiplierMap);

        result = applyMultipliers(betAmount, result); // Apply the multipliers to the result
        log.debug("Result after applying multipliers: {}", result);

        result = applyBonuses(result, bonusSet); // Apply the bonus multipliers if any
        log.debug("Result after applying bonuses: {}", result);

        log.info("Final calculated result: {}", result);
    }

    private Double applyMultipliers(Double betAmount, Double result) {
        log.debug("Applying multipliers...");
        for (Map.Entry<String, Double> s : symbolMultiplierMap.entrySet()) {
            String symbol = s.getKey();
            Double multiplier = s.getValue();
            log.debug("Processing symbol: {} with multiplier: {}", symbol, multiplier);

            Double symbolValue = symbolRewardMultiplier.get(symbol);
            log.debug("Reward multiplier for {}: {}", symbol, symbolValue);

            betAmount = betAmount * symbolValue * multiplier;
            log.debug("Updated bet amount for {}: {}", symbol, betAmount);

            result += betAmount;
            betAmount = (betAmount / symbolValue) / multiplier; // Reset betAmount for next calculation
        }
        return result;
    }

    private Double applyBonuses(Double result, HashSet<String> bonusSet) {
        log.debug("Applying bonuses...");
        for (String s : bonusSet) {
            log.debug("Checking bonus for symbol: {}", s);
            Optional<Map.Entry<Predicate<String>, Function<Double, Double>>> map = ScratchUtility.bonusList
                    .stream()
                    .flatMap(t -> t.entrySet().stream())
                    .filter(t -> t.getKey().test(s))
                    .findFirst();

            result = map.get().getValue().apply(result);

            appliedBonus = s;
            log.debug("Applied bonus for symbol {}. Updated result: {}", s, result);
        }
        return result;
    }

    private void generateMultiplerForSymbols(HashMap<String, Integer> counterMap, HashSet<String> bonusSet) {
        log.debug("Generating multipliers for symbols...");
        for (Map.Entry<String, Integer> s : counterMap.entrySet()) {
            Integer currentCount = s.getValue();
            log.debug("Processing symbol: {} with count: {}", s.getKey(), currentCount);

            if (bonuses.contains(s.getKey())) {
                bonusSet.add(s.getKey());
                log.debug("Symbol {} is a bonus. Added to bonusSet.", s.getKey());
            }

            Double rewardMultiplier = winningPatternMultiplier.get(currentCount);

            String patternName = winningPatternNames.get(currentCount);


            if (rewardMultiplier != null) {
                log.debug("Found rewardMultiplier for {}: {}", s.getKey(), rewardMultiplier);
                if (symbolMultiplierMap.get(s.getKey()) != null) {
                    Double currentMultiplier = symbolMultiplierMap.get(s.getKey());
                    symbolMultiplierMap.put(s.getKey(), rewardMultiplier * currentMultiplier);
                    log.debug("Updated multiplier for symbol {}: {}", s.getKey(), symbolMultiplierMap.get(s.getKey()));
                } else {
                    symbolMultiplierMap.put(s.getKey(), rewardMultiplier);
                    log.debug("Set initial multiplier for symbol {}: {}", s.getKey(), rewardMultiplier);
                }

                if (appliedPatterns.get(s.getKey()) == null) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(patternName);
                    appliedPatterns.put(s.getKey(), list);
                } else {
                    appliedPatterns.put(s.getKey(), appliedPatterns.get(s.getKey())).add(patternName);
                }
            }
        }
    }

    private static void countSymbols(String[][] scratcher, HashMap<String, Integer> counterMap) {
        log.debug("Counting symbols in the scratcher...");
        for (int i = 0; i < scratcher.length; i++) {
            for (int j = 0; j < scratcher[i].length; j++) {
                String current = scratcher[i][j];
                counterMap.merge(current, 1, Integer::sum);
            }
        }
    }

    private void checkVerticalPattern(String[][] scratcher) {
        log.debug("Checking for vertical winning patterns...");
        for (int j = 0; j < scratcher[0].length; j++) {
            int counter = 1;
            for (int i = 1; i < scratcher.length; i++) {
                if (Objects.equals(scratcher[i][j], scratcher[i - 1][j])) {
                    counter++;
                } else {
                    counter = 1;
                }

                if (counter >= 3) {
                    symbolMultiplierMap.putIfAbsent(scratcher[i][j], 1.0);
                    symbolMultiplierMap.put(scratcher[i][j], symbolMultiplierMap.get(scratcher[i][j])
                            * winningPatternMultiplier.get(VERTICAL_KEY));
                    log.debug("Vertical pattern matched for symbol {}. Updated multiplier: {}", scratcher[i][j], symbolMultiplierMap.get(scratcher[i][j]));

                    if (appliedPatterns.get(scratcher[i][j]) == null) {
                        ArrayList<String> list = new ArrayList<>();

                        list.add(winningPatternNames.get(VERTICAL_KEY));

                        appliedPatterns.put(scratcher[i][j], list);
                    } else {
                        appliedPatterns.get(scratcher[i][j]).add((winningPatternNames.get(VERTICAL_KEY)));
                    }
                }
            }
        }
    }

    private void checkHorizontalPattern(String[][] scratcher) {
        log.debug("Checking for horizontal winning patterns...");
        for (int i = 0; i < scratcher.length; i++) {
            int counter = 1;
            for (int j = 1; j < scratcher[i].length; j++) {
                if (Objects.equals(scratcher[i][j], scratcher[i][j - 1])) {
                    counter++;
                } else {
                    counter = 1;
                }

                if (counter >= 3) {
                    symbolMultiplierMap.putIfAbsent(scratcher[i][j], 1.0);
                    symbolMultiplierMap.put(scratcher[i][j], symbolMultiplierMap.get(scratcher[i][j])
                            * winningPatternMultiplier.get(HORIZONTAL_KEY));

                    if (appliedPatterns.get(scratcher[i][j]) == null) {

                        ArrayList<String> list = new ArrayList<>();
                        list.add(winningPatternNames.get(HORIZONTAL_KEY));

                        appliedPatterns.put(scratcher[i][j], list);

                    } else {
                        appliedPatterns.get(scratcher[i][j]).add((winningPatternNames.get(HORIZONTAL_KEY)));
                    }

                    log.debug("Horizontal pattern matched for symbol {}. Updated multiplier: {}", scratcher[i][j], symbolMultiplierMap.get(scratcher[i][j]));
                }
            }
        }
    }


    private String generateResponse(Double result, String[][] matrix) {
        ScratcherResponseDTO responseDTO = new ScratcherResponseDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        String response = "ERROR";

        responseDTO.setReward(result);
        responseDTO.setAppliedWinningCombinations(appliedPatterns);
        responseDTO.setMatrix(matrix);
        responseDTO.setAppliedBonusSymbol(appliedBonus);

        try {
            response = objectMapper.writeValueAsString(responseDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can not Parse Response");
        }
        return response;
    }

    private void loadPatterns() {
        log.info("Loading game configuration and probabilities...");
        gamingConfigurationDTO = GameConfigurationContext.getGamingConfiguration();
        SymbolsDTO symbolsDTO = gamingConfigurationDTO.getSymbols();

        for (Map.Entry<String, SymbolsDTO.Symbol> entries : symbolsDTO.getSymbols().entrySet()) {
            if (entries.getValue().getType().equals("bonus"))
                bonuses.add(entries.getKey());

            if (entries.getValue().getExtra() != null) {
                symbolRewardMultiplier.put(entries.getKey(), entries.getValue().getExtra());
            } else {
                symbolRewardMultiplier.put(entries.getKey(), entries.getValue().getRewardMultiplier());
            }
        }
        log.debug("Loaded symbol reward multipliers: {}", symbolRewardMultiplier);

        for (Map.Entry<String, WinCombinationDTO> entries : gamingConfigurationDTO
                .getWinCombinations().getWinCombinations().entrySet()) {
            WinCombinationDTO winCombinationDTO = entries.getValue();

            if (ScratchUtility.isSameSymbolsHorizontally.test(winCombinationDTO)) {
                winningPatternMultiplier.put(HORIZONTAL_KEY, winCombinationDTO.getRewardMultiplier());
                winningPatternNames.put(HORIZONTAL_KEY, entries.getKey());
            } else if (ScratchUtility.isVerticallyLinearSymbols.test(winCombinationDTO)) {
                winningPatternMultiplier.put(VERTICAL_KEY, winCombinationDTO.getRewardMultiplier());
                winningPatternNames.put(VERTICAL_KEY, entries.getKey());
            } else if (ScratchUtility.isSameSymbol.test(winCombinationDTO)) {
                Integer count = winCombinationDTO.getCount();
                Double multiplier = winCombinationDTO.getRewardMultiplier();

                winningPatternMultiplier.put(count, multiplier);
                winningPatternNames.put(count, entries.getKey());
            }
        }
        log.debug("Loaded winning pattern multipliers: {}", winningPatternMultiplier);
    }
}
