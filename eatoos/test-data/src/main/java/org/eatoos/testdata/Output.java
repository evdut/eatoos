package org.eatoos.testdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Output {

	private final static String START_WORKING_TIME = "start";
	private final static String STOP_WORKING_TIME = "stop";
	private final static String DAYS_PERIOD = "period";
	private final static String RECIPE = "recipe";
	private final static String MORNING_PROPORTION = "morning";
	private final static String AFTERNOON_PROPORTION = "afternoon";
	private final static String EVENING_PROPORTION = "evening";
	private final static String DAY_CONSUMPTION_PROPORTION = "amount";

	private Map<Integer, Integer> consumptionAmount;
	private InputData inputData;

	public Output(InputData inputData) {
		this.inputData = inputData;
		createConsumptionNumbers();
	}

	@SuppressWarnings("unchecked")
	public void generate(String outputPath) throws IOException {
		LocalTime start = LocalTime.of(inputData.fetch(START_WORKING_TIME, Integer.class), 0);
		LocalTime stop = LocalTime.of(inputData.fetch(STOP_WORKING_TIME, Integer.class), 0);
		Double morningProportion = inputData.fetch(MORNING_PROPORTION, Double.class);
		Double afternoonProportion = inputData.fetch(AFTERNOON_PROPORTION, Double.class);
		Double eveningProportion = inputData.fetch(EVENING_PROPORTION, Double.class);
		Map<String, Double> recipe = (Map<String, Double>) inputData.fetch(RECIPE, Map.class);
		Map<Integer, Integer> helperMap = new HashMap<>();

		recipe.entrySet().forEach(ingridientEntry -> {
			try {
				File file = new File(outputPath + ingridientEntry.getKey() + ".csv");
				file.getParentFile().mkdir();
				file.createNewFile();
				FileWriter fileWriter = new FileWriter(file);

				CSVPrinter csvPrinter = new CSVPrinter(fileWriter,
						CSVFormat.EXCEL.withHeader("Day", "Hour", "Consumed gramms"));
				IntStream.range(0, inputData.fetch(DAYS_PERIOD, Integer.class)).forEach(day -> {
					Integer morningAmount = (int) (consumptionAmount.get(day) * morningProportion);
					Integer afternoonAmount = (int) (consumptionAmount.get(day) * afternoonProportion);
					Integer eveningAmount = (int) (consumptionAmount.get(day) * eveningProportion);
					Iterator<Integer> iterator = IntStream.range(0, (int) Duration.between(start, stop).toHours() + 1)
							.iterator();

					while (iterator.hasNext()) {
						LocalTime currentHour = start.plusHours(iterator.next());

						try {
							if (currentHour.isBefore(LocalTime.of(13, 0))) {
								csvPrinter.printRecord(day, currentHour, countTotalWeightPerHour(currentHour, 12, day,
										helperMap, morningAmount, ingridientEntry.getValue()));
							} else if (currentHour.isAfter(LocalTime.of(16, 0))) {
								csvPrinter.printRecord(day, currentHour, countTotalWeightPerHour(currentHour,
										stop.getHour(), day, helperMap, eveningAmount, ingridientEntry.getValue()));
							} else {
								csvPrinter.printRecord(day, currentHour, countTotalWeightPerHour(currentHour, 16, day,
										helperMap, afternoonAmount, ingridientEntry.getValue()));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				fileWriter.flush();
				fileWriter.close();
				csvPrinter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private Double countTotalWeightPerHour(LocalTime currentHour, Integer lastHour, Integer day,
			Map<Integer, Integer> helperMap, Integer amount, Double ingridientWeight) {
		if (currentHour.getHour() != lastHour) {
			Integer amountPerHour = helperMap.get(currentHour.getHour());
			if (amountPerHour == null) {
				amountPerHour = ThreadLocalRandom.current().nextInt(0, amount);
				helperMap.put(currentHour.getHour(), amountPerHour);
			}
			amount = amount - amountPerHour;
		}
		return amount * ingridientWeight;
	}

	@SuppressWarnings("unchecked")
	private void createConsumptionNumbers() {
		final RandomCollection<int[]> consumptionNumbers = new RandomCollection<int[]>();
		Map<Double, String> data = (Map<Double, String>) inputData.fetch(DAY_CONSUMPTION_PROPORTION, Map.class);
		data.entrySet().forEach(entry -> {
			String[] fromToArray = entry.getValue().split("-");
			consumptionNumbers.add((Double) entry.getKey(), IntStream
					.rangeClosed(Integer.parseInt(fromToArray[0]), Integer.parseInt(fromToArray[1])).toArray());
		});

		Integer days = inputData.fetch(DAYS_PERIOD, Integer.class);
		this.consumptionAmount = new HashMap<Integer, Integer>(days);
		IntStream.range(0, inputData.fetch(DAYS_PERIOD, Integer.class)).forEach(day -> {
			int[] numberArray = consumptionNumbers.next();
			this.consumptionAmount.put(day, numberArray[new Random().nextInt(numberArray.length)]);
		});
	}

}
