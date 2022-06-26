package core.entities.detector.exceptions;

public class WrongRepositoryLink extends RuntimeException
{

    public WrongRepositoryLink()
    {
        this("Wrong repository link. Please insert a valid URL.");
    }


    public WrongRepositoryLink(String message)
    {
        super(message);
    }

}
