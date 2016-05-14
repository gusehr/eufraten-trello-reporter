package org.eufraten.trelloreporter.xls;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseXLS {
	private final static Logger LOGGER = LoggerFactory.getLogger(BaseXLS.class);

	private Workbook wb;
	private Sheet sheet;

	private int currentRowIndex = 0;
	private int lastCreatedColumIndex = -1;

	private CellStyle defaultStyle;
	private CellStyle labelStyle;
	private CellStyle dataStyle;
	private CellStyle titleStyle;

	private String title;

	public BaseXLS(String title) {
		this.title = title;
		this.init();
	}

	private void init() {
		this.wb = new HSSFWorkbook();
		this.sheet = wb.createSheet();
		this.sheet.setDefaultRowHeightInPoints((short) 15);
		this.currentRowIndex = 0;
		this.lastCreatedColumIndex = -1;

		this.createCellStyles();
		this.createHeader();
	}

	private void createCellStyles() {
		defaultStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 12);
		defaultStyle.setFont(font);
		defaultStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);

		labelStyle = wb.createCellStyle();
		labelStyle.cloneStyleFrom(defaultStyle);
		labelStyle.setAlignment(CellStyle.ALIGN_RIGHT);

		dataStyle = wb.createCellStyle();
		dataStyle.cloneStyleFrom(defaultStyle);
		font = wb.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		dataStyle.setFont(font);

		titleStyle = wb.createCellStyle();
		titleStyle.cloneStyleFrom(defaultStyle);
		font = wb.createFont();
		font.setFontHeightInPoints((short) 15);
		font.setBold(true);
		titleStyle.setFont(font);
	}

	private void createHeader() {
		this.currentRowIndex = 1;

		try (InputStream resourceIn = getClass().getClassLoader().getResourceAsStream("logo-eufraten.png")) {
			byte[] imageBytes = IOUtils.toByteArray(resourceIn);
			int pictureureIdx = wb.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);

			CreationHelper helper = wb.getCreationHelper();
			Drawing drawing = sheet.createDrawingPatriarch();
			ClientAnchor anchor = helper.createClientAnchor();

			anchor.setCol1(1);
			anchor.setRow1(currentRowIndex);
			anchor.setDx1(150);

			Picture picture = drawing.createPicture(anchor, pictureureIdx);
			picture.resize();
		} catch (IOException e) {
			LOGGER.warn("Erro ao gerar a imagem do cabecalho do relatorio", e);
		}

		this.currentRowIndex += 2;

		Row titleRow = sheet.createRow(currentRowIndex);
		Cell titleCell = titleRow.createCell(3);
		titleCell.setCellStyle(titleStyle);
		titleCell.setCellValue(this.title);
		titleRow.setHeightInPoints((short) 20);

		currentRowIndex = 8;
	}

	public ColumnXLS createColumn(int columnSizeInEstimatedChars) {
		lastCreatedColumIndex++;
		sheet.setColumnWidth(lastCreatedColumIndex, columnSizeInEstimatedChars * 256);
		sheet.setDefaultColumnStyle(lastCreatedColumIndex, defaultStyle);

		return new ColumnXLS(lastCreatedColumIndex);
	}

	public void createSpacerColumn(int columnSizeInEstimatedChars) {
		lastCreatedColumIndex++;
		sheet.setColumnWidth(lastCreatedColumIndex, columnSizeInEstimatedChars * 256);
		sheet.setDefaultColumnStyle(lastCreatedColumIndex, defaultStyle);
	}

	public void skipColumn() {
		lastCreatedColumIndex++;
	}

	public ColumnXLS createColumn() {
		lastCreatedColumIndex++;
		sheet.setDefaultColumnStyle(lastCreatedColumIndex, defaultStyle);

		return new ColumnXLS(lastCreatedColumIndex);
	}

	public void processDataRows(List<DataRow> rows, ColumnXLS labelColumn, ColumnXLS dataColumn) {
		for (DataRow dataRow : rows) {
			Row row = sheet.createRow(currentRowIndex);
			currentRowIndex += 2;
			row.setHeightInPoints(row.getHeightInPoints() * dataRow.getDataLineCount());

			Cell labelCell = row.createCell(1);
			labelCell.setCellStyle(labelStyle);
			labelCell.setCellValue(dataRow.getLabel());

			Cell dataCell = row.createCell(3);
			dataCell.setCellStyle(dataStyle);
			dataCell.setCellValue(dataRow.getData());
		}
	}

	public void nextRow() {
		this.currentRowIndex += 2;
	}

	public void createDefaultCell(ColumnXLS column, String cellText) {
		Row row = sheet.getRow(currentRowIndex);
		if (row == null) {
			row = sheet.createRow(currentRowIndex);
		}

		Cell footerCell = row.createCell(column.columIndex);
		footerCell.setCellStyle(defaultStyle);
		footerCell.setCellValue(cellText);
	}

	public void exportToFileAndFlush(String filePath) throws IOException {
		try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
			wb.write(fileOut);
		}
		wb.close();
		this.init();
	}

}
