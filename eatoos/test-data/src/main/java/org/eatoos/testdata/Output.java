package org.eatoos.testdata;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Output {

    private final static String START_WORKING_TIME = "time.working.start";
    private final static String STOP_WORKING_TIME = "time.working.stop";
    private final static String DAYS_PERIOD = "period";
    private final static String RECIPE = "recipe";
    private final static String MORNING_PROPORTION = "proportion.consumption.morning";
    private final static String AFTERNOON_PROPORTION = "proportion.consumption.afternoon";
    private final static String EVENING_PROPORTION = "proportion.consumption.evening";
    private final static String DAY_CONSUMPTION_PROPORTION = "proportion.consumption.numbers";
    
    private RandomCollection<int[]> consumptionNumbers =  new RandomCollection<int[]>();
    private InputData inputData;
    
    public Output(InputData inputData) {
        this.inputData = inputData;
        createConsumptionNumbers();
    }
    
    public void generate(String outputPath) throws IOException {
        LocalTime start = LocalTime.of(inputData.fetch(START_WORKING_TIME, Integer.class), 0);
        LocalTime stop = LocalTime.of(inputData.fetch(STOP_WORKING_TIME, Integer.class), 0);
        Double morningProportion = inputData.fetch(MORNING_PROPORTION, Double.class);
        Double afternoonProportion = inputData.fetch(AFTERNOON_PROPORTION, Double.class);
        Double eveningProportion = inputData.fetch(EVENING_PROPORTION, Double.class);
        Map<String, Double> recipe = (Map<String, Double>)inputData.fetch(EVENING_PROPORTION, Map.class);
        
        recipe.entrySet().forEach(entry-> {
            try {
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath + entry.getKey() + ".csv"));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("Day", "Hour", "Consumed gramms"));
                IntStream.range(0, inputData.fetch(DAYS_PERIOD, Integer.class)).forEach(day -> {
                    IntStream.range(0, (int) Duration.between(start, stop).toHours()).forEach(hour -> {
                        csvPrinter.printRecord(day, start.plusHours(hour), )
                    });
                    ;

                    
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
    
    private void createConsumptionNumbers() {
        Map<Double, String> data = (Map<Double, String>)inputData.fetch(DAY_CONSUMPTION_PROPORTION, Map.class);
        data.entrySet().forEach(entry -> {
            String[] fromToArray = entry.getValue().split("-");
            consumptionNumbers.add(entry.getKey(), IntStream.rangeClosed(Integer.parseInt(fromToArray[0]), Integer.parseInt(fromToArray[1])).toArray());
        });
    }
    
    private int consumptionNumber() {
        int[] numberArray = consumptionNumbers.next();
        return numberArray[new Random().nextInt(numberArray.length)];
    }
}
