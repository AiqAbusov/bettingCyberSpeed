package service.impl;

import cache.GameConfigurationContext;
import dto.config.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import service.GenerateScratcherService;

import java.util.*;

@Data
@Slf4j
public class GenerateScratcherServiceImpl implements GenerateScratcherService {
    private static final Map<String, StandardSymbolsDTO> mapOfProbabilitiesStandardSymbols = new HashMap<>();
    private static final Random random = new Random();

    private GamingConfigurationDTO gamingConfigurationDTO;

    /**
     * It can easily be adjusted for any size 4x4 or 3x4 or 10x10
     * for sake of example i use 4x4
     *
     * @return String[][]
     */

    @Override
    public String[][] generateScratcher() {
        loadProbabilities();

        log.info("Fetching value from context: {}", gamingConfigurationDTO);

        int row = gamingConfigurationDTO.getRows();
        int column = gamingConfigurationDTO.getColumns();
        log.info("Generating scratcher with {} rows and {} columns.", row, column);

        String[][] scratcher = new String[row][column];

        // Limit amount of bonus for each scratcher
        // so we can limit amount of bonus dynamically
        int bonusLimit = random.nextInt(gamingConfigurationDTO
                .getProbabilities()
                .getBonusSymbols()
                .getLimit());

        log.info("Bonus limit set to: {}", bonusLimit);

        Set<String> indexesOfBonus = new HashSet<>();
        addIndexOfBonuses(row, column, bonusLimit, indexesOfBonus);

        log.info("Indexes of bonus symbols: {}", indexesOfBonus);

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (indexesOfBonus.contains(i + "" + j)) {
                    scratcher[i][j] = generateSymbol();
                    log.debug("Position ({}, {}) assigned a bonus symbol: {}", i, j, scratcher[i][j]);
                } else {
                    String randomSymbol = generateSymbol(i, j);
                    scratcher[i][j] = randomSymbol;
                    log.debug("Position ({}, {}) assigned symbol: {}", i, j, scratcher[i][j]);
                }
            }
        }

        log.info("Scratcher generation complete.");
        return scratcher;
    }

    public static void addIndexOfBonuses(int row, int column, int bonusLimit, Set<String> indexesOfBonus) {
        log.debug("Adding bonus symbols to scratcher grid. Row: {}, Column: {}, Bonus Limit: {}", row, column, bonusLimit);
        while (bonusLimit > 0) {
            int rowOfBonus = random.nextInt(row);
            int columnOfBonus = random.nextInt(column);

            String index = rowOfBonus + "" + columnOfBonus;
            indexesOfBonus.add(index);
            log.debug("Added bonus symbol at index: {}", index);

            bonusLimit--;
        }
    }

    public String generateSymbol() {
        log.debug("Generating bonus symbol.");
        return generateSymbolFromMap(gamingConfigurationDTO.getProbabilities().getBonusSymbols().getSymbols(), 97);
    }

    public String generateSymbol(int row, int column) {
        StandardSymbolsDTO standardSymbol = mapOfProbabilitiesStandardSymbols.get(row + "" + column);
        log.debug("Generating standard symbol for position ({}, {}).", row, column);
        return generateSymbolFromMap(standardSymbol.getSymbols(), 97);
    }

    public String generateSymbolFromMap(Map<String, Integer> symbolMap, int total) {
        log.debug("Generating symbol from map with total: {}", total);

        TreeMap<String, Integer> mapOfPercentage = new TreeMap<>();

        int sumOfWeights = 0;
        for (Map.Entry<String, Integer> entries : symbolMap.entrySet()) {
            sumOfWeights += entries.getValue();
        }

        log.debug("Sum of symbol weights: {}", sumOfWeights);

        for (Map.Entry<String, Integer> entries : symbolMap.entrySet()) {
            int percentage = (entries.getValue() * 100) / sumOfWeights;
            log.debug("Symbol: {} | Weight: {} | Percentage: {}", entries.getKey(), entries.getValue(), percentage);
            mapOfPercentage.put(entries.getKey(), percentage);
        }

        Random random = new Random();
        int randomInt = random.nextInt(total);
        log.debug("Generated random value: {}", randomInt);

        int checker = 0;
        for (Map.Entry<String, Integer> entries : mapOfPercentage.entrySet()) {
            checker += entries.getValue();
            if (randomInt < checker) {
                log.debug("Selected symbol: {}", entries.getKey());
                return entries.getKey();
            }
        }

        log.error("No symbol selected - returning empty string.");
        return "";
    }

    public void loadProbabilities() {
        log.info("Loading probabilities from the configuration context.");

        gamingConfigurationDTO = GameConfigurationContext.getGamingConfiguration();
        log.debug("Loaded gaming configuration: {}", gamingConfigurationDTO);

        ProbabilitiesDTO probabilities = gamingConfigurationDTO.getProbabilities();

        StandardSymbolsDTO[] standardSymbols = probabilities.getStandardSymbols();

        log.debug("Loading standard symbols...");

        for (StandardSymbolsDTO symbolsDTO : standardSymbols) {
            mapOfProbabilitiesStandardSymbols.put(symbolsDTO.getRow() + "" + symbolsDTO.getColumn(), symbolsDTO);
            log.debug("Loaded standard symbol for position: {}{}", symbolsDTO.getRow(), symbolsDTO.getColumn());
        }
    }
}
