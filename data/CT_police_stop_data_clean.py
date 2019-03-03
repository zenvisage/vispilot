# Only Conneticut data. Tried to collect all datasets with complete information (indicated on the stanford website)
# eliminated IL and WA data which had all is_arrested as NaN, and VT eliminated because no stop duration, RI had no violation attribute

import pandas as pd
from glob import glob
from sklearn import preprocessing


fname="CT-clean.csv"
df = pd.read_csv(fname)
# Making stop time a categorical variable
df = df[~df["stop_time"].isnull()] #drop rows where no stop_time was recorded (about 222 records)
df["stop_hr"] = df["stop_time"].apply(lambda x:  int(x.split(":")[0]) )
df.loc[(df["stop_hr"]>=6)&(df["stop_hr"]<12),"stop_time_of_day"]="Morning"
df.loc[(df["stop_hr"]>=12)&(df["stop_hr"]<18),"stop_time_of_day"]="Afternoon"
df.loc[(df["stop_hr"]>=18)&(df["stop_hr"]<22),"stop_time_of_day"]="Early Evening"
df.loc[(df["stop_hr"]>=22)|(df["stop_hr"]<6),"stop_time_of_day"]="Late Evening"

# Making driver's age a categorical variable
df.loc[(df["driver_age"]<=21),"driver_age_category"]="21 and under"
df.loc[(df["driver_age"]>21)&(df["driver_age"]<30),"driver_age_category"]="22-29"
df.loc[(df["driver_age"]>=30)&(df["driver_age"]<40),"driver_age_category"]="30-39"
df.loc[(df["driver_age"]>=40)&(df["driver_age"]<50),"driver_age_category"]="40-49"
df.loc[(df["driver_age"]>=50)&(df["driver_age"]<60),"driver_age_category"]="50-59"
df.loc[(df["driver_age"]>=60),"driver_age_category"]="60 and over"

# Merging Arrest with Summons
df.loc[(df["stop_outcome"]=="Arrest")|(df["stop_outcome"]=="Summons"),"stop_outcome"]="Arrest/Summons"
# Merging Verbal and Written Summons
df.loc[(df["stop_outcome"]=="Verbal Warning")|(df["stop_outcome"]=="Written Warning"),"stop_outcome"]="Verbal/Written Warning"
# # Merging Asians with Others
# df.loc[(df["driver_race"]=="Asian")|(df["driver_race"]=="Other"),"driver_race"]="Others"
#renaming stop outcome to abbrev x axis
df.loc[df["stop_outcome"]=="Ticket","stop_outcome"]="ticket"
df.loc[df["stop_outcome"]=="Verbal/Written Warning","stop_outcome"]="warn"
df.loc[df["stop_outcome"]=="Arrest/Summons","stop_outcome"]="arrest"
# Making the top 5 common violations into 5 binary attributes
df = df[~df["violation"].isnull()] 
top5violations= list(df["violation"].value_counts()[:5].keys()) 
for violation in top5violations:
    if "violation" not in violation:
        violation_name = violation +"_violations"
    else:
        violation_name = violation
    violation_name = violation_name.lower().replace(" ","_").replace("/","_")
    df[violation_name]=False
    df.loc[df.violation.apply(lambda x: violation in x ),violation_name]=True
df = df.drop(["stop_date","stop_time","location_raw","county_name",'county_fips','stop_hr','fine_grained_location',\
'search_type_raw', 'search_type','police_department','officer_id',"violation","violation_raw","driver_age","driver_age_raw","driver_race_raw"],axis=1)
# cols = ['id', 'driver_gender', 'driver_race', 'search_conducted', 'contraband_found', 'stop_outcome', 'is_arrested', 'stop_duration', 'stop_time_of_day', 'driver_age_category']
# df = df[cols]
df =df.dropna()
df = df.rename(index=str,columns={"stop_time_of_day":"stop_time","driver_age_category":"driver_age","stop_duration":"duration"})
df.to_csv("CT_police_stop.csv")
#Upload to database:
from sqlalchemy import create_engine
data = df.dropna(axis=0,how='any') # Drop any columns with NaN values (since it messes with the combination code)
engine = create_engine("postgresql://summarization:lattice@localhost:5432")
data.to_sql(name='ct_police_stop', con=engine, if_exists = 'replace', index=False)
