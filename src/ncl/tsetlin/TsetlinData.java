package ncl.tsetlin;

public class TsetlinData {

	public final TsetlinOptions opt;
	
	public boolean[][] trainX;
	public int[] trainy;

	public boolean[][] testX;
	public int[] testy;


	public TsetlinData(TsetlinOptions opt) {
		this.opt = opt;
		trainX = new boolean[opt.numExamplesTrain][opt.features];
		trainy = new int[opt.numExamplesTrain];
		testX = new boolean[opt.numExamplesTest][opt.features];
		testy = new int[opt.numExamplesTest];
	}

}
