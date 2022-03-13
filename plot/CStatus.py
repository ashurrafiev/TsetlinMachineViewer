from jdiag import *

from com.xrbpowered.jdiagram.data import NumberFormatter
from com.xrbpowered.jdiagram.data import ExpFormatter

from com.xrbpowered.jdiagram.chart import Page
from com.xrbpowered.jdiagram.chart import ScatterChart
from com.xrbpowered.jdiagram.chart.ScatterChart import Population
from com.xrbpowered.jdiagram.chart import Anchor

chartWidth = 1500
chartHeight = 250
epochs = 180
epochStep = 6
numClasses = 10

classes = [
	('1', 'class 1', 'stroke-width:1.5;stroke-dasharray:3 3;stroke:#a00'),
	('9', 'class 9', 'stroke-width:1.5;stroke-dasharray:9 3;stroke:#555'),
	('A', 'all-class average', 'stroke-width:2;stroke:#7b0')
]

data = Data.read(File('tm-status.csv'))
for cls in range(numClasses):
	data.addCol('ratio-c%d' % cls, ratio(getNum('type1-c%d' % cls), getNum('type2-c%d' % cls)))

def addAvg(hdr):
	cols =list(map(lambda x:hdr%x, range(numClasses)))
	data.addCol(hdr % 'A', average(cols));
addAvg('inc%s')
addAvg('flips%s')
addAvg('ratio-c%s')
addAvg('vote%s')

page = Page(1)

class TimeFmt(NumberFormatter):
	def format(py, x):
		t = int(x)
		return '%d:%dk' % (t/60, t%60)

# accuracy

chart = ScatterChart().setSize(chartWidth, chartHeight).setTitle('Accuracy')
chart.setMargins(50, 20, 30, 80)
chart.legend.setCols(1).posBottom(-40).setItemSize(150, 20)
chart.clipChart = True
		
chart.axisx.setRange(0, epochs, epochStep) \
		.setAnchor(Anchor.bottom).setLabel('time (epochs:inputs)').setNumberFormatter(TimeFmt())
chart.axisy.setRange(0.5, 1, 0.05) \
		.setAnchor(Anchor.left).setLabel('accuracy', Anchor.left.offset(-35)).setNumberFormatter(NumberFormatter.simple('%.2f'))

#chart.addPopLegend('train', Population(data, 't', 'acctrain', 'fill:none;stroke-width:2;stroke:#7b0'))
chart.addPopLegend('against test data', Population(data, 't', 'acctest', 'fill:none;stroke-width:2;stroke:#d00'))

page.add(chart)

#other charts

charts = [
	('Number of inclusions', (False, 0, 12000, 1000), 'literals', '%.0f', 'inc%s'),
	('Number of decision flips', (False, 0, 20, 5), 'flips', '%.0f', 'flips%s'),
	('Feedback by type', (True, 0.25, 4.0, 2), 'T1 / T2 ratio', '%.2f', 'ratio-c%s'),
	('Absolute sum votes', (False, 0, 5, 1), 'votes', '%.0f', 'vote%s')
]

for (title, yaxis, ylabel, yfmt, hdr) in charts:
	chart = ScatterChart().setSize(chartWidth, chartHeight).setTitle(title)
	chart.setMargins(50, 20, 30, 80)
	chart.legend.setCols(len(classes)).posBottom(-40).setItemSize(150, 20)
	chart.clipChart = True
			
	chart.axisx.setRange(0, epochs, epochStep) \
			.setAnchor(Anchor.bottom).setLabel('time (epochs:inputs)').setNumberFormatter(TimeFmt())
	chart.axisy.setRange(yaxis[0], yaxis[1], yaxis[2], yaxis[3]) \
			.setAnchor(Anchor.left).setLabel(ylabel, Anchor.left.offset(-35)).setNumberFormatter(NumberFormatter.simple(yfmt))

	for (cls, legend, stroke) in classes:
		chart.addPopLegend(legend, Population(data, 't', hdr % cls, 'fill:none;%s' % stroke))

	page.add(chart)


# finish and print
page.printPage(System.out)
