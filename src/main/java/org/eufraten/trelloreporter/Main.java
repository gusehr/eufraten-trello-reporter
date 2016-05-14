package org.eufraten.trelloreporter;

import java.io.IOException;
import java.util.Properties;

import org.eufraten.trelloreporter.ordemDeServico.OrdemDeServico;
import org.eufraten.trelloreporter.ordemDeServico.RelatorioOrdemDeServico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;

public class Main {

	public final static String BOARD_ID = "56e48159f355aa7de6717f7b";
	public final static String SAMPLE_CARD_ID = "Yzmp1j6x";

	private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		properties.load(Main.class.getClassLoader().getResourceAsStream("trello.properties"));

		String apiKey = properties.getProperty("trello.api.key");
		String accessToken = properties.getProperty("trello.access.token");

		TrelloImpl trelloImpl = new TrelloImpl(apiKey, accessToken, new ApacheHttpClient());

		String osId = "2";
		String cardId = SAMPLE_CARD_ID;
		if (args != null && args.length > 0) {
			cardId = args[0];
		}
		LOGGER.info("Buscando cargo com id {}", cardId);
		Card card = trelloImpl.getCard(cardId);

		LOGGER.info("Gerando OS para o cartao {} {}", card.getName(), card.getId());
		OrdemDeServico ordemDeServico = new OrdemDeServico(osId, card, trelloImpl);

		LOGGER.info("Gerando relatorio em Excel");
		RelatorioOrdemDeServico relatorioOrdemDeServico = new RelatorioOrdemDeServico(ordemDeServico);
		relatorioOrdemDeServico.gerarExcel("./OS - " + osId);
	}

}
