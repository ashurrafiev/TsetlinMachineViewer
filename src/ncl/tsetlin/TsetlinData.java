package ncl.tsetlin;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import ncl.tsetlin.csv.DataLogger;

public class TsetlinData {

	public final TsetlinOptions opt;
	public DataLogger logger = null;
	
	public boolean[][] trainX;
	public int[] trainy;

	public boolean[][] testX;
	public int[] testy;

	public int countTrain;
	public int countTest;

	public TsetlinData(int countTrain, int countTest, TsetlinOptions opt) {
		this.opt = opt;
		this.countTrain = countTrain;
		this.countTest = countTest;
		trainX = new boolean[countTrain][opt.features];
		trainy = new int[countTrain];
		testX = new boolean[countTest][opt.features];
		testy = new int[countTest];
	}

	public static void readFile(String path, int num, int features, boolean[][] X, int[] y) {
		try {
			Scanner fp = new Scanner(new File(path));
			for(int i=0; i<num; i++) {
				for(int j=0; j<features; j++) {
					X[i][j] = fp.nextInt()!=0;
				}
				y[i] = fp.nextInt();
			}
		} catch(IOException e) {
			System.err.printf("Cannot read %s\n", path);
			System.exit(1);
		}
	}

	public TsetlinData readFiles(String train, String test) {
		readFile(train, this.countTrain, this.opt.features, this.trainX, this.trainy);
		readFile(test, this.countTest, this.opt.features, this.testX, this.testy);
		return this;
	}

	public TsetlinData readFileCombined(String path) {
		int total = this.countTrain + this.countTest;
		boolean[][] allX = new boolean[total][opt.features];
		int[] ally = new int[total];
		readFile(path, total, opt.features, allX, ally);
		
		Random rand = new Random();
		int countTrain = this.countTrain;
		int countTest = this.countTest;
		
		int i=0;
		while(countTrain>0 || countTest>0) {
			if(rand.nextInt(countTrain+countTest)<countTrain) {
				countTrain--;
				for(int j=0; j<opt.features; j++)
					this.trainX[countTrain][j] = allX[i][j];
				this.trainy[countTrain] = ally[i];
			}
			else {
				countTest--;
				for(int j=0; j<opt.features; j++)
					this.testX[countTest][j] = allX[i][j];
				this.testy[countTest] = ally[i];
			}
			i++;
		}
		return this;
	}
	
}
