package ncl.tsetlin.view;

import ncl.tsetlin.TsetlinMachine.Polarity;
import ncl.tsetlin.MultiClassTsetlinMachine;
import ncl.tsetlin.TsetlinData;
import ncl.tsetlin.TsetlinMachine;
import ncl.tsetlin.TsetlinOptions;

public class TsetlinLiveTracker implements TsetlinStateTracker {

	public final TsetlinData data;
	public final MultiClassTsetlinMachine mcTsetlinMachine;
	
	public int epoch = 0;
	public int nextExample = 0;

	public TsetlinLiveTracker(TsetlinData data) {
		this.data = data;
		this.mcTsetlinMachine = new MultiClassTsetlinMachine(data.opt);
		reset();
	}

	@Override
	public TsetlinOptions getOptions() {
		return data.opt;
	}

	@Override
	public int getState(int cls, int clause, Polarity polarity, int feature) {
		return mcTsetlinMachine.tsetlinMachines[cls].getState(clause, polarity, feature);
	}

	@Override
	public int getEpoch() {
		return epoch;
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
	public boolean includeLiteral(int state) {
		return TsetlinMachine.includeLiteral(state);
	}

	@Override
	public float getIncludeLevel(int state) {
		return (float) TsetlinMachine.getIncludeLevel(data.opt, state);
	}

}
