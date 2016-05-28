package org.eufraten.trelloreporter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eufraten.trelloreporter.ordemDeServico.ManutencaoServices;
import org.eufraten.trelloreporter.ordemDeServico.OrdemDeServico;
import org.eufraten.trelloreporter.ordemDeServico.RelatorioOrdemDeServico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.tabajara.ui.GUI;
import br.com.tabajara.ui.UI;

public class Main {

	private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private final static int OPCAO_SAIR = 0;
	private final static int GERAR_OS_POR_ENDERECO = 1;

	public static void main(String[] args) throws IOException {
		UI userInterface = new GUI();
		int opcao;

		do {
			// TODO pensar em um menu dinamico
			// Problema com reflection para chamada dos metodos de cada item de menu
			// Talvez criar uma ActionBase no enum de item de menu
			opcao = userInterface.readInt(GERAR_OS_POR_ENDERECO + " - Gerar OS por endereço do cartão",
					OPCAO_SAIR + " - Sair", "Digite a opcao");

			if (opcao == GERAR_OS_POR_ENDERECO) {
				gerarOrdemPorURL(userInterface);
			}

		} while (opcao != OPCAO_SAIR);
	}

	private static void gerarOrdemPorURL(UI userInterface) {
		String cardURL = userInterface.readString("Digite o endereço do cartão (URL)");
		if (StringUtils.isBlank(cardURL)) {
			userInterface.write(
					"Campo obrigatório. Para buscar a URL acesse o Trello, clique no cartão e copie o texto que aparece na barra de endereço.");
			return;
		}
		if (!cardURL.toLowerCase().startsWith("https://")) {
			userInterface.write(
					"O endereço deve ser iniciado por https://.");
			return;
		}

		try {
			ManutencaoServices service = new ManutencaoServices();
			LOGGER.info("Gerando ordem de servico");
			OrdemDeServico ordemDeServico = service.gerarOrdemDeServico(cardURL);
			LOGGER.info("Ordem de servico gerada.");
			LOGGER.info("Gerando relatorio.");
			RelatorioOrdemDeServico relatorioOrdemDeServico = new RelatorioOrdemDeServico(ordemDeServico);
			LOGGER.info("Relatorio gerado.");
			LOGGER.info("Exportando relatorio em Excel.");
			String caminhoDoArquivo = relatorioOrdemDeServico.gerarExcel(".");
			LOGGER.info("Relatorio em Excel gerado.");
			caminhoDoArquivo = new File(caminhoDoArquivo).getCanonicalPath();
			userInterface.write("OS nº " + ordemDeServico.getId() + " gerada com sucesso em\n" + caminhoDoArquivo);
		} catch (Exception e) {
			LOGGER.error("Erro ao gerar a OS do endereco {}", e, cardURL);
			userInterface.write("Erro ao gerar a ordem de servico.");
			userInterface.write(e.toString());
		}

	}

}
