package main;

import core.entities.Commit;
import core.entities.detector.RealSATDDetector;
import core.entities.detector.exceptions.WrongRepositoryLink;
import core.usecases.identifySATD.IdentifySATDInteractor;
import core.usecases.identifySATD_andShowFiles.IdentifySATDandFiles;
import core.util.RetrieveCommitsLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@SpringBootApplication
@RestController
public class SATDDetectorMain
{

    public SATDDetectorMain()
    {
        retrieveCommitsLog = new RetrieveCommitsLog();
        realSATDDetector = new RealSATDDetector();
    }


    @RequestMapping("/retrieveSATD")
    public List<Commit> retrieveSATD(@RequestParam String repository_url)
    {
        //CHECK THIS LINK FOR THE REGEX https://stackoverflow.com/a/63283134
        if(repository_url.matches("^(([A-Za-z0-9]+@|http(|s)\\:\\/\\/)|(http(|s)\\:\\/\\/[A-Za-z0-9]+@))([A-Za-z0-9.]+(:\\d+)?)(?::|\\/)([\\d\\/\\w.-]+?)(\\.git){1}$"))
        {
            identifySATDInteractor = new IdentifySATDInteractor(retrieveCommitsLog, realSATDDetector, repository_url);
            results = identifySATDInteractor.execute();
        } else
            throw new WrongRepositoryLink();

        return results;
    }

    /*
    @RequestMapping("/retrieveAll")
    public List<Commit> retrieveAll(@RequestParam String repository_url)
    {
        identifySATDandShowFiles = new IdentifySATDandFiles(retrieveCommitsLog, realSATDDetector, repository_url);
        results = identifySATDandShowFiles.execute();

        if(!results.isEmpty())
            return results;

        return null;
    }
    */


    public static void main(String[] args)
    {
        SpringApplication.run(SATDDetectorMain.class, args);
    }



    private final RetrieveCommitsLog retrieveCommitsLog;
    private final RealSATDDetector realSATDDetector;
    private IdentifySATDInteractor identifySATDInteractor;
    private IdentifySATDandFiles identifySATDandShowFiles;
    private List<Commit> results;

}

