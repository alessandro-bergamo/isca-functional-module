import main.SATDDetectorMain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class IntegrationTesting
{

    public IntegrationTesting()
    {
        satdDetectorMain = new SATDDetectorMain();

        repository_url = "https://github.com/onecompiler/tutorials.git";
    }


    @Test
    public void retrieveSATD_IntegrationTesting()
    {
        assertDoesNotThrow(() -> satdDetectorMain.retrieveSATD(repository_url));
    }



    private String repository_url;
    private SATDDetectorMain satdDetectorMain;
}
