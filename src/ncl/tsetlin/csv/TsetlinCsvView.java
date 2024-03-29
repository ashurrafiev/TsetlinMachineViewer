package ncl.tsetlin.csv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import ncl.tsetlin.TsetlinMachine.Polarity;
import ncl.tsetlin.TsetlinMachine;
import ncl.tsetlin.TsetlinOptions;
import ncl.tsetlin.view.TsetlinStateTracker;

public class TsetlinCsvView implements TsetlinStateTracker {
	
	public class State {
		public int ta[][][];
	}
	
	public final TsetlinOptions opt;
	
	public ArrayList<State> epochStates;
	public int maxEpoch;
	
	public int epoch;
	
	public TsetlinCsvView(TsetlinOptions opt, String path) throws IOException {
		this.opt = opt;
		if(!loadEpochs(path))
			throw new IOException("Cannot load CSV data.");
	}
	
	public boolean loadEpochs(String path) {
		File file = new File(path);
		if(!file.canRead())
			return false;
		try {
			epochStates = new ArrayList<>();
			maxEpoch = 0;
			Scanner in = new Scanner(file);

			boolean first = true;
			while(in.hasNextLine()) {
				String line = in.nextLine();
				if(line.isEmpty())
					continue;
				if(first) {
					first = false;
					continue;
				}
				String[] s = line.split(",|\\s+");
				if(s.length!=opt.classes*opt.clauses*opt.features*2+1) {
					in.close();
					throw new IOException("Number of TAs in CSV doesn't match the config");
				}
				
				State state = new State();
				state.ta = new int[opt.classes][opt.clauses][opt.features*2];
				int index = 1;
				for(int cls=0; cls<opt.classes; cls++)
					for(int j=0; j<opt.clauses; j++)
						for(int k=0; k<opt.features*2; k++)
							state.ta[cls][j][k] = Integer.parseInt(s[index++]);
				
				epochStates.add(state);
				maxEpoch++;
			}
			
			in.close();
			return true;
		}
		catch (Exception e) {
			System.err.println("Cannot read CSV");
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public TsetlinOptions getOptions() {
		return opt;
	}

	@Override
	public int getTAState(int cls, int clause, Polarity polarity, int feature) {
		return epochStates.get(this.epoch-1).ta[cls][clause][polarity==Polarity.positive ? feature : feature+opt.features];
	}
	
	@Override
	public int countTAPerClause() {
		return opt.features*2;
	}
	
	@Override
	public int getRawTAState(int cls, int clause, int ta) {
		return epochStates.get(this.epoch-1).ta[cls][clause][ta];
	}

	@Override
	public int getEpoch() {
		return epoch;
	}
	
	@Override
	public int getStateIndex() {
		return 0;
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
	public void printStatusHeader(PrintWriter out) {
	}
	
	@Override
	public void printStatus(PrintWriter out) {
	}
	
	@Override
	public boolean includeLiteral(int state) {
		return TsetlinMachine.includeLiteral(state);
	}

	@Override
	public float getIncludeLevel(int state) {
		return (float) TsetlinMachine.getIncludeLevel(opt, state);
	}

	@Override
	public TsetlinStateTracker setConfigValues(HashMap<String, String> values) {
		return this;
	}
}
