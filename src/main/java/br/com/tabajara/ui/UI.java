package br.com.tabajara.ui;

import java.util.Date;

public interface UI {

	public void write(String text);

	public String readString(String message);

	public int readInt(String message);

	public double readDouble(String message);

	public Date readDate(String message);

	public int readInt(String... messages);

}
