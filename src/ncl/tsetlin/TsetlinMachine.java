package ncl.tsetlin;

import java.util.Random;

public class TsetlinMachine {

	public boolean countFlips = true;
	public boolean countFeedback = true;

	public static final int PROMOTE = +1;
	public static final int DEMOTE = -1;
	
	public enum Polarity { positive, negative }
	
	// random number generator
	public static final Random rand = new Random();
	
	// Tsetlin Machine configuration parameters
	public final TsetlinOptions opt;
	public final int literals;

	/**
	 * Randomly returns true with the given probability
	 */
	private static boolean withProbability(double prob) {
		return rand.nextDouble() < prob;
	}
	
	/**
	 * Returns voting sign for j-th clause: odd clauses vote -1, even clauses vote +1
	 */
	private static int vote(int j) {
		return ((j&1)!=0) ? -1 : 1;
	}
	
	/**
	 * Returns polarity of k-th literal: odd literals are negated (1), even literals are positive (0)
	 */
	private static boolean polarity(int k) {
		return (k&1)!=0;
	}
	
	/**
	 * Calculate the value of k-th literal on the given input respecting literal polarity
	 */
	private static boolean literalValue(boolean[] input, int k) {
		return (input)[(k)/2] ^ polarity(k);
	}
	
	/**
	 * Determine include (true) or exclude (false) decision based on a TA state
	 */
	public static boolean includeLiteral(int state) {
		return state>0;
	}
	
	public static double getIncludeLevel(TsetlinOptions opt, int state) {
		if(state>0)
			return state / (double) opt.numStates;
		else
			return (state-1) / (double) opt.numStates;
	}
	
	public class Clause {
		// TA states for positive (even) and negative (odd) polarity literals
		public int[] ta;
		
		// clause output (cached value)
		public boolean output;
		
		/**
		 * Calculate clause output for a given input vector.
		 * The result is cached in the output field of the Clause object
		 * @param input input vector 
		 * @return clause output value
		 */
		public boolean calculateOutput(boolean[] input, boolean eval) {
			output = true;
			boolean inc = false;
			// calculate conjunction over k literals
			// (we can stop early if output becomes false)
			for(int k=0; output && k<literals; k++) {
				if(includeLiteral(ta[k])) {
					output &= literalValue(input, k);
					inc = true;
				}
			}
			if(eval && !inc)
				return output = false;
			return output;
		}
		
		public void updateTA(int k, int action) {
			int nextState = ta[k]+action;
			
			// update, if next state is within allowed states
			if(nextState>-opt.numStates && nextState<=opt.numStates)
				ta[k] = nextState;
		}
	}
	
	public Clause[] clauses;

	public int flips = 0;
	public int countType1 = 0;
	public int countType2 = 0;

	public TsetlinMachine(TsetlinOptions opt) {
		this.opt = opt;
		this.literals = opt.features*2;
		
		// create all clauses
		clauses = new Clause[opt.clauses];
		for(int j=0; j<opt.clauses; j++) {
			clauses[j] = new Clause();
			clauses[j].ta = new int[literals];
		}
		
		initialize();
	}
	
	/**
	 * reset all TAs by randomly assigning weak include or exclude states
	 */
	public void initialize() {
		for(int j=0; j<opt.clauses; j++) {				
			for(int k=0; k<literals; k+=2) {
				this.clauses[j].ta[k] = 0;
				this.clauses[j].ta[k+1] = 0; 
			}
		}
	}
	
	public void remapTAStates() {
		for(int j=0; j<opt.clauses; j++) {				
			for(int k=0; k<literals; k++) {
				if(includeLiteral(this.clauses[j].ta[k]))
					this.clauses[j].ta[k] = 1;
				else
					this.clauses[j].ta[k] = -opt.numStates+1;
			}
		}
	}

	/**
	 * Update clauses for the given input vector
	 * @param input
	 */
	private void calculateClauseOutputs(boolean input[], boolean eval) {
		for(int j=0; j<opt.clauses; j++) {
			this.clauses[j].calculateOutput(input, eval);
		}
	}

	/**
	 * Calculate class voting based on the clause outputs.
	 * Must be called after calculateClauseOuputs.
	 */
	private int calculateVoting() {
		int sum = 0;
		for(int j=0; j<opt.clauses; j++) {
			// if output is true, the clause is active
			if(this.clauses[j].output) {
				// add vote
				sum += vote(j);
			}
		}
		return sum;
	}
	
	/**
	 * return TA state for the given clause, feature, and polarity of a literal
	 */
	public int getState(int clause, Polarity polarity, int feature) {
		int k = feature*2;
		if(polarity==Polarity.negative)
			k++;
		return this.clauses[clause].ta[k];
	}
	
	private void typeIFeedback(Clause clause, boolean[] input) {
		for(int k=0; k<literals; k++) {
			if(clause.output && literalValue(input, k)) { // clause is 1 and literal is 1
				if(withProbability(1.0-1.0/opt.s))
					clause.updateTA(k, PROMOTE);
			}
			else { // clause is 0 or literal is 0
				if(withProbability(1.0/opt.s))
					clause.updateTA(k, DEMOTE);
			}
		}
	}

	private void typeIIFeedback(Clause clause, boolean[] input) {
		// only if clause is 1
		if(clause.output) {
			for(int k=0; k<literals; k++) {
				if(!literalValue(input, k) && !includeLiteral(clause.ta[k])) // if literal is 0 and excluded
					clause.updateTA(k, PROMOTE);
			}
		}
	}

	public void update(boolean[] input, boolean output) {
		boolean[][] prevInc = null;
		if(countFlips) {
			prevInc = new boolean[opt.clauses][literals];
			for(int j=0; j<opt.clauses; j++)
				for(int k=0; k<literals; k++) {
					prevInc[j][k] = includeLiteral(clauses[j].ta[k]);
				}
		}
		
		calculateClauseOutputs(input, false);
		int classSum = calculateVoting();

		// calculate feedback probability
		double feedbackProbability;
		feedbackProbability = (opt.threshold - classSum) / (2.0 * opt.threshold);
		if(!output)
			feedbackProbability = 1.0 - feedbackProbability;

		for(int j=0; j<opt.clauses; j++) {
			// inverse the decision for negatively-voting clauses
			boolean y;
			if(vote(j)>0)
				y = output;
			else
				y = !output;
			
			if(y) {
				if(withProbability(feedbackProbability)) {
					if(countFeedback) countType1++;
					typeIFeedback(this.clauses[j], input);
				}
			}
			else {
				if(withProbability(feedbackProbability)) {
					if(countFeedback) countType2++;
					typeIIFeedback(this.clauses[j], input);
				}
			}
		}
		
		if(prevInc!=null) {
			for(int j=0; j<opt.clauses; j++)
				for(int k=0; k<literals; k++) {
					if(prevInc[j][k]!=includeLiteral(clauses[j].ta[k]))
						flips++;
				}
		}
	}
	
	public int score(boolean[] input) {
		calculateClauseOutputs(input, true);
		return calculateVoting();
	}

	public int countIncluded() {
		int count = 0;
		for(int j=0; j<opt.clauses; j++)
			for(int k=0; k<literals; k++) {
				if(includeLiteral(clauses[j].ta[k]))
					count++;
			}
		return count;
	}
	
}
