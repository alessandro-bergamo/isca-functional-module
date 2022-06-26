import core.entities.Commit;
import core.entities.detector.RealSATDDetector;
import core.entities.detector.exceptions.NotEnoughCommitsFound;

import org.junit.jupiter.api.Test;
import weka.classifiers.trees.REPTree;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RealSATDDetectorTest
{

    public RealSATDDetectorTest()
    {
        realSATDDetector = new RealSATDDetector();

        commits = new ArrayList<>();
        commits.add(new Commit("test1", "test1", "test1", null));
        commits.add(new Commit("test2", "test2", "test2", null));
        commits.add(new Commit("test3", "test3", "test3", null));
        commits.add(new Commit("test4", "test4", "test4", null));
        commits.add(new Commit("test4", "test4", "test4", null));

        commits_2 = new ArrayList<>();
        commits_2.add(new Commit("test1", "test1", "test1", null));
        commits_2.add(new Commit("test2", "test2", "test2", null));
        commits_2.add(new Commit("test3", "test3", "test3", null));
        commits_2.add(new Commit("test4", "test4", "test4", null));
        commits_2.add(new Commit("test5", "test5", "test5", null));
        commits_2.add(new Commit("test6", "test6", "test6", null));
        commits_2.add(new Commit("test7", "test7", "test7", null));
    }


    @Test
    public void detectSATDTest_NotEnoughCommitsFound()
    {
        assertThrows(NotEnoughCommitsFound.class, () -> realSATDDetector.detectSATD(commits));
    }


    @Test
    public void detectSATDTest()
    {
        assertDoesNotThrow(() -> realSATDDetector.detectSATD(commits_2));
    }


    @Test
    public void detectSATDTest_regression() throws NoSuchMethodException
    {
        List<Commit> commitList = new ArrayList<>();
        Method method = RealSATDDetector.class.getMethod("detectSATD", List.class);
        Class returnParam = method.getReturnType();

        System.out.println(commitList.getClass());
        System.out.println();
        System.out.println(returnParam);

        assertEquals(commitList.getClass(), returnParam);
    }


    @Test
    public void loadOrTrainClassifier_regression() throws NoSuchMethodException
    {
        Class classifier = new REPTree().getClass().getSuperclass();
        Method method = RealSATDDetector.class.getMethod("loadOrTrainClassifier");
        Class returnParam = method.getReturnType();

        System.out.println(classifier.getInterfaces()[0]);
        System.out.println("--------------------------------------");
        System.out.println(returnParam);

        assertEquals(classifier.getInterfaces()[0], returnParam);
    }


    private RealSATDDetector realSATDDetector;
    private List<Commit> commits, commits_2;

}
