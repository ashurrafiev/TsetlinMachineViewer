package ncl.tsetlin;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import ncl.tsetlin.view.TsetlinLiveTracker;
import ncl.tsetlin.view.TsetlinStateViewer;

public class TrainLiveDemo {

	public static final TsetlinOptions opt = new TsetlinOptions()
			.setFeatures(16)
			.setClauses(10)
			.setNumStates(100)
			.setS(3.0)
			.setThreshold(10)
			.setClasses(3)
			.setNumExamples(120, 30);
	
	/*public static final TsetlinOptions opt = new TsetlinOptions()
			.setFeatures(12)
			.setClauses(15)
			.setNumStates(100)
			.setS(3.9)
			.setThreshold(10)
			.setClasses(2)
			.setNumExamples(5000, 5000);*/
	
	public static void readFile(String path, int num, boolean[][] X, int[] y) {
		try {
			Scanner fp = new Scanner(new File(path));
			for(int i=0; i<num; i++) {
				for(int j=0; j<opt.features; j++) {
					X[i][j] = fp.nextInt()!=0;
				}
				y[i] = fp.nextInt();
			}
		} catch(IOException e) {
			System.err.printf("Cannot read %s\n", path);
			System.exit(1);
		}
	}
	
	public static TsetlinData readFileCombined(String path) {
		int total = opt.numExamplesTrain+opt.numExamplesTest;
		boolean[][] allX = new boolean[total][opt.features];
		int[] ally = new int[total];
		readFile(path, total, allX, ally);
		
		Random rand = new Random();
		int countTrain = opt.numExamplesTrain;
		int countTest = opt.numExamplesTest;
		
		TsetlinData data = new TsetlinData(opt);
		int i=0;
		while(countTrain>0 || countTest>0) {
			if(rand.nextInt(countTrain+countTest)<countTrain) {
				countTrain--;
				for(int j=0; j<opt.features; j++)
					data.trainX[countTrain][j] = allX[i][j];
				data.trainy[countTrain] = ally[i];
			}
			else {
				countTest--;
				for(int j=0; j<opt.features; j++)
					data.testX[countTest][j] = allX[i][j];
				data.testy[countTest] = ally[i];
			}
			i++;
		}
		return data;
	}
	
	public static TsetlinData readFiles(String train, String test) {
		TsetlinData data = new TsetlinData(opt);
		readFile(train, opt.numExamplesTrain, data.trainX, data.trainy);
		readFile(test, opt.numExamplesTest, data.testX, data.testy);
		return data;
	}

	public static TsetlinData readFiles() {
		//return readFiles("NoisyXORTrainingData.txt", "NoisyXORTestData.txt");
		//return readFiles("xor5_train.txt", "xor5_test.txt");
		//return readFiles("BinaryIrisTrain.txt", "BinaryIrisTest.txt");
		//return readFiles("IrisJMTrain.txt", "IrisJMTest.txt");
		return readFileCombined("BinaryIrisData.txt");
	}

	public static void main(String[] args) {
		TsetlinData data = TrainLiveDemo.readFiles();
		TsetlinStateViewer.startViewer(new TsetlinLiveTracker(data));

		/*int epochs = args.length>0 ? Integer.parseInt(args[0]) : 30; // 30
		opt.s = args.length>1 ? Double.parseDouble(args[1]) : 3.9; // 3.65
		int maxi = args.length>2 ? Integer.parseInt(args[2]) : 10;

		TsetlinData data = readFiles();

		MultiClassTsetlinMachine mcTsetlinMachine = new MultiClassTsetlinMachine(opt);

		double timeSum = 0.0;
		double accSum = 0.0;
		for (int i = 0; i < maxi; i++) {
			mcTsetlinMachine.initialize();
			long startTotal = System.currentTimeMillis();
			mcTsetlinMachine.fit(data.trainX, data.trainy, opt.numExamplesTrain, epochs);
			long endTotal = System.currentTimeMillis();
			double time_used = ((double) (endTotal - startTotal)) / 1000.0;

			double acc = mcTsetlinMachine.evaluate(data.testX, data.testy, opt.numExamplesTest);
			accSum += acc;
			timeSum += time_used;

			System.out.printf("\rAttepmt %d/%d    Time: %f    ", i+1, maxi, time_used);
			System.out.printf("Accuracy: %f", acc); // average/(i+1));
			System.out.flush();
		}
		System.out.printf("\rAttepmts: %d   Mean time: %f   Mean accuracy: %f\n", maxi, timeSum/maxi, accSum/maxi);*/
	}

}
