package core.process;

import core.entities.Document;
import core.util.FileUtil;

import java.util.*;

public class DataReader
{

	public static void outputArffData(List<Document> comments, String outputFilePath)
	{
		random = new Random();
		// arff declare info
		List<String> lines = new ArrayList<>();
		lines.add("@relation 'CommitMessages'");
		lines.add("");
		lines.add("@attribute Text string");
		lines.add("@attribute class-att {negative,positive}");
		lines.add("");
		lines.add("@data");
		lines.add("");

		for (Document document : comments)
		{
			StringBuilder temp = new StringBuilder("'");

			for (String word : document.getWords())
				temp.append(word).append(" ");

			if (document.getLabel().equals("WITHOUT_CLASSIFICATION"))
				temp.append("',negative");// negative comments
			else
				temp.append("',positive");

			//ATTENZIONE A QUESTO! POTREBBE CREARE UN DISASTRO!
			/*
			if (document.getLabel().equals("WITHOUT_CLASSIFICATION"))
			{
				int random_label = random.nextInt(100);

				if(random_label < 98)
					temp.append("',negative");
				else
					temp.append("',positive");
			}*/

			lines.add(temp.toString());
		}

		FileUtil.writeLinesToFile(lines, outputFilePath);
	}


	public static List<Document> selectProject(List<Document> comments, Set<String> projectName)
	{
		List<Document> res = new ArrayList<>();

		for (Document doc : comments) {
			if (projectName.contains(doc.getProject()))
				res.add(doc);
		}
		return res;
	}


	public static List<Document> readComments(String path)
	{
		List<Document> comments = new ArrayList<>();

		// read comments' content first
		List<String> lines = FileUtil.readLinesFromFile(path);

		for (int i = 0; i < lines.size(); i++)
		{
			String line = lines.get(i);
			if (!line.contains("\"/*"))
			{
				comments.add(new Document(line));
			} else {
				StringBuilder temp = new StringBuilder();
				for (int j = i; j < lines.size(); j++)
				{
					temp.append(lines.get(j));
					if (lines.get(j).contains("*/\""))
					{
						i = j;
						break;
					}
				}
				comments.add(new Document(temp.toString()));
			}
		}

		for (int i = 0; i < lines.size(); i++)
			comments.get(i).setLabel("WITHOUT_CLASSIFICATION");

		// remove duplicate and empty comments
		List<Document> res = new ArrayList<>();
		Set<String> content = new HashSet<>();

		for (Document doc : comments)
		{
			if (doc.getWords().isEmpty() || content.contains(doc.getContent()))
				continue;

			content.add(doc.getContent());
			res.add(doc);
		}

		return res;
	}

	private static Random random;

}
