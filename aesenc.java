import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.compress.*;

public class aesenc extends Configured implements Tool 
{
	static String uname;
        public static String asHex (byte buf[]) 
        {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) 
                {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	/**
   	* A mapper class that encrypts every line.
   	* TODO: Encrypt block by block than line-by-line based on file size
   	*/

       public static class MapClass extends Mapper<Object, Text, Text,Text> 
       {
	    
            private final static IntWritable one = new IntWritable(1);
	    int blockid=1;
	
            private Text word = new Text();
           public void map(Object key, Text value, Context context) throws IOException, InterruptedException 
	{
	  
     	 StringTokenizer itr = new StringTokenizer(value.toString(),"\n");
	FileSplit fspl = (FileSplit)context.getInputSplit();
	uname = fspl.getPath().toString();
	 while (itr.hasMoreTokens()) 
	 {
	try
	 {  
		Configuration conf2 = new Configuration();
		FileSystem dfs = FileSystem.get(conf2);
		Path src = new Path(uname+"/../../key.txt");
		FSDataInputStream in = dfs.open(src);
       		DataInputStream fin = new DataInputStream(in);
		String enkey = fin.readLine();

	  	word.set(itr.nextToken());
		byte[] kb= enkey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(kb, "AES");
		byte[] ivec = "1234567812345678".getBytes();
               	IvParameterSpec encIv = new IvParameterSpec(ivec);

                Cipher cipher = Cipher.getInstance("AES/CTS/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec,encIv);
		
		String v = word.toString();
		byte[] encrypted = cipher.doFinal(v.getBytes());
                String enc = asHex(encrypted);
                Text encr = new Text(enc);
		Text bid = new Text(blockid+"");
		blockid++;
        	context.write(bid,encr);
		fin.close();
	 }
	 catch(NoSuchAlgorithmException e){}
	 catch(InvalidKeyException e){}
 	 catch(IllegalBlockSizeException e){}
        catch(NoSuchPaddingException e){}
        catch(BadPaddingException e){}
        catch(InvalidAlgorithmParameterException e){}
         }
	}
      }

   /**
   * A reducer class that now simply writes the output to HDFS.
   */

     public static class Reduce extends Reducer<Text,Text,Text,Text> 
     {
            private Text result = new Text();

    public void reduce(Text key, Iterable<Text> values, 
                       Context context) throws IOException, InterruptedException 
   {
      String sum = "";
      for (Text val : values) 
	{
        sum = val.toString();
        }
      result.set(sum);
      Text t = new Text();
      context.write(t,result);
    }
  }


         static int printUsage() 
         {
            System.out.println("aesenc [-r <reduces>] <input> <output>");
            ToolRunner.printGenericCommandUsage(System.out);
            return -1;
         }

   public int run(String[] args) throws Exception 
        {
            Configuration conf = new Configuration();
            Job job = new Job(conf, "AES example for hadoop 0.20.1");
            job.setJarByClass(aesenc.class);
            job.setMapperClass(MapClass.class);
	    //job.setCombinerClass(Reduce.class);
            job.setReducerClass(Reduce.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
	  // job.setNumReduceTasks(0);
            List<String> other_args = new ArrayList<String>();
            for(int i=0; i < args.length; ++i)
            {
                try 
                {
                        // The number of map tasks was earlier configurable, 
                        // But with hadoop 0.20.1, it is decided by the framework.
                        // Since this heavily depends on the input data size and how it is being split.
                        if ("-r".equals(args[i])) 
                        {
	                  job.setNumReduceTasks(Integer.parseInt(args[++i]));
                        } 
                        else 
                        {
                          other_args.add(args[i]);
                        }
                } 
                
                    catch (NumberFormatException except) 
                    {
                        System.out.println("ERROR: Integer expected instead of " + args[i]);
                        return printUsage();
                    }
                    catch (ArrayIndexOutOfBoundsException except) 
                    {
                        System.out.println("ERROR: Required parameter missing from " +args[i-1]);
                        return printUsage();
                    }
           }
          // Make sure there are exactly 2 parameters left.
          if (other_args.size() != 2) 
          {
               System.out.println("ERROR: Wrong number of parameters: " + other_args.size() + " instead of 2.");
              return printUsage();
          }
	  FileInputFormat.addInputPath(job, new Path(other_args.get(0)));
          FileOutputFormat.setOutputPath(job, new Path(other_args.get(1)));
	  FileOutputFormat.setCompressOutput(job, true);
          FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class); 

         //submit job and wait for completion. Also show output to user.
         job.waitForCompletion(true);
         return 0;
      }
     
     
        public static void main(String[] args) throws Exception 
        {
                int res = ToolRunner.run(new Configuration(), new aesenc(), args);
		System.exit(res);
        }
}
