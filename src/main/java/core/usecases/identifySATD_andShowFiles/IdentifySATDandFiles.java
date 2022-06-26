package core.usecases.identifySATD_andShowFiles;

import core.entities.Commit;
import core.entities.detector.SATDDetector;
import core.util.RetrieveCommitsLog;

import java.util.ArrayList;
import java.util.List;

public class IdentifySATDandFiles
{

    public IdentifySATDandFiles(RetrieveCommitsLog retrieveCommitsLog, SATDDetector SATDDetector, String repository_url)
    {
        this.retrieveCommitsLog = retrieveCommitsLog;
        this.SATDDetector = SATDDetector;
        this.repository_url = repository_url;
    }


    public List<Commit> execute()
    {
        try {
            List<Commit> repositoryUrl_commits = retrieveCommitsLog.retrieveCommitsLogs(repository_url);

            commits_identified = (ArrayList<Commit>) SATDDetector.detectSATD(repositoryUrl_commits);

            return commits_identified;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    private final RetrieveCommitsLog retrieveCommitsLog;
    private ArrayList<Commit> commits_identified;
    private final SATDDetector SATDDetector;
    private final String repository_url;

}
