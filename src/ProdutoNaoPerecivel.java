public class ProdutoNaoPerecivel extends Produto {
	
	/**
	 * Construtor completo para produto não perecível.
	 * @param desc Descrição do produto (mínimo de 3 caracteres)
	 * @param precoCusto Preço do produto (mínimo 0.01)
	 * @param margemLucro Margem de lucro (mínimo 0.01)
	 */
	public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro) {
		super(desc, precoCusto, margemLucro);
	}
	
	/**
	 * Construtor sem margem de lucro - fica considerado o valor padrão de margem de lucro.
	 * @param desc Descrição do produto (mínimo de 3 caracteres)
	 * @param precoCusto Preço do produto (mínimo 0.01)
	 */
	public ProdutoNaoPerecivel(String desc, double precoCusto) {
		super(desc, precoCusto);
	}
	
	/**
	 * Retorna o valor de venda do produto não perecível, considerando seu preço de custo e margem de lucro.
	 * @return Valor de venda do produto (double, positivo)
	 */
	@Override
	public double valorDeVenda() {
		return super.valorDeVenda();
	}

	/**
	 * Gera uma linha de texto a partir dos dados do produto. Preço e margem de lucro são formatados com 2 casas decimais.
	 * @return Uma string no formato "1;descrição;preçoCusto;margemLucro"
	 */
	@Override
	public String gerarDadosTexto() {
		String precoFormatado = String.format("%.2f", precoCusto).replace(",", ".");
		String margemFormatada = String.format("%.2f", margemLucro).replace(",", ".");
		return String.format("1;%s;%s;%s", descricao, precoFormatado, margemFormatada);
	}
}

