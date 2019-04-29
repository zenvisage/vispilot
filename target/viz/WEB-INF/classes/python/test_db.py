# Create tests cases for all functions in Database.py 
from flask.ext.sqlalchemy import SQLAlchemy
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://localhost/viz-summarization'
db = SQLAlchemy(app)

#Initialize DB 
def test_upload():
	#Test Upload Data (Titanic)
	raise NotImplementedError
def test_get_tables():
	# Check Titanic contained in Table from get_all_tables()
	raise NotImplementedError
def test_get_columns():
	# Check corresponds to Titanic schema
	raise NotImplementedError
def test_construct_query(tablename,x_attr,y_attr, agg_func, filters):
	# Check constructed query return expected numbers of tuples
	raise NotImplementedError
