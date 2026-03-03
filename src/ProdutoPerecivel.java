import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.text.NumberFormat;

public class ProdutoPerecivel extends Produto {
	
	private static final double DESCONTO = 0.25;
	private static final int PRAZO_DESCONTO = 7;
	private LocalDate dataDeValidade;
	
	/**
	 * Construtor completo para produto perecível.
	 * @param desc Descrição do produto (mínimo de 3 caracteres)
	 * @param precoCusto Preço do produto (mínimo 0.01)
	 * @param margemLucro Margem de lucro (mínimo 0.01)
	 * @param validade Data de validade do produto (não pode ser anterior ao dia atual)
	 */
	public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
		super(desc, precoCusto, margemLucro);
		
		if (validade.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Data de validade não pode ser anterior ao dia atual.");
		}
		
		this.dataDeValidade = validade;
	}
	
	/**
	 * Retorna o valor de venda do produto perecível, considerando seu preço de custo e margem de lucro.
	 * Não pode ser vendido se estiver fora da data de validade.
	 * Aplica desconto de 25% se o vencimento estiver com prazo de 7 dias ou menos.
	 * @return Valor de venda do produto (double, positivo)
	 * @throws IllegalStateException se o produto estiver vencido
	 */
	@Override
	public double valorDeVenda() {
		LocalDate hoje = LocalDate.now();
		
		if (dataDeValidade.isBefore(hoje)) {
			throw new IllegalStateException("Produto fora da data de validade não pode ser vendido.");
		}
		
		double valorBase = precoCusto * (1.0 + margemLucro);
		
		long diasAteVencimento = ChronoUnit.DAYS.between(hoje, dataDeValidade);
		
		if (diasAteVencimento <= PRAZO_DESCONTO) {
			return valorBase * (1.0 - DESCONTO);
		}
		
		return valorBase;
	}
	
	/**
	 * Descrição, em string, do produto perecível, contendo sua descrição e o valor de venda.
	 * @return String com o formato: [NOME]: R$ [VALOR DE VENDA]
	 */
	@Override
	public String toString() {
		NumberFormat moeda = NumberFormat.getCurrencyInstance();
		return String.format("NOME: " + descricao + ": " + moeda.format(valorDeVenda()));
	}
}