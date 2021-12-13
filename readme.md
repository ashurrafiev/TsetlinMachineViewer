
# TsetlinMachineViewer

GUI application for visualising [Tsetlin machine](https://github.com/cair/TsetlinMachine) (TM) states and learning process.

## Download and Run

* Download: [**tsetlinviewer.jar**](https://github.com/ashurrafiev/TsetlinMachineViewer/releases/download/1.0/tsetlinviewer.jar)
* Download example data: [**data.zip**](https://github.com/ashurrafiev/TsetlinMachineViewer/releases/download/1.0/data.zip)

Requires [Java 8](https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=hotspot) or later to run.

Running from command line:

```
java -jar tsetlinviewer.jar <config_file.cfg>
```

Example configurations:
* [BinaryIris.cfg](BinaryIris.cfg) live learning demo using IRIS dataset.
* [NoisyXor.cfg](NoisyXor.cfg) live learning demo using [Noisy XOR](https://github.com/cair/TsetlinMachine) dataset.
* [IrisRunCsv.cfg](IrisRunCsv.cfg) viewing IRIS learning saved as CSV files.

See [Control keys](#control-keys) on how to operate the GUI.

## Configuration commands

_TsetlinMachineViewer_ uses configuration files to set up TM and viewer options. Configuration file consists of `key : value` pairs, one pair per line.

* **classes** (int, required) the number of classes. This is specific to the training data set: each training data point is classified into one of these classes.
* **features** (int, required) the number of input features. This is specific to the training data set: each training data point represents a Boolean vector of this size.
* **clauses** (int, required) the number of TM clauses per class.
* **numStates** (int, required) the number of TA states _per decision_, e.g. `numStates : 10` means there are 10 _include_ states and 10 _exclude_ states.

#### Live learning mode

* **threshold** (int, required for live learing) learning threshold parameter.
* **s** (float, required for live learing) learning rate parameter.
* **countTrain** (int, required for live learing) the number of training data points in the input dataset.
* **countTest** (int, required for live learing) the number of test data points in the input dataset.
* **trainData** (path string) training data file; see [Training data format](#training-data-format). You must specify either **combinedData** or both **trainData** and **testData**.
* **testData** (path string) test data file; see [Training data format](#training-data-format). You must specify either **combinedData** or both **trainData** and **testData**.
* **combinedData** (path string) input data file; see [Training data format](#training-data-format). The app will randomly divide these data into training and test data sets based on the **countTrain** and **countTest** options. You must specify either **combinedData** or both **trainData** and **testData**.

#### CSV-view mode

Providing **csvPathFormat** toggles CSV-view mode. CSV-view mode doesn't use live learning options.

* **csvPathFormat** (path string) path to CSV (or TSV) files, one file per learning epoch. Use `%d` in the path format to indicate epoch indices. Each line of the file corresponds to a literal, 2 &times; **features** lines in total: first **features** literals are positive, last **features** literals are negative. One line contains **classes** &times; **clauses** TA state values in order.

#### GUI options

* **uiDrawLiterals** (boolean, optional) start GUI in literal display mode (`true`) or TA state display (`false`, default).
* **uiClassesVertical** (boolean, optional) start GUI in vertical layout (`true`, default) or horizontal layout (`false`).
* **uiScale** (float, optional) initial zoom, default is 1.

## Training data format

Training and test data is expected in the text format. Each line contains one data point (input vector and a label). Input vector is given as 0 or 1 values, the number of values should match **features**. The last value in a line is the _label_ - a class that corresponds to this input. The label value must be within range 0 &leq; label < **classes**.

## Control keys

Use **right mouse button** to pan around and **Control+scroll** to zoom in and out.

| key | action |
| :---: | :--- |
| **Up** | previous state; live mode: does nothing |
| **Down** | next state; live mode: process next training data point |
| **Page Up** | previous epoch; live mode: does nothing |
| **Page Down** | next epoch; live mode: process all remaning training data (i.e. progress to the next epoch) |
| **Backspace** | reset TM to the initial state; live mode: randomise initial state |
| **L** | toggle between literal display and TA state display |
| **V** | toggle between horizontal and vertical layouts |

## Building the sources

The project is dependant on [ZoomUI library](https://github.com/AshurAxelR/ZoomUI) (use the latest sources from the `master` branch, not the JAR).
