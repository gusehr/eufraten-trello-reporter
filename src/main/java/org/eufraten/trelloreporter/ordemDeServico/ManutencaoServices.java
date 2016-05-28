package org.eufraten.trelloreporter.ordemDeServico;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eufraten.trelloreporter.trello.TrelloBoard;

import com.julienvey.trello.domain.Action;
import com.julienvey.trello.domain.Card;

public class ManutencaoServices {

	private final static String QUADRO_MANUTENCAO_ID = "56e48159f355aa7de6717f7b";
	private final static String CARD_OS_ID = "sjMFgC4m";

	public OrdemDeServico gerarOrdemDeServico(String cardUrl) {
		TrelloBoard boardManutencao = new TrelloBoard(QUADRO_MANUTENCAO_ID);

		Card cardOrdemServico = boardManutencao.cardPorUrl(cardUrl);

		String numeroOS = gerarNumeroDaOS(boardManutencao);

		OrdemDeServico ordemDeServico = new OrdemDeServico(numeroOS, cardOrdemServico, boardManutencao);
		return ordemDeServico;
	}

	private final static Pattern REGEX_NUMERO_OS = Pattern.compile("^[ ]*OS ([0-9]+) Gerada.*$",
			Pattern.CASE_INSENSITIVE);

	private String gerarNumeroDaOS(TrelloBoard boardManutencao) {
		int ultimaOSGerada = 0;
		List<Action> comentariosDoCard = boardManutencao.comentariosDoCard(CARD_OS_ID);
		for (Action comentario : comentariosDoCard) {
			Matcher matcherNumeroOS = REGEX_NUMERO_OS.matcher(comentario.getData().getText());
			if (matcherNumeroOS.find()) {
				ultimaOSGerada = Integer.valueOf(matcherNumeroOS.group(1));
				break;
			}
		}
		ultimaOSGerada++;
		String novoComentario = "OS " + ultimaOSGerada + " Gerada";

		boardManutencao.adicionarComentario(CARD_OS_ID, novoComentario);

		return String.valueOf(ultimaOSGerada);
	}

	public void salvarRelatorio(RelatorioOrdemDeServico relatorioOrdemDeServico) throws IOException {
		relatorioOrdemDeServico.gerarExcel(".");
		// TrelloBoard boardManutencao = new TrelloBoard(QUADRO_MANUTENCAO_ID);
		// boardManutencao.adicionarAnexo(relatorioOrdemDeServico.getOrdemDeServico().getCardId(), filePath);
	}

}
