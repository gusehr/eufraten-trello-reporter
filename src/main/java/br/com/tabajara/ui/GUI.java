package br.com.tabajara.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class GUI implements UI {

	@Override
	public void write(String text) {
		JOptionPane.showMessageDialog(null, text);
	}

	public void write(String... texts) {
		JOptionPane.showMessageDialog(null, texts);
	}

	@Override
	public String readString(String message) {
		String value = JOptionPane.showInputDialog(message);
		return value;
	}

	@Override
	public int readInt(String... messages) {
		String value = JOptionPane.showInputDialog(messages);
		return Integer.valueOf(value);
	}

	@Override
	public int readInt(String message) {
		String value = JOptionPane.showInputDialog(message);
		return Integer.valueOf(value);
	}

	@Override
	public double readDouble(String message) {
		String value = JOptionPane.showInputDialog(message);
		return Double.valueOf(value);
	}

	@Override
	public Date readDate(String message) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
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

}
