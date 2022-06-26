package core.util;

import core.entities.Commit;
import core.entities.detector.exceptions.ImpossibleDetection;

import core.entities.detector.exceptions.RepositoryNotFound;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class RetrieveCommitsLog
{

    public RetrieveCommitsLog()
    {
        repository = null;
        logCommits = new ArrayList<>();
        cloneDirectoryPath = System.getProperty("user.home") + File.separator + "ChatbotRepositoriesTest";
    }


    private boolean cloneRepository(String repository_url)
    {
        try {
            FileUtils.deleteDirectory(new File(cloneDirectoryPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean repo_directory = new File(cloneDirectoryPath).mkdirs();

        try {
            Git.cloneRepository().setURI(repository_url).setDirectory(Paths.get(cloneDirectoryPath).toFile()).call();
        } catch (GitAPIException e) {
            try {
                FileUtils.deleteDirectory(new File(cloneDirectoryPath));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            throw new RepositoryNotFound();
        }

        return repo_directory;
    }


    public List<Commit> retrieveCommitsLogs(String repository_url)
    {
        if(cloneRepository(repository_url))
        {
            try {
                repository = Git.open(new File(cloneDirectoryPath)).getRepository();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RepositoryNotFound();
            }
        }

        logCommits.clear();

        Git git = new Git(repository);
        Iterable<RevCommit> log;

        //String url = repository.getConfig().getString("remote", "origin", "url");

        try {
            log = git.log().call();
        } catch (Exception e) {
            throw new ImpossibleDetection();
        }

        for(Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();)
        {
            RevCommit rev = iterator.next();

            Commit commit = new Commit (
                    rev.getId().getName(),
                    rev.getCommitterIdent().getName(),
                    rev.getFullMessage(),
                    rev.getAuthorIdent().getWhen()
            );

            /* CODICE DI STAMPA DEI FILE MODIFICATI NEL COMMIT
            System.out.println("\nCOMMIT START ----------------------------"+commit.getCommitID()+" MESSAGE: "+commit.getCommitMessage());

            List<DiffEntry> diffs;
            try (RevWalk rw = new RevWalk(repository))
            {
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);

                df.setRepository(repository);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);

                if (iterator.hasNext()) {
                    RevCommit parent = rw.parseCommit(rev.getParent(0).getId());

                    diffs = df.scan(parent.getTree(), rev.getTree());
                } else {
                    diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), rev.getTree()));
                }
            } catch(Exception e) {
                throw new ImpossibleIdentification();
            }

            for (DiffEntry diff : diffs)
            {
                if(!diff.getChangeType().name().equals("DELETE"))
                    System.out.println("Change Type: " + diff.getChangeType().name() + " -/- File Path: " + diff.getNewPath());
            }

            System.out.println("COMMIT END ----------------------------");
            */

            logCommits.add(commit);
        }

        Collections.reverse(logCommits);

        //repositoryUrl_commits = new AbstractMap.SimpleEntry<>(url, logCommits);

        try {
            FileUtils.deleteDirectory(new File(cloneDirectoryPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logCommits;
    }



    private Repository repository;
    private ArrayList<Commit> logCommits;
    private String cloneDirectoryPath;

}
