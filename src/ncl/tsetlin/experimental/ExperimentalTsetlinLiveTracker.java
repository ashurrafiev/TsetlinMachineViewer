package ncl.tsetlin.experimental;

import java.io.PrintWriter;
import java.util.HashMap;

import ncl.tsetlin.TsetlinData;
import ncl.tsetlin.TsetlinMachine.Polarity;
import ncl.tsetlin.TsetlinOptions;
import ncl.tsetlin.view.TsetlinStateTracker;

public class ExperimentalTsetlinLiveTracker implements TsetlinStateTracker {

	public final TsetlinData data;
	public final ExperimentalMultiClassTsetlinMachine mcTsetlinMachine;
	
	public int epoch = 0;
	public int nextExample = 0;

	public ExperimentalTsetlinLiveTracker(TsetlinData data) {
		this.data = data;
		this.mcTsetlinMachine = new ExperimentalMultiClassTsetlinMachine(data.opt);
		reset();
	}

	@Override
	public TsetlinOptions getOptions() {
		return data.opt;
	}

	@Override
	public int getTAState(int cls, int clause, Polarity polarity, int feature) {
		return mcTsetlinMachine.tsetlinMachines[cls].getState(clause, polarity, feature);
	}

	@Override
	public int countTAPerClause() {
		return mcTsetlinMachine.tsetlinMachines[0].literals;
	}
	
	@Override
	public int getRawTAState(int cls, int clause, int ta) {
		return mcTsetlinMachine.tsetlinMachines[cls].clauses[clause].ta_w[ta];
	}
	
	@Override
	public int getEpoch() {
		return epoch;
	}
	
	@Override
	public int getStateIndex() {
		return nextExample;
	}
	
	@Override
	public String getStateTitle() {
		return String.format("input: %d/%d", nextExample, data.countTrain);
	}
	
	@Override
	public void reset() {
		mcTsetlinMachine.initialize();
		epoch = 0;
		nextExample = 0;
	}
	
	@Override
	public void nextState() {
		mcTsetlinMachine.update(data.trainX[nextExample], data.trainy[nextExample]);
		
		nextExample++;
		if(nextExample>=data.countTrain) {
			epoch++;
			nextExample = 0;
		}
	}
	
	@Override
	public void prevState() {
		// not supported
	}
	
	@Override
	public void nextEpoch() {
		do {
			nextState();
		} while(nextExample>0);
	}
	
	@Override
	public void prevEpoch() {
		// not supported
	}

	@Override
	public double evaluateTrain() {
		return mcTsetlinMachine.evaluate(data.trainX, data.trainy, data.countTrain);
	}

	@Override
	public double evaluateTest() {
		return mcTsetlinMachine.evaluate(data.testX, data.testy, data.countTest);
	}

	@Override
	public void printStatusHeader(PrintWriter out) {
	}
	
	@Override
	public void printStatus(PrintWriter out) {
	}

	@Override
	public boolean includeLiteral(int state) {
		return ExperimentalTsetlinMachine.includeLiteral(state);
	}

	@Override
	public float getIncludeLevel(int state) {
		return (float) ExperimentalTsetlinMachine.getIncludeLevel(data.opt, state);
	}

	@Override
	public TsetlinStateTracker setConfigValues(HashMap<String, String> values) {
		return this;
	}
}
