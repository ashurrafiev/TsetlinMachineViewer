package ncl.tsetlin.view;

import ncl.tsetlin.TsetlinMachine.Polarity;
import ncl.tsetlin.TsetlinOptions;

public interface TsetlinStateTracker {

	public TsetlinOptions getOptions();
	public int getState(int cls, int clause, Polarity polarity, int feature);
	public int getEpoch();
	public String getStateTitle();
	
	public void reset();
	public void nextState();
	public void prevState();
	public void nextEpoch();
	public void prevEpoch();
	
	public double evaluateTrain();
	public double evaluateTest();
	
	public boolean includeLiteral(int state);
	public float getIncludeLevel(int state);
	
}
