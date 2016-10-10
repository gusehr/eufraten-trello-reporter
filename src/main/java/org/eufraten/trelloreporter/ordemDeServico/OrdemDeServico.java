package org.eufraten.trelloreporter.ordemDeServico;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
	private Date dataFechamento;

	public OrdemDeServico(String id, Card card, TrelloBoard trelloBoard) {
		this(card, trelloBoard);
		this.id = id;
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

	OrdemDeServico(Card card, TrelloBoard trelloBoard) {
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

	public Date getDataFechamento() {
		return dataFechamento;
	}

	void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public long calcularLeadTime() {
		LocalDate inicio = LocalDateTime
				.ofInstant(Instant.ofEpochMilli(this.dataAbertura.getTime()), ZoneId.systemDefault())
				.toLocalDate();
		LocalDate fim;
		if (this.dataFechamento == null) {
			fim = LocalDate.now(ZoneId.systemDefault());
		} else {
			fim = LocalDateTime
					.ofInstant(Instant.ofEpochMilli(this.dataFechamento.getTime()), ZoneId.systemDefault())
					.toLocalDate();
		}

		return ChronoUnit.DAYS.between(inicio, fim);
	}

	// Se precisar ignorar fim de semana
	// static long daysBetween(LocalDate start, LocalDate end, List<DayOfWeek> ignore) {
	// return Stream.iterate(start, d->d.plusDays(1))
	// .limit(start.until(end, ChronoUnit.DAYS))
	// .filter(d->!ignore.contains(d.getDayOfWeek()))
	// .count();
	// }

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
