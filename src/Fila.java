import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {

	private Celula<E> frente;
	private Celula<E> tras;

	public Fila() {

		Celula<E> sentinela = new Celula<E>();
		frente = sentinela;
		tras = sentinela;
	}

	public boolean vazia() {
		return frente == tras;
	}


	public void enfileirar(E item) {

		Celula<E> novaCelula = new Celula<E>(item);
		tras.setProximo(novaCelula);
		tras = novaCelula;
	}

	public E desenfileirar() {

		E item = consultarPrimeiro();
		Celula<E> primeiro = frente.getProximo();
		frente.setProximo(primeiro.getProximo());

		if (primeiro == tras) {
			tras = frente;
		}

		return item;
	}

	public E consultarPrimeiro() {

		if (vazia()) {
			throw new NoSuchElementException("Não há nenhum item na fila!");
		}

		return frente.getProximo().getItem();
	}

	public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {

		if (quantidade <= 0) {
			throw new IllegalArgumentException("A quantidade de elementos deve ser positiva!");
		}

		double soma = 0;
		Celula<E> atual = frente.getProximo();

		for (int i = 0; i < quantidade; i++) {
			if (atual == null) {
				throw new IllegalArgumentException("A fila não contém " + quantidade + " elementos!");
			}
			soma += extrator.apply(atual.getItem());
			atual = atual.getProximo();
		}

		return soma / quantidade;
	}

	public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {

		if (quantidade <= 0) {
			throw new IllegalArgumentException("A quantidade de elementos deve ser positiva!");
		}

		Fila<E> filtrada = new Fila<E>();
		Celula<E> atual = frente.getProximo();

		for (int i = 0; i < quantidade; i++) {
			if (atual == null) {
				throw new IllegalArgumentException("A fila não contém " + quantidade + " elementos!");
			}
			if (condicional.test(atual.getItem())) {
				filtrada.enfileirar(atual.getItem());
			}
			atual = atual.getProximo();
		}

		return filtrada;
	}
}