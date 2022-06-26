package core.usecases.identifySATD;

import core.entities.Commit;
import core.entities.detector.SATDDetector;
import core.entities.detector.exceptions.NotEnoughCommitsFound;
import core.entities.detector.exceptions.RepositoryNotFound;
import core.util.RetrieveCommitsLog;

import java.util.ArrayList;
import java.util.List;

public class IdentifySATDInteractor
{

    public IdentifySATDInteractor(RetrieveCommitsLog retrieveCommitsLog, SATDDetector SATDDetector, String repository_url)
    {
        this.retrieveCommitsLog = retrieveCommitsLog;
        this.SATDDetector = SATDDetector;
        this.repository_url = repository_url;
    }


    public List<Commit> execute()
    {
        try {
            repositoryUrl_commits = retrieveCommitsLog.retrieveCommitsLogs(repository_url);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RepositoryNotFound();
        }

        try {
            commits_identified = (ArrayList<Commit>) SATDDetector.detectSATD(repositoryUrl_commits);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotEnoughCommitsFound();
        }

        return commits_identified;
    }


    private RetrieveCommitsLog retrieveCommitsLog;
    private SATDDetector SATDDetector;

    private ArrayList<Commit> commits_identified;
    private String repository_url;

    private List<Commit> repositoryUrl_commits;

}
