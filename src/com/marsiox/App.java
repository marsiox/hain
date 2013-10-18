package com.marsiox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {

	public static void main(String[] args) {
    /*		
		for (String s: args) {
            System.out.println(s);
        }
	*/	
		System.out.print("URL: ");
		
		String urlString = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			urlString = br.readLine();
		} catch (IOException e) {
			System.out.println("IO error");
			System.exit(1);
		}

		Redirection redirection = new Redirection();
		if (urlString.length() > 0) {
			redirection.getRedirectionUrl(urlString);
		}
		else {
			System.out.println("Empty URL");
		}
		System.exit(0);
	}
}