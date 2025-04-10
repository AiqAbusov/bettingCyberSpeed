import service.GenerateScratcherService;
import service.ScratcherService;
import service.impl.GenerateScratcherServiceImpl;
import service.impl.ScratchServiceImpl;

import java.io.IOException;
import java.util.HashMap;


public class BettingApp {
    public static void main(String[] args) throws IOException {
        HashMap<String, String> parsedArgs = parseArgs(args);

        String betAmountStr = parsedArgs.get("betting-amount");

        GenerateScratcherService generator = new GenerateScratcherServiceImpl();

        String[][] matrix = generator.generateScratcher();

        ScratcherService scratcher = new ScratchServiceImpl();

        scratcher.scratch(matrix, Double.valueOf(betAmountStr));
    }

    private static HashMap<String, String> parseArgs(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].startsWith("--")) {
                map.put(args[i].substring(2), args[i + 1]);
            }
        }
        return map;
    }
}
//
