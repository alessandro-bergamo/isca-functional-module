package core.entities.detector;

import core.entities.Commit;

import java.util.ArrayList;
import java.util.List;


public interface SATDDetector
{

    ArrayList<Commit> detectSATD(List<Commit> commits) throws Exception;

}
