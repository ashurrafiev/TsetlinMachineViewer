from jdiag import *

from com.xrbpowered.jdiagram.data import NumberFormatter
from com.xrbpowered.jdiagram.data import ExpFormatter

from com.xrbpowered.jdiagram.chart import Page
from com.xrbpowered.jdiagram.chart import ScatterChart
from com.xrbpowered.jdiagram.chart.ScatterChart import Population
from com.xrbpowered.jdiagram.chart import Anchor

chartWidth = 1800
chartHeight = 400
epochs = 100
epochStep = 10
classes = 2
clauses = 15
literals = 64*2

data = Data.read(File('tm-tastates.csv'))

page = Page(1)

for cls in range(classes):
	# set up a new chart
	chart = ScatterChart().setSize(chartWidth, chartHeight).setTitle('TA states: Class %d' % cls)
	chart.setMargins(50, 20, 30, 80)
	#chart.legend.setCols(2).posBottom(-40).setItemSize(150, 20)
	chart.clipChart = True
			
	# set up axes
	chart.axisx.setRange(0, epochs, epochStep) \
			.setAnchor(Anchor.bottom).setLabel('epoch').setNumberFormatter(NumberFormatter.simple('%.0f'))
	chart.axisy.setRange(-10, 10, 1) \
			.setAnchor(Anchor.left).setLabel('state', Anchor.left.offset(-35)).setNumberFormatter(NumberFormatter.simple('%.0f'))

	# add data lines
	for j in range(clauses):
		for k in range(literals):
			chart.addPop(Population(data, 't', 's%d-%d-%d' % (cls, j, k), 'fill:none;stroke-width:1;stroke:#000;stroke-opacity:0.1'))

	page.add(chart)

# finish and print
page.printPage(System.out)
