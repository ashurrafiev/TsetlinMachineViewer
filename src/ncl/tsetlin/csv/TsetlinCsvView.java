package ncl.tsetlin.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import ncl.tsetlin.TsetlinMachine.Polarity;
import ncl.tsetlin.TsetlinOptions;
import ncl.tsetlin.view.TsetlinStateTracker;
import ncl.tsetlin.view.TsetlinStateViewer;

public class TsetlinCsvView implements TsetlinStateTracker {

	public static final TsetlinOptions demoOpt = new TsetlinOptions()
			.setFeatures(16)
			.setClauses(100)
			.setNumStates(100)
			.setClasses(3);
	
	public class State {
		public int ta[][];
	}
	
	public final TsetlinOptions opt;
	
	public ArrayList<State> epochStates;
	public int maxEpoch;
	
	public int epoch;
	
	public TsetlinCsvView(TsetlinOptions opt, String pathFormat) throws IOException {
		this.opt = opt;
		loadEpochs(pathFormat);
		if(maxEpoch<1)
			throw new IOException("Cannot load CSV data.");
	}
	
	public boolean loadEpoch(String pathFormat, int epoch) {
		File file = new File(String.format(pathFormat, epoch));
		if(!file.canRead())
			return false;
		try {
			Scanner in = new Scanner(file);
			
			State state = new State();
			state.ta = new int[opt.classes*opt.clauses][opt.features*2];
			for(int j=0; j<opt.features*2; j++) {
				String line = in.nextLine();
				String[] s = line.split(",|\\s+");
				for(int i=0; i<opt.classes*opt.clauses; i++) {
					state.ta[i][j] = Integer.parseInt(s[i]);
				}
			}
			
			in.close();
			epochStates.add(state);
			return true;
		}
		catch (Exception e) {
			System.err.println("Cannot read CSV");
			e.printStackTrace();
			return false;
		}
	}

	public void loadEpochs(String pathFormat) {
		epochStates = new ArrayList<>();
		maxEpoch = 0;
		while(loadEpoch(pathFormat, maxEpoch+1))
			maxEpoch++;
	}
	
	@Override
	public TsetlinOptions getOptions() {
		return opt;
	}

	@Override
	public int getState(int cls, int clause, Polarity polarity, int feature) {
		return epochStates.get(this.epoch-1).ta[cls*opt.clauses+clause][polarity==Polarity.positive ? feature : feature+opt.features];
	}

	@Override
	public int getEpoch() {
		return epoch;
	}

	@Override
	public String getStateTitle() {
		return null;
	}

	@Override
	public void reset() {
		epoch = 1;
	}

	@Override
	public void nextState() {
		nextEpoch();
	}

	@Override
	public void prevState() {
		prevEpoch();
	}

	@Override
	public void nextEpoch() {
		if(epoch<maxEpoch)
			epoch++;
	}

	@Override
	public void prevEpoch() {
		if(epoch>1)
			epoch--;
	}

	@Override
	public double evaluateTrain() {
		return -1;
	}

	@Override
	public double evaluateTest() {
		return -1;
	}

	@Override
	public boolean includeLiteral(int state) {
		return state>=opt.numStates/2;
	}

	@Override
	public float getIncludeLevel(int state) {
		if(state>opt.numStates/2)
			return (state+1-opt.numStates/2) / (float)(opt.numStates/2);
		else
			return (state-opt.numStates/2) / (float)(opt.numStates/2);
	}

	public static void main(String[] args) {
		try {
			TsetlinCsvView csv = new TsetlinCsvView(demoOpt, "D:/Poets/tsetlin/IrisRunTaStates/%d.csv");
			TsetlinStateViewer.startViewer(csv);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
