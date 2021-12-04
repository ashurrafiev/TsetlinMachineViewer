package ncl.tsetlin;

import static ncl.tsetlin.ConfigLoader.*;

import java.io.IOException;
import java.util.HashMap;

import ncl.tsetlin.csv.TsetlinCsvView;
import ncl.tsetlin.view.TsetlinLiveTracker;
import ncl.tsetlin.view.TsetlinStateTracker;
import ncl.tsetlin.view.TsetlinStateViewer;

public class TrainLiveDemo {

	public static TsetlinStateTracker loadConfig(String path) {
		HashMap<String, String> values = loadConfigValues(path);
		if (values == null) {
			System.err.printf("Cannot read config file: %s", new Object[] { path });
			System.exit(1);
			return null;
		}
		TsetlinOptions opt = (new TsetlinOptions()).setConfigValues(values);

		String csvFormat = values.get("csvPathFormat");
		if (csvFormat != null) {
			try {
				return (TsetlinStateTracker) new TsetlinCsvView(opt, csvFormat);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
				return null;
			}
		}

		int countTrain = getInt(values.get("countTrain"), 1, Integer.MAX_VALUE, 1);
		int countTest = getInt(values.get("countTest"), 1, Integer.MAX_VALUE, 1);
		TsetlinData data = new TsetlinData(countTrain, countTest, opt);

		String trainData = values.get("trainData");
		String testData = values.get("testData");
		String combinedData = values.get("combinedData");
		if (combinedData != null) {
			data.readFileCombined(combinedData);
		} else {

			data.readFiles(trainData, testData);
		}
		return (TsetlinStateTracker) new TsetlinLiveTracker(data);
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			TsetlinStateViewer.startViewer(loadConfig(args[0]));
			return;
		}
		System.out.println("Usage:\njava -jar tsetlinviewer.jar <config_file>");
	}
	
}
