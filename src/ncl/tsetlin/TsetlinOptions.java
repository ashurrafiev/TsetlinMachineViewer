package ncl.tsetlin;

import static ncl.tsetlin.ConfigLoader.*;

import java.util.HashMap;

public class TsetlinOptions {

	public int clauses;
	public int numStates = 100;
	public double s = 3.9;
	public int threshold = 15;
	public int features;
	public int classes = 1;

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
	
	public TsetlinOptions setConfigValues(HashMap<String, String> values) {
		this.clauses = getInt(values.get("clauses"), 1, Integer.MAX_VALUE, 2);
		this.numStates = getInt(values.get("numStates"), 1, Integer.MAX_VALUE, 100);
		this.s = getDouble(values.get("s"), 1.0, 1000.0, 3.9);
		this.threshold = getInt(values.get("threshold"), 1, Integer.MAX_VALUE, 15);
		this.features = getInt(values.get("features"), 1, Integer.MAX_VALUE, 0);
		this.classes = getInt(values.get("classes"), 1, Integer.MAX_VALUE, 1);
		return this;
	}

}
