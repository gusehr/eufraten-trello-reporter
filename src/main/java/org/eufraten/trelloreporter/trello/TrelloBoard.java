package org.eufraten.trelloreporter.trello;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Action;
import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.CheckList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;

public class TrelloBoard {

	@SuppressWarnings("unused")
	private String boardId;
	private String apiKey;
	private String accessToken;
	private Trello trello;

	private final static Logger LOGGER = LoggerFactory.getLogger(TrelloBoard.class);

	public TrelloBoard(String id) {
		this.boardId = id;
		Properties properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream("trello.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Erro ao ler os dados de autenticacao do quadro: " + id, e);
		}

		this.apiKey = properties.getProperty("trello.api.key");
		this.accessToken = properties.getProperty("trello.access.token");

		if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(this.apiKey)) {
			throw new RuntimeException("Dados vazios para autenticacao do quadro: " + id);
		}
	}

	public Card cardPorUrl(String url) {
		Pattern regexCardId = Pattern.compile("^.*trello.com/c/([A-z0-9]+)[/]*.*$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = regexCardId.matcher(url);
		if (matcher.find()) {
			String cardId = matcher.group(1);
			LOGGER.debug("Id do cartao encontrado {}", cardId);
			return getTrello().getCard(cardId);
		}
		LOGGER.warn("O id do cartao nao foi encontrado na url {}", url);

		return null;
	}

	public Card cardPorId(String cardId) {
		return getTrello().getCard(cardId);
	}

	public List<Action> comentariosDoCard(String cardId) {
		return getTrello().getCardActions(cardId, new Argument("filter", "commentCard"));
	}

	public void adicionarComentario(String cardId, String comentario) {
		getTrello().addCommentToCard(cardId, comentario);
	}

	public CheckList checkListPorId(String checkListId) {
		return getTrello().getCheckList(checkListId);
	}

	public List<CheckList> checkListsPorCard(Card card) {
		List<CheckList> checkLists = new ArrayList<>();
		if (card.getIdChecklists() != null) {
			Trello trello = getTrello();
			for (String checkListId : card.getIdChecklists()) {
				checkLists.add(trello.getCheckList(checkListId));
			}
		}

		return checkLists;
	}

	public void adicionarAnexo(String cardId, String filePath) throws IOException {
		getTrello().addAttachmentToCard(cardId, new File(filePath));
	}

	private Trello getTrello() {
		if (this.trello == null) {
			this.trello = new TrelloImpl(this.apiKey, this.accessToken, new ApacheHttpClient());
		}
		return this.trello;
	}

}
