package br.com.reisdaresenha.rest;

import java.util.ArrayList;
import java.util.List;

public class _TesteGerarTabela {

	public static void main(String[] args) {
				
		List<String> clubes = new ArrayList<String>();
		System.out.println("Entre com o nome dos clubes. Deixe em branco para terminar.");
			
		clubes.add("TIME 1");
		clubes.add("TIME 2");
		clubes.add("TIME 3");
		clubes.add("TIME 4");
		clubes.add("TIME 5");
		clubes.add("TIME 6");
		clubes.add("TIME 7");
		clubes.add("TIME 8");
		clubes.add("TIME 9");
		clubes.add("TIME 10");
		clubes.add("TIME 11");
		clubes.add("TIME 12");
		clubes.add("TIME 13");
		clubes.add("TIME 14");
		clubes.add("TIME 15");
		clubes.add("TIME 16");		

		if (clubes.size() % 2 == 1) {
			clubes.add(0, "");
		}

		int t = clubes.size();
		int m = clubes.size() / 2;
		
		for (int i = 0; i < t - 1; i++) {
			System.out.print((i + 1) + "a rodada: ");
			for (int j = 0; j < m; j++) {				
				// Teste para ajustar o mando de campo
				if (j % 2 == 1 || i % 2 == 1 && j == 0) {
					System.out.print(clubes.get(t - j - 1) + " x " + clubes.get(j) + "   ");
				} else {
					System.out.print(clubes.get(j) + " x " + clubes.get(t - j - 1) + "   ");
				}
			}
			
			System.out.println();
			
			// Gira os clubes no sentido horário, mantendo o primeiro no lugar
			clubes.add(1, clubes.remove(clubes.size() - 1));
			
		}
		
	}
}
