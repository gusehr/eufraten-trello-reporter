package br.com.tabajara.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Interação com o usuário através do console padrão.
 *
 */
public class Console implements UI {

	private Scanner scanner;

	public Console() {
		this.scanner = new Scanner(System.in);
	}

	public void write(String text) {
		System.out.println(text);
	}

	public String readString(String message) {
		this.write(message);
		String value = this.scanner.next();
		this.scanner.nextLine();
		return value;
	}

	public int readInt(String message) {
		boolean entradaInvalida;
		int value = 0;

		do {
			try {
				this.write(message);
				value = this.scanner.nextInt();
				entradaInvalida = false;
			} catch (Exception ex) {
				this.write("Número inválido.");
				entradaInvalida = true;

				// TODO Estudar sobre ferramentas de LOG!
				// Ex. SLF4J + Log4J
				ex.printStackTrace();
			} finally {
				// Aqui executa SEMPRE!
				this.scanner.nextLine();
			}
		} while (entradaInvalida);
		return value;
	}

	public double readDouble(String message) {
		this.write(message);
		double value = this.scanner.nextDouble();
		this.scanner.nextLine();
		return value;
	}

	public void pause() {
		this.scanner.nextLine();
	}

	public void pause(String message) {
		this.write(message);
		this.pause();
	}

	public void close() {
		// TODO Explicar o fechamento do scanner.
		this.scanner.close();
	}

	@Override
	public Date readDate(String message) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
		Date dateValue = null;
		boolean dataInvalida;
		do {
			String stringValue = this.readString(message);
			try {
				dateValue = simpleDateFormat.parse(stringValue);
				dataInvalida = false;
			} catch (ParseException e) {
				this.write("Data inválida!");
				dataInvalida = true;
			}
		} while (dataInvalida);

		return dateValue;
	}

	@Override
	public int readInt(String... messages) {
		StringBuffer finalMessage = new StringBuffer();
		for (String message : messages) {
			finalMessage.append(message);
			finalMessage.append("\n");
		}
		return readInt(finalMessage.toString());
	}
}
