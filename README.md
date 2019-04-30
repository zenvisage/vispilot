# VisPilot

## Introduction
As datasets continue to grow in size and complexity, exploring multi-dimensional datasets remain challenging for analysts. A common operation during this exploration is drilldown—understanding the behavior of data subsets by progressively adding filters. While widely used, in the absence of careful attention towards confounding factors, drill-downs could lead to inductive fallacies. Specifically, an analyst may end up being “deceived” into thinking that a deviation in trend is attributable to a local change, when in fact it is a more general phenomenon; we term this the drill-down fallacy. One way to avoid falling prey to drill-down fallacies is to exhaustively explore all potential drill-down paths, which quickly becomes infeasible on complex datasets with many attributes. We present VisPilot, an accelerated visual data exploration tool that guides analysts through the key insights in a dataset, while avoiding drill-down fallacies. Our user study results show that VisPilot helps analysts discover interesting visualizations, understand attribute importance, and predict unseen visualizations better than other multidimensional data analysis baselines.

## Basic Setup

To build the project, run:
```
bash build.sh
```
Under the ``/vispilot/`` directory.

Install postgres at: https://postgresapp.com/

If it doesn't work on the current port(5432), try a another port.
```
$psql -d postgres

CREATE USER summarization WITH CREATEDB CREATEROLE;
ALTER USER summarization WITH PASSWORD 'lattice';
ALTER USER summarization WITH SUPERUSER;
```

## Build Outputs

To run the built server, run:
```
bash run.sh
```
Under the ``/vispilot/`` directory.



