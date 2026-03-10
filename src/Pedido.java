import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Pedido {

	private static final int MAX_PRODUTOS = 10;
	
	private static final double DESCONTO_PG_A_VISTA = 0.15;
	
	private ItemDePedido[] itens;
	
	private LocalDate dataPedido;
	
	private int quantItens = 0;
	
	private int formaDePagamento;
	
	/** Construtor do pedido.
	 *  Deve criar o vetor de produtos do pedido, 
	 *  armazenar a data e a forma de pagamento informadas para o pedido. 
	 */  
	public Pedido(LocalDate dataPedido, int formaDePagamento) {
		itens = new ItemDePedido[MAX_PRODUTOS];
		quantItens = 0;
		this.dataPedido = dataPedido;
		this.formaDePagamento = formaDePagamento;
	}
	
	/**
	 * Inclui um item no pedido.
	 * @param item O item a ser incluído
	 * @return true se houve espaço e a inclusão foi feita; false caso contrário
	 */
	public boolean incluirItem(ItemDePedido item) {
		if (item == null || quantItens >= MAX_PRODUTOS) return false;
		itens[quantItens++] = item;
		return true;
	}
	
	/**
	 * Inclui um produto como novo item (quantidade 1, preço no momento da venda).
	 * @param novo O produto a ser incluído no pedido
	 * @return true/false indicando se a inclusão foi realizada com sucesso
	 */
	public boolean incluirProduto(Produto novo) {
		if (novo == null || quantItens >= MAX_PRODUTOS) return false;
		ItemDePedido item = new ItemDePedido(novo, 1, novo.valorDeVenda());
		itens[quantItens++] = item;
		return true;
	}
	
	/**
     * Calcula e retorna o valor final do pedido (soma do valor de venda de todos os produtos do pedido).
     * Caso a forma de pagamento do pedido seja à vista, aplica o desconto correspondente.
     * @return Valor final do pedido (double)
     */
	public double valorFinal() {
		double valorPedido = 0;
		for (int i = 0; i < quantItens; i++) {
			if (itens[i] != null) valorPedido += itens[i].calcularSubtotal();
		}
		if (formaDePagamento == 1) {
			valorPedido = valorPedido * (1.0 - DESCONTO_PG_A_VISTA);
		}
		return valorPedido;
	}
	
	/**
     * Representação, em String, do pedido.
     * Contém um cabeçalho com sua data e o número de produtos no pedido.
     * Depois, em cada linha, a descrição de cada produto do pedido.
     * Ao final, mostra a forma de pagamento, o percentual de desconto (se for o caso) e o valor a ser pago pelo pedido.
     * Exemplo:
     * Data do pedido: 25/08/2025
     * Pedido com 2 produtos.
     * Produtos no pedido:
     * NOME: Iogurte: R$ 8.00
     * Válido até: 29/08/2025
     * NOME: Guardanapos: R$ 2.75
     * Pedido pago à vista. Percentual de desconto: 15,00%
     * Valor total do pedido: R$ 10.75 
     * @return Uma string contendo dados do pedido conforme especificado (cabeçalho, detalhes, forma de pagamento,
     * percentual de desconto - se for o caso - e valor a pagar)
     */
	@Override
	public String toString() {
		StringBuilder stringPedido = new StringBuilder();
		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		stringPedido.append("Data do pedido:" + formatoData.format(dataPedido) + "\n");
		stringPedido.append("Pedido com " + quantItens + " itens.\n");
		stringPedido.append("Itens no pedido:\n");
		for (int i = 0; i < quantItens; i++) {
			if (itens[i] != null) {
				stringPedido.append(itens[i].getProduto().toString()).append(" Qtd: ").append(itens[i].getQuantidade()).append("\n");
			}
		}
		stringPedido.append("Pedido pago ");
		if (formaDePagamento == 1) {
			stringPedido.append("à vista. Percentual de desconto: " + String.format("%.2f", DESCONTO_PG_A_VISTA * 100) + "%\n");
		} else {
			stringPedido.append("parcelado.\n");
		}
		stringPedido.append("Valor total do pedido: R$ " + String.format("%.2f", valorFinal()));
		return stringPedido.toString();
	}
	
	/**
	 * Mescla o pedido atual com outro pedido: transfere todos os itens do outro para este.
	 * Itens com o mesmo produto são agrupados (soma-se a quantidade) e fica o menor preço de venda.
	 * @param outroPedido pedido do qual os itens serão transferidos (será esvaziado ao final)
	 * @throws IllegalStateException se a capacidade do vetor for insuficiente
	 */
	public void mesclarPedido(Pedido outroPedido) {
		if (outroPedido == null) return;
		int novasPosicoes = 0;
		for (int j = 0; j < outroPedido.quantItens; j++) {
			ItemDePedido itemOutro = outroPedido.itens[j];
			if (itemOutro == null) continue;
			boolean jaExiste = false;
			for (int i = 0; i < quantItens; i++) {
				if (itens[i] != null && itens[i].equals(itemOutro)) {
					jaExiste = true;
					break;
				}
			}
			if (!jaExiste) novasPosicoes++;
		}
		if (quantItens + novasPosicoes > MAX_PRODUTOS) {
			throw new IllegalStateException("Capacidade do pedido insuficiente para mesclar.");
		}
		for (int j = 0; j < outroPedido.quantItens; j++) {
			ItemDePedido itemOutro = outroPedido.itens[j];
			if (itemOutro == null) continue;
			int idxPrincipal = -1;
			for (int i = 0; i < quantItens; i++) {
				if (itens[i] != null && itens[i].equals(itemOutro)) {
					idxPrincipal = i;
					break;
				}
			}
			if (idxPrincipal >= 0) {
				itens[idxPrincipal].setQuantidade(itens[idxPrincipal].getQuantidade() + itemOutro.getQuantidade());
				if (itemOutro.getPrecoVenda() < itens[idxPrincipal].getPrecoVenda()) {
					itens[idxPrincipal].setPrecoVenda(itemOutro.getPrecoVenda());
				}
			} else {
				itens[quantItens++] = itemOutro;
			}
		}
		for (int j = 0; j < outroPedido.quantItens; j++) {
			outroPedido.itens[j] = null;
		}
		outroPedido.quantItens = 0;
	}
	
	/**
	 * Imprime no terminal um recibo (cupom fiscal) do pedido.
	 * Lista: Nome do Produto, Quantidade, Preço Unitário, Subtotal.
	 * Se quantidade do item > 10, aplica 5% de desconto no subtotal daquele item.
	 * Ao final exibe o Total Geral.
	 */
	public void imprimirRecibo() {
		System.out.println("========== RECIBO DE VENDA ==========");
		double totalGeral = 0;
		for (int i = 0; i < quantItens; i++) {
			ItemDePedido item = itens[i];
			if (item == null) continue;
			String nome = item.getProduto().getDescricao();
			int qtd = item.getQuantidade();
			double precoUnit = item.getPrecoVenda();
			double subtotal = item.calcularSubtotal();
			if (qtd > 10) {
				subtotal = subtotal * 0.95;
			}
			totalGeral += subtotal;
			System.out.println(nome + " | Qtd: " + qtd + " | Preço unit.: R$ " + String.format("%.2f", precoUnit) + " | Subtotal: R$ " + String.format("%.2f", subtotal));
		}
		System.out.println("--------------------------------------");
		System.out.println("TOTAL GERAL: R$ " + String.format("%.2f", totalGeral));
		System.out.println("======================================");
	}
	
	/**
     * Igualdade de pedidos: caso possuam a mesma data. 
     * @param obj Outro pedido a ser comparado 
     * @return booleano true/false conforme o parâmetro possua a data igual ou não a este pedido.
     */
    @Override
    public boolean equals(Object obj) {
        Pedido outro = (Pedido)obj;
        return this.dataPedido.equals(outro.dataPedido);
    }
}