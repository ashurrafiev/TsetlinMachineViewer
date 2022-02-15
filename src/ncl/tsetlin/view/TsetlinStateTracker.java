package ncl.tsetlin.view;

import java.io.PrintWriter;
import java.util.HashMap;

import ncl.tsetlin.TsetlinMachine.Polarity;
import ncl.tsetlin.TsetlinOptions;

public interface TsetlinStateTracker {

	public TsetlinOptions getOptions();

	public int getTAState(int cls, int clause, Polarity polarity, int feature);
	public int countTAPerClause();
	public int getRawTAState(int cls, int clause, int ta);
	
	public int getEpoch();
	public int getStateIndex();
	public String getStateTitle();
	
	public void reset();
	public void nextState();
	public void prevState();
	public void nextEpoch();
	public void prevEpoch();
	
	public double evaluateTrain();
	public double evaluateTest();
	public void printStatusHeader(PrintWriter out);
	public void printStatus(PrintWriter out);
	
	public boolean includeLiteral(int state);
	public float getIncludeLevel(int state);
	
	public TsetlinStateTracker setConfigValues(HashMap<String, String> values);
	
}
