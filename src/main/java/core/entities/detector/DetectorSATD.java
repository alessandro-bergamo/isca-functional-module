package core.entities.detector;

import core.util.PorterStemmer;

import org.apache.commons.io.FileUtils;

import weka.attributeSelection.*;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.*;

import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.WordsFromFile;

import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.supervised.instance.SMOTE;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

public class DetectorSATD
{

    public void buildClassifier() throws Exception
    {
        final int seed = 1, folds = 5;
        Classifier classifier;

        String dataDirectoryPath = System.getProperty("user.home") + File.separator + "IdentifySATD" + File.separator + "data";
        String modelsDirectoryPath = System.getProperty("user.home") + File.separator + "IdentifySATD" + File.separator + "models";
        String classifierFilePath = modelsDirectoryPath + File.separator + "SATDClassifier.model";
        String testDataFilePath = dataDirectoryPath + File.separator + "testData.arff";

        File directory_models = new File(modelsDirectoryPath);

        if(directory_models.isDirectory() && directory_models.listFiles().length == 3)
        {
            classifier = (Classifier) SerializationHelper.read(classifierFilePath);
        } else {
            String stopwordsFilePath = modelsDirectoryPath + File.separator + "stopwords.data";

            StringToWordVector stw = new StringToWordVector(100000);
            stw.setOutputWordCounts(true);
            stw.setIDFTransform(true);
            stw.setTFTransform(true);

            Stemmer stemmer = new PorterStemmer();
            stw.setStemmer(stemmer);

            InputStream stopWordsIS = this.getClass().getResourceAsStream("/stopwords.txt");
            FileUtils.copyInputStreamToFile(stopWordsIS, new File(stopwordsFilePath));
            WordsFromFile stopwords = new WordsFromFile();
            stopwords.setStopwords(new File(stopwordsFilePath));
            stw.setStopwordsHandler(stopwords);

            InputStream trainingFile = this.getClass().getResourceAsStream("/trainingData.arff");
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(trainingFile);
            Instances trainSet = dataSource.getDataSet();
            stw.setInputFormat(trainSet);
            trainSet = Filter.useFilter(trainSet, stw);
            trainSet.setClassIndex(0);

            AttributeSelection attSelection = new AttributeSelection();
            CfsSubsetEval cfsSubsetEval = new CfsSubsetEval();
            BestFirst bestFirst = new BestFirst();

            attSelection.setEvaluator(cfsSubsetEval);
            attSelection.setSearch(bestFirst);
            attSelection.setInputFormat(trainSet);

            trainSet = Filter.useFilter(trainSet, attSelection);

            classifier = new REPTree();

            //randomizing instances$
            Random random = new Random(seed);
            Instances randomData_train = new Instances(trainSet);
            randomData_train.randomize(random);

            //stratification
            if(randomData_train.classAttribute().isNominal())
                randomData_train.stratify(folds);

            //INIZIO DATASET TESTSET

            InputStream testFile = this.getClass().getResourceAsStream("/testData.arff");
            ConverterUtils.DataSource testdataSource = new ConverterUtils.DataSource(testFile);
            Instances testSet = testdataSource.getDataSet();
            testSet = Filter.useFilter(testSet, stw);
            testSet.setClassIndex(0);

            Instances commitData = ConverterUtils.DataSource.read(testDataFilePath);
            commitData = Filter.useFilter(commitData, stw);
            commitData.setClassIndex(0);
            System.out.println("CLASSE DI ATTRIBUTI = "+commitData.classAttribute().toString());

            commitData = Filter.useFilter(commitData, attSelection);

            //randomizing instances
            Random random_test = new Random(seed);
            Instances randomData_test = new Instances(commitData);
            randomData_test.randomize(random_test);

            //stratification
            if (randomData_test.classAttribute().isNominal())
                randomData_test.stratify(folds);

            //FINE DATASET TESTSET

            System.out.println("PRIMA DI CV -> TRAINSET SIZE: "+randomData_train.size()+" TESTSET SIZE: "+randomData_test.size());

            //CROSS-VALIDATION
            for(int I=0; I<folds; I++)
            {
                Evaluation evaluation = new Evaluation(randomData_train);

                Instances train = randomData_train.trainCV(folds, I);
                Instances test = randomData_test.trainCV(folds, I);

                System.out.println("\nDENTRO CV -> TRAINSET SIZE: "+train.size()+" TESTSET SIZE: "+test.size());

                SMOTE smote = new SMOTE();
                smote.setInputFormat(train);
                Instances trains_smote = Filter.useFilter(train, smote);

                classifier.buildClassifier(trains_smote);

                evaluation.crossValidateModel(classifier, test, folds, random);

                //PRINT EVALUATION
                System.out.println();
                System.out.println(evaluation.toMatrixString("=== CONFUSION MATRIX FOR FOLD "+(I+1)+"/"+folds+" ==="));
                System.out.println("Correct % = "+evaluation.pctCorrect());
                System.out.println("Incorrect % = "+evaluation.pctIncorrect());
                //System.out.println("AUC = "+evaluation.areaUnderROC(1));
                //System.out.println("Kappa = "+evaluation.kappa());
                //System.out.println("MAE = "+evaluation.meanAbsoluteError());
                //System.out.println("RMSE = "+evaluation.rootMeanPriorSquaredError());
                //System.out.println("RAE = "+evaluation.relativeAbsoluteError());
                //System.out.println("RRSE = "+evaluation.rootRelativeSquaredError());
                System.out.println("Precision = "+evaluation.precision(1));
                System.out.println("Recall = "+evaluation.recall(1));
                System.out.println("fMeasure = "+evaluation.fMeasure(1));
                System.out.println("Error Rate = "+evaluation.errorRate());
            }
        }
    }


    public static void main(String args[]) throws Exception
    {
        DetectorSATD realSATDDetector = new DetectorSATD();
        realSATDDetector.buildClassifier();

        System.out.println("\n--------- ESECUZIONE TERMINATA ---------");
    }
}
