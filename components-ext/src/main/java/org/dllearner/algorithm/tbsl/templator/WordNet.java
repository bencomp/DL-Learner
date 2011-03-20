package org.dllearner.algorithm.tbsl.templator;

import java.util.ArrayList;
import java.util.List;

import edu.smu.tspell.wordnet.*;

public class WordNet {

	public String path;
	public WordNetDatabase database;
	
	public WordNet(String s) {
		path = s;
		
	}
	public WordNet() {
		path = "src/main/resources/tbsl/dictionary/";
	}
	
	public void setWordNetPath(String s) {
		path = s;
	}	
	
	public void init() {	
		System.setProperty("wordnet.database.dir",path);
		database = WordNetDatabase.getFileInstance();
	}
	

	public List<String> getBestSynonyms(String s) {
		
		List<String> synonyms = new ArrayList<String>();
		
		Synset[] synsets = database.getSynsets(s);
		if (synsets.length != 0) {
			String[] candidates = synsets[0].getWordForms();
			for (String c : candidates) {
				if (!c.equals(s) && !c.contains(" ") && synonyms.size() < 4) {
					synonyms.add(c);
				}
			}
		}
		return synonyms;
	}
	
	public List<String> getAttributes(String s) {
		
		List<String> result = new ArrayList<String>();
		
		Synset synset = database.getSynsets(s)[0];
		if (synset.getType().equals(SynsetType.ADJECTIVE)) {
			NounSynset[] attributes = ((AdjectiveSynset) synset).getAttributes();
			for (int i = 0; i < attributes.length; i++) {
				result.add(attributes[i].getWordForms()[0]);
			}
		}
		
		return result;
	}
	
}