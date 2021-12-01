package ncl.tsetlin;

public class TsetlinOptions {

	public int clauses;
	public int numStates = 100;
	public double s = 3.9;
	public int threshold = 15;
	public int features, classes;
	public int numExamplesTrain, numExamplesTest;

	public TsetlinOptions setClauses(int clauses) {
		this.clauses = clauses;
		return this;
	}

	public TsetlinOptions setNumStates(int numStates) {
		this.numStates = numStates;
		return this;
	}
	
	public TsetlinOptions setS(double s) {
		this.s = s;
		return this;
	}

	public TsetlinOptions setThreshold(int threshold) {
		this.threshold = threshold;
		return this;
	}
	
	public TsetlinOptions setFeatures(int features) {
		this.features = features;
		return this;
	}

	public TsetlinOptions setClasses(int classes) {
		this.classes = classes;
		return this;
	}
	
	public TsetlinOptions setNumExamples(int train, int test) {
		this.numExamplesTrain = train;
		this.numExamplesTest = test;
		return this;
	}

}
