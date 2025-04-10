package cache;

import config.GameConfig;
import dto.config.GamingConfigurationDTO;
import exception.ConfigFileRuntimeException;

import java.io.IOException;

/**
 * author : a.abusov
 * <p>
 * Thread Local is used to save value of GamingConfigurationDTO
 * that we get from config.json
 */

public class GameConfigurationContext {

    private static final GameConfig gameConfig = new GameConfig();

    private static final ThreadLocal<GamingConfigurationDTO> gamingConfigThreadLocal = new ThreadLocal<>();

    public static void setGamingConfiguration(GamingConfigurationDTO config) {
        gamingConfigThreadLocal.set(config);
    }

    public static GamingConfigurationDTO getGamingConfiguration() {
        GamingConfigurationDTO gamingConfigurationDTO = gamingConfigThreadLocal.get();

        if (gamingConfigurationDTO == null) {
            try {
                gamingConfigurationDTO = gameConfig.loadConfig();
            } catch (IOException e) {
                throw new ConfigFileRuntimeException("Can not load config.json");
            }
            setGamingConfiguration(gamingConfigurationDTO);
        }

        return gamingConfigurationDTO;
    }

    public static void clear() {
        gamingConfigThreadLocal.remove();
    }
}