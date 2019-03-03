import pandas as pd
from sqlalchemy import create_engine
data = pd.read_csv("titanic.csv")
data.loc[data["survived"]==1,"survived"]="t"
data.loc[data["survived"]==0,"survived"]="f"
data = data.rename(index=str,columns={"sex":"gender"})
data = data.drop(["sexcode","name"],axis=1)
data = data.dropna(axis=0,how='any') # Drop any columns with NaN values (since it messes with the combination code)
engine = create_engine("postgresql://summarization:lattice@localhost:5432")
data.to_sql(name='titanic', con=engine, if_exists = 'replace', index=False)

