# read-big-file-with-amazon-emr
A case study in reading a big data file using Spark-Scala on AWS Elastic Map Reduce

This is the fourth part in my increasingly innacurately named  trilogy on how I got on reading a big data file 
with C, Python, spark-python and spark-scala. You can see my other articles on this topic 
<a href="https://github.com/taupirho/read-big-file-with-python">here</a>,  
<a href="https://github.com/taupirho/read-big-file-with-spark-python">here</a> and 
<a href="https://github.com/taupirho/read-big-file-with-spark-scala">here</a>.
 
As a reminder, I'm trying to read the same big data file (21 Gbytes) we read before with C, python, spark-python and spark-scala but 
this time using an Amazon AWS Elastic Map Reduce cluster and the spark-scala program from before.
I figured since all previous runs had been on my local PC it would be interesting to see how I got on in a real cluster 
environment in the cloud.

Just to recap, the data file is about 21 Gigabtyes long and holds approximately 335 Million pipe separated records. The first 
10 records are shown below:


```
18511|1|2587198|2004-03-31|0|100000|0|1.97|0.49988|100000||||
18511|2|2587198|2004-06-30|0|160000|0|3.2|0.79669|60000|60|||
18511|3|2587198|2004-09-30|0|160000|0|2.17|0.79279|0|0|||
18511|4|2587198|2004-09-30|0|160000|0|1.72|0.79118|0|0|||
18511|5|2587198|2005-03-31|0|0|0|0|0|-160000|-100|||19
18511|6|2587940|2004-03-31|0|240000|0|0.78|0.27327|240000||||
18511|7|2587940|2004-06-30|0|560000|0|1.59|0.63576|320000|133.33||24|
18511|8|2587940|2004-09-30|0|560000|0|1.13|0.50704|0|0|||
18511|9|2587940|2004-09-30|0|560000|0|0.96|0.50704|0|0|||
18511|10|2587940|2005-03-31|0|0|0|0|0|-560000|-100|||14

```

The second field (period) in the above file can range between 1 and 56 and the goal was to split up the original 
file so that all the records with the same value for the second field would be grouped together in the same file. i.e we 
would end up with 56 separate files, period1.txt, period2.txt ... period56.txt each containing approximately 6 million records.

There were a number of pre-requisites to set up before we could do anything.

1) Create a free tier Amazon AWS account
2) Create an S3 bucket, replicate the "directory" structure from my PC to the bucket and copy over my big data file and 
   my JAR file containing the executable spark-scala code to the S3 bucket.
3) Since I was running this from a Windows PC I also had to create a private key file in order to be able to SSH log on 
   to my cluster master node when the time came.
4) Create the cluster on AWS. This was just the default cluster choice. At the time of writing this was a 3 node m4.large 
   cluster running Spark 2.2.1 on Hadoop 2.8.3 YARN
5) Modify the spark-scala code to enable it to run on the cluster rather than my local PC. This mainly involved changes to 
   the SparkSession call and to file pathnames 

Once the cluster was up and running it was a case of copying the JAR file from S3 to the master node using the aws s3 cp command 
then submitting the following on the master node.

$ spark-submit --class sparkread.test spark-scala.jar

The rest of the interaction is shown below

```
[hadoop@ip-172-31-24-1 ~]$ spark-submit --class spark-read.test spark-scala.jar
Mon Mar 05 14:34:59 UTC 2018
Setting up Spark session
Reading in input file
18/03/05 14:35:28 INFO GPLNativeCodeLoader: Loaded native gpl library
18/03/05 14:35:28 INFO LzoCodec: Successfully loaded & initialized native-lzo library [hadoop-lzo rev cfe28705e7dfdec92539cc7b24fc97936c259a05]
Adding column to DF
Writing out data to files
18/03/05 15:35:23 INFO MultipartUploadOutputStream: close closed:false s3://taupirho/iholding/myfiles/_SUCCESS
Mon Mar 05 15:35:23 UTC 2018
[hadoop@ip-172-31-24-1 ~]
```
The bottom line in all this is that, as you can see from the above output, the job took just under one hour to complete.

As a reminder, my other timings were:-

```
C program on a openVMS Alpha server (Dual 1.33Ghz processors, 32 GB Ram) :- 54 minutes
Python 3.6 program on a Quad 3.4 GHz Intel Core i7-3770 windows 7 PC with 16GB RAM : 18 minutes
Spark-Python 3.5 program on a Quad 3.4 GHz Intel Core i7-3770 windows 7 PC with 16GB RAM : 36 minutes
Spark-Scala 2.1  program on a Quad 3.4 GHz Intel Core i7-3770 windows 7 PC with 16GB RAM : 48 minutes
Visual Studio C++ program on a Quad 3.4 GHz Intel Core i7-3770 windows 7 PC with 16GB RAM : 59 minutes
```




