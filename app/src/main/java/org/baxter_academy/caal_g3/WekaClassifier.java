package org.baxter_academy.caal_g3;

/**
 * Created by Baxter on 1/13/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import weka.core.Instances;

import static org.baxter_academy.caal_g3.R.raw.randomforestbinarycfscsc20;

public class WekaClassifier extends Service {

    @Override
    public void onCreate() {

        /** ASSIGN / LOAD RESOURCES **/
        // assigns classifier
        InputStream classifier = getResources().openRawResource(randomforestbinarycfscsc20);

        WekaClassifier cls = null; // TODO study SerializationHelper
        try {
            cls = (WekaClassifier) weka.core.SerializationHelper.read(classifier);
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

        // load unlabeled data
        Instances unlabeled = null;
        try {
            unlabeled = new Instances(
                    new BufferedReader(reader));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** PREPARE OPERATIONS **/
        // set class attribute
        if (unlabeled != null) {
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
        }

        // create copy
        assert unlabeled != null;
        Instances labeled = new Instances(unlabeled);

        /** PERFORM CLASSIFICATION **/
        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
            assert cls != null;
            //double clsLabel = cls != null ? cls.classifyInstance(unlabeled.instance(i)) : null; // FIXME: 1/18/2017
            //labeled.instance(i).setClassValue(clsLabel);

        }

        /** SAVE LABELED INSTANCE **/
        String FILENAME = "classification";
        //File file = new File(FILENAME);
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

        /** DISPLAY **/
        /** does not work //TODO add TextView for labeled data (for comparison purposes)
         // displays test data
         String line = labeledDataReader.readLine();
         String labeledDataString;
         while (line != null) {
         line = labeledDataReader.readLine();
         labeledDataString += line;
         }
         TextView showTestData = (TextView) findViewById(showTestData);
         showTestData.setText();
         **/

        // displays prediction (deprecated from CAAL_G2)
        //TextView showResults = (TextView) findViewById(R.id.showResults);
        //showResults.setText(toWrite);
    }

    public void onDestroy() {

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}