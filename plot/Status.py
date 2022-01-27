from jdiag import *

from com.xrbpowered.jdiagram.data import NumberFormatter
from com.xrbpowered.jdiagram.data import ExpFormatter

from com.xrbpowered.jdiagram.chart import Page
from com.xrbpowered.jdiagram.chart import ScatterChart
from com.xrbpowered.jdiagram.chart.ScatterChart import Population
from com.xrbpowered.jdiagram.chart import Anchor

chartWidth = 1800
chartHeight = 250
epochs = 100
epochStep = 10
classes = 2

data = Data.read(File('tm-status.csv'))

# accuracy
page = Page(1)

chart = ScatterChart().setSize(chartWidth, chartHeight).setTitle('Accuracy')
chart.setMargins(50, 20, 30, 80)
chart.legend.setCols(2).posBottom(-40).setItemSize(150, 20)
chart.clipChart = True
		
chart.axisx.setRange(0, epochs, epochStep) \
		.setAnchor(Anchor.bottom).setLabel('epoch').setNumberFormatter(NumberFormatter.simple('%.0f'))
chart.axisy.setRange(0, 1.0, 0.1) \
		.setAnchor(Anchor.left).setLabel('accuracy', Anchor.left.offset(-35)).setNumberFormatter(NumberFormatter.simple('%.1f'))

chart.addPopLegend('train', Population(data, 't', 'acctrain', 'fill:none;stroke-width:2;stroke:#7b0'))
chart.addPopLegend('test', Population(data, 't', 'acctest', 'fill:none;stroke-width:2;stroke:#d00'))

page.add(chart)

# inclusions
chart = ScatterChart().setSize(chartWidth, chartHeight).setTitle('Number of inclusions')
chart.setMargins(50, 20, 30, 80)
chart.legend.setCols(classes).posBottom(-40).setItemSize(150, 20)
chart.clipChart = True
		
chart.axisx.setRange(0, epochs, epochStep) \
		.setAnchor(Anchor.bottom).setLabel('epoch').setNumberFormatter(NumberFormatter.simple('%.0f'))
chart.axisy.setRange(0, 800, 100) \
		.setAnchor(Anchor.left).setLabel('literals', Anchor.left.offset(-35)).setNumberFormatter(NumberFormatter.simple('%.0f'))

colors = ['#555', '#888', '#bbb'];
for cls in range(classes):
	chart.addPopLegend('class %d' % cls, Population(data, 't', 'inc%d' % cls, 'fill:none;stroke-width:2;stroke:%s' % colors[cls]))

page.add(chart)

# flips
chart = ScatterChart().setSize(chartWidth, chartHeight).setTitle('Number of decision flips')
chart.setMargins(50, 20, 30, 80)
chart.legend.setCols(classes).posBottom(-40).setItemSize(150, 20)
chart.clipChart = True
		
chart.axisx.setRange(0, epochs, epochStep) \
		.setAnchor(Anchor.bottom).setLabel('epoch').setNumberFormatter(NumberFormatter.simple('%.0f'))
chart.axisy.setRange(0, 1500, 500) \
		.setAnchor(Anchor.left).setLabel('literals', Anchor.left.offset(-35)).setNumberFormatter(NumberFormatter.simple('%.0f'))

colors = ['#700', '#f00', '#f77'];
for cls in range(classes):
	chart.addPopLegend('class %d' % cls, Population(data, 't', 'flips%d' % cls, 'fill:none;stroke-width:2;stroke:%s' % colors[cls]))

page.add(chart)

# feedback
chart = ScatterChart().setSize(chartWidth, chartHeight).setTitle('Number of feedbacks by type')
chart.setMargins(50, 20, 30, 80)
chart.legend.setCols(classes*2).posBottom(-40).setItemSize(150, 20)
chart.clipChart = True
		
chart.axisx.setRange(0, epochs, epochStep) \
		.setAnchor(Anchor.bottom).setLabel('epoch').setNumberFormatter(NumberFormatter.simple('%.0f'))
chart.axisy.setRange(0, 100, 10) \
		.setAnchor(Anchor.left).setLabel('literals', Anchor.left.offset(-35)).setNumberFormatter(NumberFormatter.simple('%.0f'))

colors = ['#037', '#07f', '#7df'];
for cls in range(classes):
	chart.addPopLegend('class %d, type I' % cls, Population(data, 't', 'type1-c%d' % cls, 'fill:none;stroke-width:2;stroke:%s' % colors[cls]))
	chart.addPopLegend('class %d, type II' % cls, Population(data, 't', 'type2-c%d' % cls, 'fill:none;stroke-width:2;stroke-dasharray: 2 3;stroke:%s' % colors[cls]))

page.add(chart)

# finish and print
page.printPage(System.out)