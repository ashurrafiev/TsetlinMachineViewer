package ncl.tsetlin.csv;

import static ncl.tsetlin.ConfigLoader.getString;
import static ncl.tsetlin.ConfigLoader.getInt;
import static ncl.tsetlin.ConfigLoader.getBoolean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

import ncl.tsetlin.view.TsetlinStateTracker;

public class DataLogger {

	public static final String filenameTAStates = "-tastates.csv";
	public static final String filenameStatus = "-status.csv";
	
	public String rootPath = "data/logs";
	public String logName = "tm";
	
	public int logFrequency = 0;
	public boolean optLogTAStates = false;
	public boolean optLogStatus = true;

	protected int step = 0;
	protected PrintWriter outTAStates = null;
	protected PrintWriter outStatus = null;
	
	protected PrintWriter resetFile(PrintWriter out, String name) {
		if(out!=null)
			out.close();
		try {
			new File(rootPath).mkdirs();
			out = new PrintWriter(new File(rootPath, logName+name));
			return out;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void headersTAStates(PrintWriter out, TsetlinStateTracker tracker) {
		if(out==null) return;
		out.printf("t\t");
		int classes = tracker.getOptions().classes;
		int clauses = tracker.getOptions().clauses;
		int literals = tracker.countTAPerClause();
		for(int cls=0; cls<classes; cls++)
			for(int j=0; j<clauses; j++)
				for(int k=0; k<literals; k++)
					out.printf("s%d-%d-%d\t", cls, j, k);
		out.println();
		out.flush();
	}

	protected void logTAStates(PrintWriter out, int t, TsetlinStateTracker tracker) {
		if(out==null) return;
		out.printf("%d\t", t);
		int literals = tracker.countTAPerClause();
		for(int cls=0; cls<tracker.getOptions().classes; cls++)
			for(int j=0; j<tracker.getOptions().clauses; j++)
				for(int k=0; k<literals; k++)
					out.printf("%d\t", tracker.getRawTAState(cls, j, k));
		out.println();
		out.flush();
	}

	protected void headersStatus(PrintWriter out, TsetlinStateTracker tracker) {
		if(out==null) return;
		out.printf("t\t");
		tracker.printStatusHeader(out);
		out.println();
		out.flush();
	}

	protected void logStatus(PrintWriter out, int t, TsetlinStateTracker tracker) {
		if(out==null) return;
		out.printf("%d\t", t);
		tracker.printStatus(out);
		out.println();
		out.flush();
	}

	protected void log(int t, TsetlinStateTracker tracker) {
		if(optLogTAStates) logTAStates(outTAStates, t, tracker);
		if(optLogStatus) logStatus(outStatus, t, tracker);
	}

	public void reset(TsetlinStateTracker tracker) {
		step = 0;
		if(optLogTAStates)
			headersTAStates(outTAStates = resetFile(outTAStates, filenameTAStates), tracker);
		if(optLogStatus)
			headersStatus(outStatus = resetFile(outStatus, filenameStatus), tracker);
	}

	public void log(TsetlinStateTracker tracker) {
		int t = tracker.getStateIndex();
		if(logFrequency>0) {
			if(t%logFrequency==0)
				log(step++, tracker);
		}
		else {
			if(t==0)
				log(step++, tracker);
		}
	}
	
	public DataLogger setConfigValues(HashMap<String, String> values) {
		this.rootPath = getString(values.get("logPath"), this.rootPath);
		this.logName = getString(values.get("logName"), this.logName);
		this.logFrequency = getInt(values.get("logFrequency"), 0, Integer.MAX_VALUE, 0);
		this.optLogTAStates = getBoolean(values.get("logTAStates"), false);
		this.optLogStatus = getBoolean(values.get("logStatus"), true);
		return this;
	}
}
