package org.eufraten.trelloreporter.ordemDeServico;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.eufraten.trelloreporter.ordemDeServico.OrdemDeServico.Item;
import org.junit.Assert;
import org.junit.Test;

public class RelatorioOrdemDeServicoTest {

	private String TEMP_PATH = System.getProperty("java.io.tmpdir");

	@Test
	public void ordem_de_servico_com_itens() throws IOException {
		OrdemDeServico ordemDeServico = new OrdemDeServico("2", "Gustavo Ehrhardt", new Date(), "EPV 3", "P2",
				"Trocar a lâmpada da sala de reunião");

		ordemDeServico.addItem(new Item("Materiais", "Lampada", true));
		ordemDeServico.addItem(new Item("Materiais", "Fita isolante", true));
		ordemDeServico.addItem(new Item("EPI", "Máscara", true));
		ordemDeServico.addItem(new Item("EPI", "Luvas", true));

		RelatorioOrdemDeServico relatorioOrdemDeServico = new RelatorioOrdemDeServico(ordemDeServico);
		String filePath = relatorioOrdemDeServico.gerarCaminhoDoArquivo(TEMP_PATH);

		File file = new File(filePath);
		Assert.assertFalse(file.exists());

		relatorioOrdemDeServico.gerarExcel(TEMP_PATH);

		file = new File(filePath);
		Assert.assertTrue(file.exists());
		file.deleteOnExit();
	}

	@Test
	public void ordem_de_servico_sem_itens() throws IOException {
		OrdemDeServico ordemDeServico = new OrdemDeServico("3", "Gustavo Ehrhardt", new Date(), "EPV 3", "P2",
				"Trocar a lâmpada da sala de reunião");

		RelatorioOrdemDeServico relatorioOrdemDeServico = new RelatorioOrdemDeServico(ordemDeServico);
		String filePath = relatorioOrdemDeServico.gerarCaminhoDoArquivo(TEMP_PATH);

		File file = new File(filePath);
		Assert.assertFalse(file.exists());

		relatorioOrdemDeServico.gerarExcel(TEMP_PATH);

		file = new File(filePath);
		Assert.assertTrue(file.exists());
		file.deleteOnExit();
	}

	@Test
	public void ordem_de_servico_com_dados_nulos() throws IOException {
		OrdemDeServico ordemDeServico = new OrdemDeServico(null, null, null, null, null,
				null);

		RelatorioOrdemDeServico relatorioOrdemDeServico = new RelatorioOrdemDeServico(ordemDeServico);
		String filePath = relatorioOrdemDeServico.gerarCaminhoDoArquivo(TEMP_PATH);

		File file = new File(filePath);
		Assert.assertFalse(file.exists());

		relatorioOrdemDeServico.gerarExcel(TEMP_PATH);

		file = new File(filePath);
		Assert.assertTrue(file.exists());
		file.deleteOnExit();
	}

}
