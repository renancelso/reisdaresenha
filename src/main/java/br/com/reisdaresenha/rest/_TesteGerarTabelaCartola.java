package br.com.reisdaresenha.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Renan Celso, em 19/08/2020
 */
public class _TesteGerarTabelaCartola {

	public static void main(String[] args) {
				
		List<String> times = new ArrayList<String>();			
		
		Random gerador = new Random();	
		
		while(times.size() < 16) {
			Long numero = Long.parseLong(String.valueOf(gerador.nextInt(72)));
			if(numero > 0) {
				times.add("Time: "+numero);
			}
		}
		
		if (times.size() % 2 == 1) {
			times.add(0, "");
		}

		int t = times.size();
		int m = times.size() / 2;
				
		for (int i = 0; i < 1; i++) {			
			
			for (int j = 0; j < m; j++) {	
				if (j % 2 == 1 || i % 2 == 1 && j == 0) {
					System.out.print(times.get(t - j - 1) + " x " + times.get(j) + "\n");
				} else {
					System.out.print(times.get(j) + " x " + times.get(t - j - 1) + "\n");
				}
			}
			
			System.out.println("\n");
			
			times.add(1, times.remove(times.size() - 1));
			
		}
		
	}
}
