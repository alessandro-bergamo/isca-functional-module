package core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class WordSplit
{
	
	/**
	 * input: orginal content of a document
	 * output: a list of words
	 * notice: noisy words are filtered
	 */
	public static List<String> split(String string)
	{
		String tempString = string;
		List<String> words = new ArrayList<>();
		StringTokenizer stringTokenizer = new StringTokenizer(tempString);
		
		while(stringTokenizer.hasMoreTokens())
		{
			String word = stringTokenizer.nextToken();
			word = filter(word);

			if(word.length()<=2 || word.length()>=20) //TODO need a global config
				continue;

			words.add(word);
		}
		
		return words;
	}


	private static String filter(String word)
	{
		String res="";

		for(int i=0;i<word.length();i++)
		{
			if(word.charAt(i) >= 'A' &&word.charAt(i) <= 'Z' || word.charAt(i) >= 'a' && word.charAt(i) <= 'z')
				res = res + word.charAt(i);
		}

		res = res.toLowerCase();

		return res;
	}

}
