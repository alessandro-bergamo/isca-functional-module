import core.entities.Commit;
import core.entities.detector.exceptions.WrongRepositoryLink;
import core.usecases.identifySATD.IdentifySATDInteractor;
import main.SATDDetectorMain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SATDDetectorMainTest
{

    private SATDDetectorMain satdDetectorMain;
    private List<Commit> commits_identified;
    private String repository_url = "https://github.com/iluwatar/java-design-patterns.git";

    private IdentifySATDInteractor identifySATDInteractor;

    public SATDDetectorMainTest()
    {
        satdDetectorMain = new SATDDetectorMain();

        identifySATDInteractor = mock(IdentifySATDInteractor.class);

        commits_identified = new ArrayList<>();
        commits_identified.add(new Commit("test1", "test1", "test1", null));
        commits_identified.add(new Commit("test2", "test2", "test2", null));
        commits_identified.add(new Commit("test3", "test3", "test3", null));
        commits_identified.add(new Commit("test4", "test4", "test4", null));
        commits_identified.add(new Commit("test5", "test5", "test5", null));
        commits_identified.add(new Commit("test6", "test6", "test6", null));
    }

    @Test
    public void retrieveSATDTest_correctURLFormat()
    {
        doReturn(commits_identified).when(identifySATDInteractor).execute();

        assertDoesNotThrow(() -> satdDetectorMain.retrieveSATD(repository_url));
    }


    @Test
    public void retrieveSATDTest_wrongURLFormat()
    {
        String repository_url = "https://stackoverflow.com/";

        assertThrows(WrongRepositoryLink.class, () -> satdDetectorMain.retrieveSATD(repository_url));
    }

}
