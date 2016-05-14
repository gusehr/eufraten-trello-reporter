package org.eufraten.trelloreporter.ordemDeServico;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eufraten.trelloreporter.ordemDeServico.OrdemDeServico.Item;
import org.eufraten.trelloreporter.xls.BaseXLS;
import org.eufraten.trelloreporter.xls.ColumnXLS;
import org.eufraten.trelloreporter.xls.DataRow;

public class RelatorioOrdemDeServico {

	private OrdemDeServico ordemDeServico;

	public RelatorioOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
	}

	public void gerarExcel(String filePath) throws IOException {
		BaseXLS baseXLS = new BaseXLS("Ordem de serviço de manutenção");

		baseXLS.createSpacerColumn(2);
		ColumnXLS primeiraColuna = baseXLS.createColumn(20);
		baseXLS.createSpacerColumn(2);
		ColumnXLS segundaColuna = baseXLS.createColumn(10);
		baseXLS.skipColumn();
		ColumnXLS colunaParaAssinatura = primeiraColuna;
		ColumnXLS colunaParaSegundaAssinatura = baseXLS.createColumn();

		List<DataRow> rows = new ArrayList<>();
		gerarDadosPrincipais(rows);
		gerarDadosDeItens(rows);

		baseXLS.processDataRows(rows, primeiraColuna, segundaColuna);

		iniciarRodape(baseXLS, primeiraColuna);

		gerarAssinaturas(baseXLS, colunaParaAssinatura, colunaParaSegundaAssinatura);

		baseXLS.exportToFileAndFlush(filePath);
	}

	private void gerarAssinaturas(BaseXLS baseXLS, ColumnXLS colunaParaAssinatura,
			ColumnXLS colunaParaSegundaAssinatura) {
		baseXLS.nextRow();
		baseXLS.nextRow();

		baseXLS.createDefaultCell(colunaParaAssinatura, "_______________________");
		baseXLS.createDefaultCell(colunaParaSegundaAssinatura, "_______________________");

		baseXLS.nextRow();
		baseXLS.createDefaultCell(colunaParaAssinatura, "Solicitante");
		baseXLS.createDefaultCell(colunaParaSegundaAssinatura, "André Silva");
	}

	private void iniciarRodape(BaseXLS baseXLS, ColumnXLS primeiraColuna) {
		baseXLS.nextRow();
		baseXLS.createDefaultCell(primeiraColuna, "___________________________________________________________");

		baseXLS.nextRow();
		baseXLS.createDefaultCell(primeiraColuna, "Ordem de serviço realizada em   _____ / _____ / _________");
	}

	private void gerarDadosPrincipais(List<DataRow> rows) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		rows.add(new DataRow("Número", ordemDeServico.getId()));
		rows.add(new DataRow("Descrição", ordemDeServico.getDescricao()));
		rows.add(new DataRow("Local", ordemDeServico.getEpv()));
		rows.add(new DataRow("Prioridade", ordemDeServico.getPrioridade()));
		rows.add(new DataRow("Solicitante", ordemDeServico.getSolicitante()));
		if (ordemDeServico.getDataAbertura() != null) {
			rows.add(new DataRow("Data solicitação", formatter.format(ordemDeServico.getDataAbertura())));
		}
	}

	private void gerarDadosDeItens(List<DataRow> rows) {
		Map<String, List<Item>> itensPorTipo = ordemDeServico.getItens()
				.stream()
				.collect(Collectors.groupingBy(OrdemDeServico.Item::getTipo));

		for (Entry<String, List<Item>> itensPorTipoEntry : itensPorTipo.entrySet()) {
			for (int i = 0; i < itensPorTipoEntry.getValue().size(); i++) {
				Item item = itensPorTipoEntry.getValue().get(i);

				String label = "";
				if (i == 0) {
					label = itensPorTipoEntry.getKey();
				}
				rows.add(new DataRow(label, item.getNome()));
			}
		}
	}

}
