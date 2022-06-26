package core.entities.detector.exceptions;

public class RepositoryNotFound extends RuntimeException
{

    public RepositoryNotFound()
    {
        this("Repository not found. Make sure the link is correct and try again.");
    }


    public RepositoryNotFound(String message)
    {
        super(message);
    }


}
