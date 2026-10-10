import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class YearlyCloseSum {

    // Мапери для кожної пари з урахуванням року
    public static class YearlyCloseMapper extends Mapper<Object, Text, Text, DoubleWritable>{
        
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String csvLine = value.toString();
            String[] csvField = csvLine.split(",");
            
            // Витягуємо рік з дати (перші 4 символи, наприклад "2019")
            String year = csvField[0].substring(0, 4);
            
            // Ціни закриття для трьох пар знаходяться за індексами 5, 12, 19
            double close1 = Double.parseDouble(csvField[5]);
            double close2 = Double.parseDouble(csvField[12]);
            double close3 = Double.parseDouble(csvField[19]);
            
            // Записуємо у форматі: Ключ = "Рік_Пара", Значення = Ціна закриття
            context.write(new Text(year + "_EURUSD"), new DoubleWritable(close1));
            context.write(new Text(year + "_EURGBP"), new DoubleWritable(close2));
            context.write(new Text(year + "_EURCHF"), new DoubleWritable(close3));
        }
    }

    // Редьюсер для підсумовування цін закриття за рік для кожної пари
    public static class YearlyCloseReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable>{
        
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            double sum = 0;
            
            for(DoubleWritable val : values) {
                sum += val.get();
            }
            
            context.write(key, new DoubleWritable(sum));
        }
    }

    public static void main(String... args) throws Exception{
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: YearlyCloseSum <hdfs://> <in> <out>");
            System.exit(2);
        }
        
        FileSystem hdfs = FileSystem.get(new URI(args[0]), conf);
        Path resultFolder = new Path(args[2]);
        if(hdfs.exists(resultFolder))
            hdfs.delete(resultFolder, true);

        Job job = Job.getInstance(conf, "Yearly Close Sum");
        job.setJarByClass(YearlyCloseSum.class);
        job.setMapperClass(YearlyCloseMapper.class);
        job.setCombinerClass(YearlyCloseReducer.class);
        job.setReducerClass(YearlyCloseReducer.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        
        for (int i = 1; i < otherArgs.length - 1; i++) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[(otherArgs.length - 1)]));

        boolean finishState = job.waitForCompletion(true);
        System.out.println("Job Running Time: " + (job.getFinishTime() - job.getStartTime()));

        System.exit(finishState ? 0 : 1);
    }
}