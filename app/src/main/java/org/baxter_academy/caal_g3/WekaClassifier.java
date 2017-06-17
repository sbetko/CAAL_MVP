package org.baxter_academy.caal_g3;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import static java.lang.Thread.currentThread;


public class WekaClassifier extends IntentService {
    public String arffDataLabeledFilename = "arffDataLabeled";

    public WekaClassifier() {
        super("WekaClassifier");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        /** ASSIGN / LOAD RESOURCES **/
        // assigns classifier
        InputStream classifier = getResources().openRawResource(R.raw.rf_full_csc);

        Classifier cls = null;
        try {
            cls = (Classifier) weka.core.SerializationHelper.read(classifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // assign unlabeled data made by Cleaner class
        Cleaner cleaner = new Cleaner();
        String arffDataFileName = cleaner.arffUnlabeledFilename;
        File path = getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        String arffDataFilePath = path + "/" + arffDataFileName;

        FileInputStream fis;
        BufferedReader read = null;
        InputStreamReader in = null;
        try {
            fis = new FileInputStream(new File(arffDataFilePath));
            in = new InputStreamReader(fis, "UTF-8");
        } catch (FileNotFoundException e1) {
            System.out.println("Error file not found exception:" + e1.getMessage());
            System.exit(0);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error encoding a file reader:" + e.getMessage());
            System.exit(0);
        }

        // load unlabeled data
        Instances unlabeled = null;
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffDataFilePath);
            unlabeled = source.getDataSet();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** PREPARE OPERATIONS **/
        // set class attribute
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

        // create copy
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
        BufferedWriter writer = null;
        new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), arffDataLabeledFilename);

        // opens file for writing
        try {
            writer = new BufferedWriter(
                    new FileWriter(new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), arffDataLabeledFilename)
                    ));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // writes to file
        String toWrite = labeled.toString();
        try {
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
    }

    public void onDestroy() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("FinishedWork");

        intent.putExtra("status", "WekaClassifier finished");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}