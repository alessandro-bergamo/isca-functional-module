import core.entities.Commit;
import core.entities.detector.RealSATDDetector;
import core.entities.detector.exceptions.RepositoryNotFound;;
import core.util.RetrieveCommitsLog;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RetrieveCommitsLogTest
{

    private RetrieveCommitsLog retrieveCommitsLog;
    private String repository_url;


    public RetrieveCommitsLogTest()
    {
        retrieveCommitsLog = new RetrieveCommitsLog();
    }


    @Test
    public void retrieveCommitsLogTest_cloneFailed()
    {
        repository_url = "https://github.com/iluwataadasdjrr/java-design-patterns.git";

        assertThrows(RepositoryNotFound.class, () -> retrieveCommitsLog.retrieveCommitsLogs(repository_url));
    }


    @Test
    public void retrieveCommitsLogTest_cloneSuccessfull()
    {
        repository_url = "https://github.com/onecompiler/tutorials.git";

        assertDoesNotThrow(() -> retrieveCommitsLog.retrieveCommitsLogs(repository_url));
    }


    @Test
    public void retrieveCommitsLogTest_regression() throws NoSuchMethodException
    {
        List<Commit> commitList = new ArrayList<>();
        Method method = RetrieveCommitsLog.class.getMethod("retrieveCommitsLogs", String.class);
        Class returnParam = method.getReturnType();

        System.out.println(commitList.getClass().getInterfaces()[0]);
        System.out.println();
        System.out.println(returnParam);

        assertEquals(commitList.getClass().getInterfaces()[0], returnParam);
    }

}
