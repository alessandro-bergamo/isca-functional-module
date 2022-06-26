package core.entities.detector.exceptions;

public class NotEnoughCommitsFound extends RuntimeException
{

    public NotEnoughCommitsFound()
    {
        this("Not enough commits required for the classification. Make sure you have at least 5 commits in your repository.");
    }


    public NotEnoughCommitsFound(String message)
    {
        super(message);
    }

}
