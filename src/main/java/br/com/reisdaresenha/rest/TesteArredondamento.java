package br.com.reisdaresenha.rest;

public class TesteArredondamento {	
	
	public static void main(String[] args) {
		
		System.out.println("DENTRO DE CASA");	
		
		for (int i = 25; i < 30; i++) {
			
			Double valorArredondarDentrodeCasa = Double.parseDouble(String.valueOf(i));			
			
			System.out.println("O cara Pontuou dentro de casa: "+valorArredondarDentrodeCasa);		
			
			Double restoDaDivisaoPor5 = valorArredondarDentrodeCasa%5;
				
			double valorSomar = 0;
					
			if(restoDaDivisaoPor5 > 0) {			
				for (int j = restoDaDivisaoPor5.intValue(); j < 5; j++) {
					valorSomar++;
				}
			}
			
			Double pontuacaoFinal = valorArredondarDentrodeCasa+valorSomar;
			
			System.out.println("Pontuacao Final Dentro de Casa: "+pontuacaoFinal+"\n");
		}	
		
		System.out.println("---");
		System.out.println("---");
		System.out.println("FORA DE CASA");		
		
		for (int i = 25; i < 30; i++) {
			
			Double valorArredondarForadeCasa = Double.parseDouble(String.valueOf(i));			
			
			System.out.println("O cara Pontuou fora de casa: "+valorArredondarForadeCasa);		
			
			Double restoDaDivisaoPor5 = valorArredondarForadeCasa%5;
				
			double valorDiminuir = 0;
					
			if(restoDaDivisaoPor5 > 0) {			
				for (int j = 0; j < restoDaDivisaoPor5.intValue(); j++) {
					valorDiminuir++;
				}
			}
			
			Double pontuacaoFinal = valorArredondarForadeCasa-valorDiminuir;
			
			System.out.println("Pontuacao Final Fora de Casa: "+pontuacaoFinal+"\n");
		}	
		
		
	}
}


