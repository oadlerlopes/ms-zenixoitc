package br.com.zenix.oitc.utilitaries;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.zenix.oitc.player.gamer.Gamer;

public class SortMapByValue {

	public Map<Gamer, Double> sortByComparator(Map<Gamer, Double> unsortMap, boolean order) {
		List<Entry<Gamer, Double>> list = new LinkedList<Entry<Gamer, Double>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Entry<Gamer, Double>>() {
			public int compare(Entry<Gamer, Double> o1, Entry<Gamer, Double> o2) {
				return order ? o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue());
			}
		});


		Map<Gamer, Double> sortedMap = new LinkedHashMap<Gamer, Double>();
		
		for (Entry<Gamer, Double> entry : list)
			sortedMap.put(entry.getKey(), entry.getValue());

		return sortedMap;
	}
	
}