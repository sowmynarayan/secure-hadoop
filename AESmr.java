import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AESmr extends Configured implements Tool {
	public static String asHex (byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

  public static class MapClass extends Mapper<Object, Text, LongWritable, Text> {

    private final static IntWritable one = new IntWritable(1);
    //  private Text word = new Text();
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException,NoSuchAlgorithmException,InvalidKeyException,IllegalBlockSizeException,NoSuchPaddingException,BadPaddingException
{
      String line = value.toString();
	//GetBytes getKey = new GetBytes("key1.txt", 16);
/*	File f = new File("hel.txt");
	FileInputStream fin = new FileInputStream(f);
  	StringBuffer message = new StringBuffer();
	int ch;
	while( (ch = fin.read()) != -1)
		message.append((char)ch);
     
	String msg = message.toString();
*/
	byte[] kb="1234567812345678".getBytes();
	SecretKeySpec skeySpec = new SecretKeySpec(kb, "AES");
	// Instantiate the cipher
	Cipher cipher = Cipher.getInstance("AES");
	cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	byte[] encrypted =
	cipher.doFinal(line.getBytes());
	String enc = asHex(encrypted);
	LongWritable line1 = new LongWritable(1);
	Text enc1 = new Text(enc);
	context.write(line1,enc1);	

     /* StringTokenizer itr = new StringTokenizer(line);
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
	}*/
    }
  }

  /**
   * A reducer class that just emits the sum of the input values.
   */
  public static class Reduce extends Reducer<LongWritable, Text, LongWritable, Text> {

    public void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	// String ciph = "";
	StringBuilder valueBuilder = new StringBuilder();
	 
        for (Text val : values) 
            valueBuilder.append(val);     

     context.write(key, new Text(valueBuilder.substring(0, valueBuilder.length() - 1)));
     valueBuilder.setLength(0);
    }
  }

  static int printUsage() {
    System.out.println("aesmr [-r <reduces>] <input> <output>");
    ToolRunner.printGenericCommandUsage(System.out);
    return -1;
  }

  public int run(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = new Job(conf, "AES example for hadoop 0.20.1");

    job.setJarByClass(AESmr.class);
    job.setMapperClass(MapClass.class);
    job.setCombinerClass(Reduce.class);
    job.setReducerClass(Reduce.class);


    job.setOutputKeyClass(LongWritable.class);

    job.setOutputValueClass(Text.class);


    List<String> other_args = new ArrayList<String>();
    for(int i=0; i < args.length; ++i) {
      try {
        // The number of map tasks was earlier configurable, 
        // But with hadoop 0.20.1, it is decided by the framework.
        // Since this heavily depends on the input data size and how it is being split.
        if ("-r".equals(args[i])) {
          job.setNumReduceTasks(Integer.parseInt(args[++i]));
        } else {
          other_args.add(args[i]);
        }
      } catch (NumberFormatException except) {
        System.out.println("ERROR: Integer expected instead of " + args[i]);
        return printUsage();
      } catch (ArrayIndexOutOfBoundsException except) {
        System.out.println("ERROR: Required parameter missing from " +
            args[i-1]);
        return printUsage();
      }
    }
    // Make sure there are exactly 2 parameters left.
    if (other_args.size() != 2) {
      System.out.println("ERROR: Wrong number of parameters: " + other_args.size() + " instead of 2.");
      return printUsage();
    }
    FileInputFormat.addInputPath(job, new Path(other_args.get(0)));
    FileOutputFormat.setOutputPath(job, new Path(other_args.get(1)));

    //submit job and wait for completion. Also show output to user.
    job.waitForCompletion(true);
    return 0;
  }
  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new AESmr(), args);
    System.exit(res);
  }
}


