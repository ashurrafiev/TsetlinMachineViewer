package ncl.tsetlin.view;

import ncl.tsetlin.TsetlinMachine.Polarity;

import static ncl.tsetlin.ConfigLoader.getInt;

import java.io.PrintWriter;
import java.util.HashMap;

import ncl.tsetlin.MultiClassTsetlinMachine;
import ncl.tsetlin.TsetlinData;
import ncl.tsetlin.TsetlinMachine;
import ncl.tsetlin.TsetlinOptions;

public class TsetlinLiveTracker implements TsetlinStateTracker {

	public final TsetlinData data;
	public final MultiClassTsetlinMachine mcTsetlinMachine;
	
	public int optRemapPeriod = 0;
	
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
	public int getTAState(int cls, int clause, Polarity polarity, int feature) {
		return mcTsetlinMachine.tsetlinMachines[cls].getState(clause, polarity, feature);
	}
	
	@Override
	public int countTAPerClause() {
		return mcTsetlinMachine.tsetlinMachines[0].literals;
	}
	
	@Override
	public int getRawTAState(int cls, int clause, int ta) {
		return mcTsetlinMachine.tsetlinMachines[cls].clauses[clause].ta[ta];
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
		if(data.logger!=null)
			data.logger.reset(this);
	}
	
	@Override
	public void nextState() {
		if(data.logger!=null)
			data.logger.log(this);
		mcTsetlinMachine.update(data.trainX[nextExample], data.trainy[nextExample]);
		
		nextExample++;
		if(nextExample>=data.countTrain) {
			epoch++;
			if(optRemapPeriod>0 && (epoch%optRemapPeriod)==0)
				mcTsetlinMachine.remapTAStates();
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
		out.printf("acctrain\tacctest\t");
		for(int i=0; i<data.opt.classes; i++) {
			out.printf("inc%d\tflips%<d\ttype1-c%<d\ttype2-c%<d\t", i);
		}
	}
	
	@Override
	public void printStatus(PrintWriter out) {
		out.printf("%.3f\t", evaluateTrain());
		out.printf("%.3f\t", evaluateTest());
		for(int i=0; i<data.opt.classes; i++) {
			TsetlinMachine tm = mcTsetlinMachine.tsetlinMachines[i];
			out.printf("%d\t", tm.countIncluded());
			out.printf("%d\t", tm.flips);
			tm.flips = 0;
			out.printf("%d\t", tm.countType1);
			tm.countType1 = 0;
			out.printf("%d\t", tm.countType2);
			tm.countType2 = 0;
		}
	}
	
	@Override
	public boolean includeLiteral(int state) {
		return TsetlinMachine.includeLiteral(state);
	}

	@Override
	public float getIncludeLevel(int state) {
		return (float) TsetlinMachine.getIncludeLevel(data.opt, state);
	}

	@Override
	public TsetlinStateTracker setConfigValues(HashMap<String, String> values) {
		this.optRemapPeriod = getInt(values.get("logFrequency"), 0, Integer.MAX_VALUE, 0);
		return this;
	}

}
