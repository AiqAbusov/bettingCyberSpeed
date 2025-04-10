package config;

import cache.GameConfigurationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.config.GamingConfigurationDTO;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GameConfig {

    public GamingConfigurationDTO loadConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File configJson = new File(Objects.requireNonNull(this
                        .getClass().getClassLoader()
                        .getResource("config.json"))
                .getFile());
        GamingConfigurationDTO gamingConfigurationDTO = objectMapper.readValue(configJson, GamingConfigurationDTO.class);

        GameConfigurationContext.setGamingConfiguration(gamingConfigurationDTO);

        return gamingConfigurationDTO;
    }
}
