package ncl.tsetlin;

public class MultiClassTsetlinMachine {

	public final TsetlinOptions opt;
	public TsetlinMachine[] tsetlinMachines;
	
	public MultiClassTsetlinMachine(TsetlinOptions opt) {
		this.opt = opt;
		this.tsetlinMachines = new TsetlinMachine[opt.classes];
		for(int i=0; i<opt.classes; i++) {
			this.tsetlinMachines[i] = new TsetlinMachine(opt);
		}
	}

	public void initialize() {
		for(int i=0; i<opt.classes; i++) {
			this.tsetlinMachines[i].initialize();
		}
	}
	
	public double evaluate(boolean[][] inputs, int[] labels, int numberOfExamples) {
		int maxClass;
		int maxClassSum;

		int errors = 0;
		for(int l=0; l<numberOfExamples; l++) {
			maxClassSum = this.tsetlinMachines[0].score(inputs[l]);
			maxClass = 0;
			for(int i=1; i<opt.classes; i++) {
				int classSum = this.tsetlinMachines[i].score(inputs[l]);
				if(maxClassSum < classSum) {
					maxClassSum = classSum;
					maxClass = i;
				}
			}

			if(maxClass!=labels[l]) {
				errors += 1;
			}
		}
		
		return 1.0 - errors / (double)numberOfExamples;
	}
	
	public void update(boolean input[], int output) {
		this.tsetlinMachines[output].update(input, true);

		int negativeTargetClass = TsetlinMachine.rand.nextInt(opt.classes);
		while(negativeTargetClass == output) {
			negativeTargetClass = TsetlinMachine.rand.nextInt(opt.classes);
		}

		this.tsetlinMachines[negativeTargetClass].update(input, false);
	}

	public void fit(boolean[][] inputs, int[] outputs, int numberOfExamples) {
		for(int l=0; l<numberOfExamples; l++) {
			update(inputs[l], outputs[l]);
		}
	}
	
	public void remapTAStates() {
		for(int i=0; i<opt.classes; i++) {
			this.tsetlinMachines[i].remapTAStates();
		}
	}

	public void fit(boolean[][] inputs, int[] outputs, int numberOfExamples, int epochs) {
		for(int epoch=0; epoch<epochs; epoch++) {
			fit(inputs, outputs, numberOfExamples);
		}
	}
	
	public int countIncluded() {
		int count = 0;
		for(int i=0; i<opt.classes; i++) {
			count += this.tsetlinMachines[i].countIncluded();
		}
		return count;
	}
}
