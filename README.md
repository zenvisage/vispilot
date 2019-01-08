# vispilot

The system is a Flask application inside the ``systems/`` directory, to run the webapp, make sure you have the latest version of Flask installed. 
 
```
export FLASK_APP=main.py
python -m flask run
```

Install postgres at: https://postgresapp.com/

If it doesn't work on the current port(5432), try a another port.
```
$psql -d postgres

CREATE USER summarization WITH CREATEDB CREATEROLE;
ALTER USER summarization WITH PASSWORD 'lattice';
ALTER USER summarization WITH SUPERUSER;
```
To upload data see the [notebook](https://github.com/dorisjlee/viz-summarization/blob/master/ipynb/Testing%20Postgres%20SQL.ipynb)

If after running the flask server, you see the error: 
```
socket.error: [Errno 48] Address already in use
```
then kill the process by : 
```
$ ps aux | grep 'python -m flask run'
dorislee         28248   0.6  0.0  2436888    812 s002  S+    4:15PM   0:00.00 grep --color=auto python -m flask run
dorislee         27885   0.0  0.5  2552352  76224 s002  T     3:58PM   0:01.26 python -m flask run

$kill $(ps aux | grep 'python -m flask run' | awk '{print $2}')
```
