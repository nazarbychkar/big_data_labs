forex_history = LOAD 'hdfs://hadoop-master:54310/user/hduser/market/EURUSD_GBP_CHF.csv' USING PigStorage(',') as (
    eurusd_date:chararray, eurusd_time:chararray, eurusd_open:float, eurusd_max:float, eurusd_min:float, eurusd_close:float, eurusd_volume:float, 
    eurgbp_date:chararray, eurgbp_time:chararray, eurgbp_open:float, eurgbp_max:float, eurgbp_min:float, eurgbp_close:float, eurgbp_volume:float, 
    eurchf_date:chararray, eurchf_time:chararray, eurchf_open:float, eurchf_max:float, eurchf_min:float, eurchf_close:float, eurchf_volume:float
);

eurusd_data = FOREACH forex_history GENERATE SUBSTRING(eurusd_date, 0, 4) AS year, eurusd_close AS close;
eurgbp_data = FOREACH forex_history GENERATE SUBSTRING(eurgbp_date, 0, 4) AS year, eurgbp_close AS close;
eurchf_data = FOREACH forex_history GENERATE SUBSTRING(eurchf_date, 0, 4) AS year, eurchf_close AS close;

eurusd_group = GROUP eurusd_data BY year;
eurgbp_group = GROUP eurgbp_data BY year;
eurchf_group = GROUP eurchf_data BY year;

eurusd_sum = FOREACH eurusd_group GENERATE group AS year, 'EURUSD' AS pair, SUM(eurusd_data.close) AS total_close;
eurgbp_sum = FOREACH eurgbp_group GENERATE group AS year, 'EURGBP' AS pair, SUM(eurgbp_data.close) AS total_close;
eurchf_sum = FOREACH eurchf_group GENERATE group AS year, 'EURCHF' AS pair, SUM(eurchf_data.close) AS total_close;

result = UNION eurusd_sum, eurgbp_sum, eurchf_sum;

STORE result INTO 'hdfs://hadoop-master:54310/user/hduser/results/marketCloseSumLab3BychkarNazar' USING PigStorage(',');