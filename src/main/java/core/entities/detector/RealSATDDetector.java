package core.entities.detector;

import core.entities.Commit;
import core.entities.Document;
import core.entities.detector.exceptions.ImpossibleDetection;
import core.entities.detector.exceptions.NotEnoughCommitsFound;
import core.process.DataReader;
import core.util.FileUtil;
import core.util.PorterStemmer;

import org.apache.commons.io.FileUtils;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.Stemmer;
import weka.core.stopwords.WordsFromFile;

import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RealSATDDetector implements SATDDetector
{

    public RealSATDDetector()
    {
        super();

        index_identified = new ArrayList<>();
        commits_identified = new ArrayList<>();
    }


    @Override
    public ArrayList<Commit> detectSATD(List<Commit> commits) throws Exception
    {
        commits_identified.clear();
        index_identified.clear();

        if(commits.size() <= 5)
            throw new NotEnoughCommitsFound();

        final int seed = 1, folds = 5;

        String dataDirectoryPath = System.getProperty("user.home") + File.separator + "IdentifySATD" + File.separator + "data";
        String commentsFilePath = dataDirectoryPath + File.separator + "comments";
        String testDataFilePath = dataDirectoryPath + File.separator + "testData.arff";

        String modelsDirectoryPath = System.getProperty("user.home") + File.separator + "IdentifySATD" + File.separator + "models";
        String stopwordsFilePath = modelsDirectoryPath + File.separator + "stopwords.data";

        File dataFileDirectory = new File(dataDirectoryPath);

        if(!dataFileDirectory.isDirectory())
            dataFileDirectory.mkdirs();
        else {
            if(dataFileDirectory.listFiles().length != 0)
            {
                for(File file : dataFileDirectory.listFiles())
                    file.delete();
            }
        }

        if(dataFileDirectory.isDirectory() && dataFileDirectory.listFiles().length == 0)
        {
            List<String> commit_messages = new ArrayList<>();

            for(Commit commit : commits)
            {
                String commitMessage = commit.getCommitMessage();
                commitMessage = commitMessage.replace("\n", " ").replace("\r", "");

                commit_messages.add("// " + commitMessage);
            }

            FileUtil.writeLinesToFile(commit_messages, commentsFilePath);
            FileUtil.removeBlankLines(commentsFilePath);
        }

        List<Document> comments = DataReader.readComments(commentsFilePath);

        DataReader.outputArffData(comments, testDataFilePath);

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

        Instances commitData = DataSource.read(testDataFilePath);
        stw.setInputFormat(commitData);
        commitData = Filter.useFilter(commitData, stw);
        commitData.setClassIndex(0);

        AttributeSelection attSelection = new AttributeSelection();
        CfsSubsetEval cfsSubsetEval = new CfsSubsetEval();
        BestFirst bestFirst = new BestFirst();

        attSelection.setEvaluator(cfsSubsetEval);
        attSelection.setSearch(bestFirst);
        attSelection.setInputFormat(commitData);

        commitData = Filter.useFilter(commitData, attSelection);

        //randomizing instances
        Random random = new Random(seed);
        Instances randomData = new Instances(commitData);
        randomData.randomize(random);

        //stratification
        if (randomData.classAttribute().isNominal())
            randomData.stratify(folds);

        try {
            index_identified = classifyCommit(randomData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(index_identified.isEmpty())
            return null;
        else {
            for(Integer index : index_identified)
                commits_identified.add(commits.get(index));

            return commits_identified;
        }
    }


    //TODO - AGGIUSTARE TUTTA LA PARTE DI TRAINING DEL CLASSIFICATORE CON LA REGOLA DEL 70%-30% DOVE IL 30% È IL TESTSET. IMPORTANTE!!!

    private List<Integer> classifyCommit(Instances instances) throws Exception
    {
        Classifier classifier = loadOrTrainClassifier();

        for(int I=0; I<instances.numInstances(); I++)
        {
            Instance instance = instances.instance(I);

            double result = classifier.classifyInstance(instance);

            if(result >= 0.1)
                index_identified.add(I);
        }

        return index_identified;
    }


    public Classifier loadOrTrainClassifier() throws Exception
    {
        final int seed = 1, folds = 5;
        Classifier classifier;

        String dataDirectoryPath = System.getProperty("user.home") + File.separator + "IdentifySATD" + File.separator + "data";
        String modelsDirectoryPath = System.getProperty("user.home") + File.separator + "IdentifySATD" + File.separator + "models";
        String classifierFilePath = modelsDirectoryPath + File.separator + "SATDClassifier.model";
        String testDataFilePath = dataDirectoryPath + File.separator + "testData.arff";

        File directory_models = new File(modelsDirectoryPath);

        if(directory_models.isDirectory() && directory_models.listFiles().length == 2)
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
            DataSource dataSource = new DataSource(trainingFile);
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

            //randomizing instances
            Random random = new Random(seed);
            Instances randomData_train = new Instances(trainSet);
            randomData_train.randomize(random);

            //stratification
            if(randomData_train.classAttribute().isNominal())
                randomData_train.stratify(folds);

            //INIZIO DATASET TESTSET

            InputStream testFile = this.getClass().getResourceAsStream("/testData.arff");
            DataSource testdataSource = new DataSource(testFile);
            Instances testSet = testdataSource.getDataSet();
            testSet = Filter.useFilter(testSet, stw);
            testSet.setClassIndex(0);

            testSet = Filter.useFilter(testSet, attSelection);

            //randomizing instances
            Random random_test = new Random(seed);
            Instances randomData_test = new Instances(testSet);
            randomData_test.randomize(random_test);

            //stratification
            if (randomData_test.classAttribute().isNominal())
                randomData_test.stratify(folds);

            //FINE DATASET TESTSET

            //CROSS-VALIDATION
            for(int I=0; I<folds; I++)
            {
                Evaluation evaluation = new Evaluation(randomData_train);

                Instances train = randomData_train.trainCV(folds, I);
                Instances test = randomData_test.trainCV(folds, I);

                SMOTE smote = new SMOTE();
                smote.setInputFormat(train);
                Instances trains_smote = Filter.useFilter(train, smote);

                classifier.buildClassifier(trains_smote);

                evaluation.crossValidateModel(classifier, test, folds, random);

                //PRINT EVALUATION
                System.out.println("\n"+evaluation.toMatrixString("=== CONFUSION MATRIX FOR FOLD "+(I+1)+"/"+folds+" ==="));
                System.out.println("Correct % = "+evaluation.pctCorrect());
                System.out.println("Incorrect % = "+evaluation.pctIncorrect());
                System.out.println("Precision = "+evaluation.precision(1));
                System.out.println("Recall = "+evaluation.recall(1));
                System.out.println("fMeasure = "+evaluation.fMeasure(1));
                System.out.println("Error Rate = "+evaluation.errorRate());
            }

            try {
                if(!directory_models.isDirectory())
                    directory_models.mkdirs();

                SerializationHelper.write(classifierFilePath, classifier);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ImpossibleDetection();
            }
        }

        return classifier;
    }

    private List<Integer> index_identified;
    private ArrayList<Commit> commits_identified;

}

