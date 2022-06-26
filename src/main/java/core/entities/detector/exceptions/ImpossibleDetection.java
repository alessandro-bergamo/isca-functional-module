package core.entities.detector.exceptions;

public class ImpossibleDetection extends RuntimeException
{

    public ImpossibleDetection()
    {
        this("There was an error. Impossible to pursue the detection.");
    }


    public ImpossibleDetection(String message)
    {
        super(message);
    }

}
