package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.baxter_academy.caal_g3.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class WekaClassifier extends Service {

    @Override
    public void onCreate() {
        System.out.println("Started WekaCleaner");
        super.onCreate();

        /** ASSIGN / LOAD RESOURCES **/
        // assigns classifier
        InputStream classifier = getResources().openRawResource(R.raw.randomforestbinarycfscsc20); //TODO

        Classifier cls = null; // TODO study SerializationHelper
        try {
            cls = (Classifier) weka.core.SerializationHelper.read(classifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** Does not work
         // assign and load labeled data
         InputStreamReader labeledData = new InputStreamReader(
         getResources().openRawResource(R.raw.testlabeledbinarycfs)
         );
         BufferedReader labeledDataReader = new BufferedReader(labeledData);
         **/
        // assign unlabeled data
        Reader reader = new InputStreamReader(
                getResources().openRawResource(R.raw.testunlabeledbinarycfs)
        );

        // load unlabeled data in Instance object
        Instances unlabeled = null;
        try {
            unlabeled = new Instances(
                    new BufferedReader(reader));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** PREPARE OPERATIONS **/
        // set class attributes
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

        // create copy of UNLABELED instance to be LABELED
        Instances labeled = new Instances(unlabeled);

        /** PERFORM CLASSIFICATION **/
        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = 0;
            try {
                clsLabel = cls.classifyInstance(unlabeled.instance(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
            labeled.instance(i).setClassValue(clsLabel);
        }

        /** SAVE LABELED INSTANCE **/
        String FILENAME = "classification";
        BufferedWriter writer = null;


        // opens file for writing
        try {
            writer = new BufferedWriter(
                    new FileWriter(new File(getFilesDir(), FILENAME)
                    ));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // writes to file
        String toWrite = labeled.toString();
        try {
            System.out.println(labeled); // debug
            if (writer != null) {
                writer.write(toWrite);
                writer.newLine();
                writer.flush();
                writer.close();
                System.out.println("file reference is not null");
            } else {
                System.out.println("file reference is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** FINISH **/
        stopSelf();

    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */

    public void onDestroy() {
        System.out.println("Stopped WekeClassifier");
        Intent schedulerIntent = new Intent(this.getBaseContext(), Scheduler.class);
        startService(schedulerIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}