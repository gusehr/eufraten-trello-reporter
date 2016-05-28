package org.eufraten.trelloreporter.ordemDeServico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.eufraten.trelloreporter.trello.TrelloBoard;

import com.julienvey.trello.domain.Action;
import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.CheckItem;
import com.julienvey.trello.domain.CheckList;
import com.julienvey.trello.domain.Label;

public class OrdemDeServico {

	private String id;
	private String cardId;
	private String solicitante;
	private Date dataAbertura;
	private String epv;
	private String prioridade;
	private String descricao;
	private List<Item> itens = new ArrayList<>();
	private String titulo;

	public OrdemDeServico(String id, Card card, TrelloBoard trelloBoard) {
		this.id = id;
		this.cardId = card.getId();
		this.descricao = card.getName();
		this.descricao += "\n";
		this.descricao += card.getDesc();
		this.titulo = card.getName();

		List<Action> actions = card.getActions(new Argument("filter", "createCard"),
				new Argument("memberCreator_fields", "fullName"));

		if (actions != null && !actions.isEmpty()) {
			Action creationAction = actions.get(0);
			this.dataAbertura = creationAction.getDate();
			this.solicitante = creationAction.getMemberCreator().getFullName();
		}
		if (card.getLabels() != null) {
			for (Label label : card.getLabels()) {
				if (label.getName().startsWith("EPV")) {
					this.epv = label.getName();
				} else if (label.getName().startsWith("P")) {
					this.prioridade = label.getName();
				}
			}
		}
		List<CheckList> checkLists = trelloBoard.checkListsPorCard(card);
		for (CheckList checkList : checkLists) {
			for (CheckItem checkItem : checkList.getCheckItems()) {
				this.itens.add(new Item(checkItem, checkList));
			}
		}
		Collections.sort(this.itens);
	}

	OrdemDeServico(String id, String solicitante, Date dataAbertura, String epv, String prioridade, String descricao) {
		this.id = id;
		this.solicitante = solicitante;
		this.dataAbertura = dataAbertura;
		this.epv = epv;
		this.prioridade = prioridade;
		this.descricao = descricao;
		this.cardId = "mockCardId";
	}

	void addItem(Item item) {
		this.itens.add(item);
	}

	@Override
	public String toString() {
		return "OrdemDeServico [id=" + id + ", solicitante=" + solicitante + ", dataAbertura=" + dataAbertura + ", epv="
				+ epv + ", prioridade=" + prioridade + ", descricao=" + descricao + ", itens=" + itens + "]";
	}

	public String getId() {
		return id;
	}

	public String getCardId() {
		return cardId;
	}

	public String getSolicitante() {
		return solicitante;
	}

	public Date getDataAbertura() {
		return dataAbertura;
	}

	public String getEpv() {
		return epv;
	}

	public String getPrioridade() {
		return prioridade;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getTitulo() {
		return titulo;
	}

	public List<Item> getItens() {
		return itens;
	}

	public static class Item implements Comparable<Item> {
		private String tipo;
		private String nome;
		private boolean marcado;

		Item(CheckItem checkItem, CheckList checkList) {
			this.tipo = checkList.getName();
			this.nome = checkItem.getName();
			this.marcado = "complete".equals(checkItem.getState());
		}

		Item(String tipo, String nome, boolean marcado) {
			this.tipo = tipo;
			this.nome = nome;
			this.marcado = marcado;
		}

		@Override
		public String toString() {
			return "Item [tipo=" + tipo + ", nome=" + nome + ", marcado=" + marcado + "]";
		}

		@Override
		public int compareTo(Item other) {
			return new CompareToBuilder().append(this.tipo, other.tipo)
					.append(this.marcado, other.marcado)
					.append(this.nome, other.nome)
					.toComparison();
		}

		public String getTipo() {
			return tipo;
		}

		public String getNome() {
			return nome;
		}

		public boolean isMarcado() {
			return marcado;
		}

	}

}