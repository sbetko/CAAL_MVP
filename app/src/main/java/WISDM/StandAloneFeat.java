package WISDM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

/**
 * WISDM project research program
 * http://storm.cis.fordham.edu/~gweiss/wisdm
 *
 * This class takes raw data from the client app and outputs an .arff file suitable
 * for weka interpretation.
 *
 * @author Jeff Lockhart <a href="mailto:lockhart@cis.fordham.edu">lockhart@cis.fordham.edu</a>
 * @author Jess Timko
 * @version 4.0
 * @date 7 July 2014
 *
 */
public class StandAloneFeat {

	/**
	 * a threadsafe queue for SplitResults objects added to by TupleSorter and taken from by FeatGen
	 */
	private static LinkedList<TupFeat> que = new LinkedList<TupFeat>();

	private static String[] usrList = null;

	private static String[] actList = null;

	private static int usrCount = 0;


	// windowSize = number of seconds for window frame
	private static int windowSize = 10;

	//samplingRate = Hz (number of samples collected per second)
	//currently use 20 Hz sampling rate
	private static int samplingRate = 20;

	private static BufferedWriter outp = null;

	// windowSize*20 entries is this much change in timestamps
	private static long duration = windowSize*1000;

	/**
	 * holds the boundaries used in binning. values settable in main
	 */
	private static double[] bins = new double[30];

	/**
	 * @param args
	 * args[0] should be the name of the file to read
	 * args[1] should be the name of the file to write
	 */
	public static void main(String[] args) {
		bins[0] = -2.5;
		bins[1] = 0;
		bins[2] = 2.5;
		bins[3] = 5;
		bins[4] = 7.5;
		bins[5] = 10;
		bins[6] = 12.5;
		bins[7] = 15.0;
		bins[8] = 17.5;
		bins[9] = 20;
		bins[10] = -2.5;
		bins[11] = 0;
		bins[12] = 2.5;
		bins[13] = 5;
		bins[14] = 7.5;
		bins[15] = 10;
		bins[26] = 12.5;
		bins[17] = 15.0;
		bins[18] = 17.5;
		bins[19] = 20;
		bins[20] = -2.5;
		bins[21] = 0;
		bins[22] = 2.5;
		bins[23] = 5;
		bins[24] = 7.5;
		bins[25] = 10;
		bins[26] = 12.5;
		bins[27] = 15.0;
		bins[28] = 17.5;
		bins[29] = 20;

		FileInputStream fis;
		BufferedReader read = null;
		try {
			fis = new FileInputStream(args[0]);
			InputStreamReader in = new InputStreamReader(fis, "UTF-8");
			read = new BufferedReader(in);
		} catch (FileNotFoundException e1) {
			System.out.println("Error file not found exception:" + e1.getMessage());
			System.exit(0);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error encodinging a file reader:" + e.getMessage());
			System.exit(0);
		}
		usrList = new String[50];
		actList = new String[]{"NoLabel", "Walking", "Jogging", "Stairs",
				"Sitting", "Standing", "LyingDown"};
		if(args.length > 2){
			windowSize = Integer.parseInt(args[2]);
			duration = windowSize*1000;
		}

		try {
			readFile(read);
		} catch (IOException e) {
			System.out.println("Error reading file. Operation aborted." + e.getMessage());
			System.exit(0);
		}

		TupFeat tmp = null;
		for(int i = 0; i < que.size(); i++){
			tmp = que.get(i);
			FeatureLib.processTup(tmp, bins);

		}

		writeArff(args[1]);
	}

	/**
	 * prints the data to an arff file
	 * @param n
	 */
	private static void writeArff(String n) {
		// Establish the file connection
		outp = getFileConn(n);
		System.out.println("writing arff function entered ");
		writeArffHeader();
		writeData();

		System.out.println("Output written to file " + n + ".");
	}

	/**
	 * prints the data in the result set to the file
//	 * @param rs the result set
//	 * @param rsMeta the meta data for the result set
	 */
	private static void writeData() {
		System.out.println("write data function entered ");
		// Temporary tuple variables
		String tuple = "";
		TupFeat tup = null;
		float[] f = null;
		int c = 0;
		// Go through the entire result set
		try {
			System.out.println("write data try block entered ");

			while (!que.isEmpty())
			{
				/** old function
				tup = que.pop();
				f = tup.getFeat();
				tuple = "";
				tuple += c++;
				tuple += ",";
				char tmp = tup.getAct();

				tuple += tmp;
				tuple += ",";
				for (int i = 0; i < 43; i++){ // the data itself //fixme 43 is hardcoded feature count
					tuple += f[i];
					tuple += ",";
				}
				tuple += tup.getUsr(); // column 2 is userid

				outp.write(tuple + "\n");
				tuple = null;
				**/

			//new function
				tup = que.pop();
				f = tup.getFeat();
				tuple = "";
                tuple += "1,1,";

				for (int i = 0; i < 43; i++){ // the data itself //fixme 43 is hardcoded feature count
					tuple += f[i];
					tuple += ",";
				}

				tuple += "?"; // column 2 is userid

				outp.write(tuple + "\n");
				tuple = null;
			}
			outp.flush();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage() + "data not written");
			System.exit(0);
		}
	}

	/**
	 * writes the Arff Header to the out file for weka processing.
	 * currently these are inaccurate, but it rarely matters since
	 * they are overwritten in the headless arff process anyway.
	 */
	private static void writeArffHeader() {

		System.out.println("write arff function entered ");

		try {

			System.out.println("write arff header try block entered ");

			outp.write("@relation activity_recognition_unlabeled \n\n" +
					"@attribute \"UNIQUE_ID\" numeric \n" +
					"@attribute \"user\" {\"33\" , \"17\", \"29\", \"13\" , \"20\" , \"27\" , \"6\" , \"15\" , \"32\" , \"36\" , \"18\" , \"35\" , \"11\" , \"16\" , \"5\", \"10\" , \"28\" , \"26\" , \"14\" , \"24\" , \"12\" , \"23\" , \"4\" , \"30\" , \"34\" , \"8\" , \"31\" , \"21\" , \"3\" , \"22\" , \"1\" , \"25\" , \"9\" , \"2\" , \"7\" , \"19\"}");

			//outp.write("Sitting\", \"Active\"");

			outp.write( "\n" +
					"@attribute \"X0\" numeric \n"+
					"@attribute \"X1\" numeric \n"+
					"@attribute \"X2\" numeric \n"+
					"@attribute \"X3\" numeric \n"+
					"@attribute \"X4\" numeric \n"+
					"@attribute \"X5\" numeric \n"+
					"@attribute \"X6\" numeric \n"+
					"@attribute \"X7\" numeric \n"+
					"@attribute \"X8\" numeric \n"+
					"@attribute \"X9\" numeric \n"+
					"@attribute \"Y0\" numeric \n"+
					"@attribute \"Y1\" numeric \n"+
					"@attribute \"Y2\" numeric \n"+
					"@attribute \"Y3\" numeric \n"+
					"@attribute \"Y4\" numeric \n"+
					"@attribute \"Y5\" numeric \n"+
					"@attribute \"Y6\" numeric \n"+
					"@attribute \"Y7\" numeric \n"+
					"@attribute \"Y8\" numeric \n"+
					"@attribute \"Y9\" numeric \n"+
					"@attribute \"Z0\" numeric \n"+
					"@attribute \"Z1\" numeric \n"+
					"@attribute \"Z2\" numeric \n"+
					"@attribute \"Z3\" numeric \n"+
					"@attribute \"Z4\" numeric \n"+
					"@attribute \"Z5\" numeric \n"+
					"@attribute \"Z6\" numeric \n"+
					"@attribute \"Z7\" numeric \n"+
					"@attribute \"Z8\" numeric \n"+
					"@attribute \"Z9\" numeric \n"+
					"@attribute \"XAVG\" numeric \n"+
					"@attribute \"YAVG\" numeric \n"+
					"@attribute \"ZAVG\" numeric \n"+
					"@attribute \"XPEAK\" numeric \n"+
					"@attribute \"YPEAK\" numeric \n"+
					"@attribute \"ZPEAK\" numeric \n"+
					"@attribute \"XABSOLDEV\" numeric \n"+
					"@attribute \"YABSOLDEV\" numeric \n"+
					"@attribute \"ZABSOLDEV\" numeric \n"+
					"@attribute \"XSTANDDEV\" numeric \n"+
					"@attribute \"YSTANDDEV\" numeric \n"+
					"@attribute \"ZSTANDDEV\" numeric \n"+
					"@attribute \"RESULTANT\" numeric \n"+
					"@attribute class { \"Walking\" , \"Jogging\" , \"Upstairs\" , \"Downstairs\" , \"Sitting\" , \"Standing\" }");
			//outp.write("\"" + usrList[0] + "\" "); //prints first user, not preceded by comma
			//if (usrCount > 1){ //prints second, third, etc. users with commas first
			//	for (int j = 1; j < usrCount; j++){
			//		outp.write(", \"" + usrList[j] + "\"");
			//	}
			//}

			outp.write("\n\n@data\n");

			outp.flush();
		} catch (IOException e) {
			System.out.println("Error writing arff header: " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Gets a buffered writer so that we can write a file to the hard disk.
	 *@param fileName the name of the file to write to disk
	 *@return a BufferedWriter that points to fileName
	 */
	private static BufferedWriter getFileConn(String fileName)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(fileName));
		}

		catch (IOException e)
		{
			System.out.println("Error making arff file: " + e.getMessage());
			System.exit(0);
		}

		return bw;

	} //end getFileConn

	/**
	 * this function is absurdly long.
	 * @param read
	 * @throws IOException
	 */
	private static void readFile(BufferedReader read) throws IOException{

		System.out.println("Started reading file ");

		boolean more = true; // more in the file?
		String cusr = "1"; // user of current tuple, must include integer for Long.valueOf
		String cact = null; // activity of current tuple
		float[] x = new float[(windowSize*samplingRate)]; // holds the accelerometer data for a single tuple
		float[] y = new float[(windowSize*samplingRate)];
		float[] z = new float[(windowSize*samplingRate)];
		String tmpLn = null, tmpLna = null, lastLn = "fakeLine";
		long cTime = 0, tmpt = 1, lastTime = 0; // time of start of current tuple, and temp time //fixme cTime does not get updated to start time of tuple
		long[] t = new long[(windowSize*samplingRate)];
		int i = 0; // counter for tuple members
		int abCount = 0; //abandoned tuple count
		int savTCount = 0; //saved tuple count
		int repCount = 0;

		while(more){

			tmpLna = read.readLine();

			try{

				if(tmpLna == null){
					System.out.println("Finished reading file ");
					more = false;
					break;

				} else{
					tmpLn = tmpLna.replace(';', ',');
					String[] values = tmpLn.split(",");

					if (i == 0) {
						cTime = Long.parseLong(values[2]);
					}

					if(!cusr.equals(values[0])){ // if the user changes
						System.out.println("current user is " + values[0] + " starting new tuple ");

					} else { // if the activity and the user are both the same still
						// sets tmpt to the time of the current line
						tmpt = Long.parseLong(values[2]);

						// make sure it's not a repeat or null line, and also check that it's within 10 seconds of tuple start
						if(tmpt <= (cTime + duration)){
							if (tmpt != lastTime && tmpt != 0){
								// extract the floating point number from the string we read from the file
								// store it as an x, y, or z value
								x[i] = Float.valueOf(values[3].trim()).floatValue();
								y[i] = Float.valueOf(values[4].trim()).floatValue();
								z[i] = Float.valueOf(values[5].trim()).floatValue();
								t[i] = tmpt;
								lastTime = tmpt;
								i++;

							}
						} else if(i >= (windowSize*0.9*samplingRate)){
							savTCount++;
							TupFeat tup = new TupFeat(Long.valueOf(cusr), cact.charAt(0), cTime);
							tup.setRaw(x, y, z, t);
							tup.setCount(i);
							cTime = Long.parseLong(values[2]);
							que.add(tup);
							i = 0; //reset count
						}
						System.out.println(i);
						if(i == (windowSize*samplingRate)){ // if we reach (windowSize*samplingRate) samples, then the windowSize tuple is done and should be saved
							savTCount++;

							TupFeat ttup = new TupFeat(1, '\u0031', cTime); //fixme replaced cusr w/ 1 (for long requirement)
							ttup.setCount(i);

							// all arrays must be copied into new ones because java is pass by reference always
							float[] xt = new float[(windowSize*samplingRate)], yt = new float[(windowSize*samplingRate)], zt = new float[(windowSize*samplingRate)];
							long[] tt = new long[(windowSize*samplingRate)];
							for(int j = 0; j<(windowSize*samplingRate); j++){
								xt[j] = x[j];
								yt[j] = y[j];
								zt[j] = z[j];
								tt[j] = t[j];
							}

							ttup.setRaw(xt, yt, zt, tt);
							que.add(ttup);

							cTime = Long.parseLong(values[2]); // set time to begin next tuple
							i = 0; // reset count
						} // end if
					} // end else
				} // end else
			} catch(ArrayIndexOutOfBoundsException a){
				System.out.println("bad line found");
				continue;
			}
		} // end while

		System.out.println("Abandoned tuple count = " + abCount);
		System.out.println("saved tuple count = " + savTCount);
		System.out.println("Repeaded lines: " + repCount);

	} // end function readfile

} // end class