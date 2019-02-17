echo 'hi'

for month in `seq 1 12`;do wget --content-disposition "http://climate.weather.gc.ca/climate_data/bulk_data_e.html?format=csv&stationID=27211&Year=2016&Month=${month}&Day=14&timeframe=1&submit= Download+Data" ;done

echo 'done'
