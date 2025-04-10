import cache.GameConfigurationContext;
import service.GenerateScratcherService;
import service.ScratcherService;
import service.impl.GenerateScratcherServiceImpl;
import service.impl.ScratchServiceImpl;

import java.io.IOException;

public class BettingApp {
    public static void main(String[] args) throws IOException {
        GenerateScratcherService generateScratcherService = new GenerateScratcherServiceImpl();

        String[][] sc = generateScratcherService.generateScratcher();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(sc[i][j]);
                System.out.print("    ");
            }
            System.out.println();
        }

        ScratchServiceImpl service = new ScratchServiceImpl();
        System.out.println( service.scratch(sc, 100.0));

    }
}
//
