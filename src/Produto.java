import java.text.NumberFormat;

public abstract class Produto {
	
	protected static final double MARGEM_PADRAO = 0.2;
	protected String descricao;
	protected double precoCusto;
	protected double margemLucro;
	
	/**
     * Inicializador protegido. Os valores default, em caso de erro, são:
     * "Produto sem descrição", R$ 0.00, 0.0  
     * @param desc Descrição do produto (mínimo de 3 caracteres)
     * @param precoCusto Preço do produto (mínimo 0.01)
     * @param margemLucro Margem de lucro (mínimo 0.01)
     */
	protected void init(String desc, double precoCusto, double margemLucro) {
		
		if ((desc.length() >= 3) && (precoCusto > 0.0) && (margemLucro > 0.0)) {
			descricao = desc;
			this.precoCusto = precoCusto;
			this.margemLucro = margemLucro;
		} else {
			throw new IllegalArgumentException("Valores inválidos para os dados do produto.");
		}
	}
	
	/**
     * Construtor completo. Os valores default, em caso de erro, são:
     * "Produto sem descrição", R$ 0.00, 0.0  
     * @param desc Descrição do produto (mínimo de 3 caracteres)
     * @param precoCusto Preço do produto (mínimo 0.01)
     * @param margemLucro Margem de lucro (mínimo 0.01)
     */
	public Produto(String desc, double precoCusto, double margemLucro) {
		init(desc, precoCusto, margemLucro);
	}
	
	/**
     * Construtor sem margem de lucro - fica considerado o valor padrão de margem de lucro.
     * Os valores default, em caso de erro, são:
     * "Produto sem descrição", R$ 0.00 
     * @param desc Descrição do produto (mínimo de 3 caracteres)
     * @param precoCusto Preço do produto (mínimo 0.01)
     */
	public Produto(String desc, double precoCusto) {
		init(desc, precoCusto, MARGEM_PADRAO);
	}
	
	 /**
     * Retorna o valor de venda do produto, considerando seu preço de custo e margem de lucro.
     * @return Valor de venda do produto (double, positivo)
     */
	public double valorDeVenda() {
		return (precoCusto * (1.0 + margemLucro));
	}

	/** @return A descrição do produto */
	public String getDescricao() {
		return descricao;
	}

	/**
     * Descrição, em string, do produto, contendo sua descrição e o valor de venda.
     *  @return String com o formato:
     * [NOME]: R$ [VALOR DE VENDA]
     */
    @Override
	public String toString() {
    	
    	NumberFormat moeda = NumberFormat.getCurrencyInstance();
    	
		return String.format("NOME: " + descricao + ": " + moeda.format(valorDeVenda()));
	}

	/**
	 * Igualdade de produtos: caso possuam o mesmo nome/descrição.
	 * @param obj Outro produto a ser comparado
	 * @return booleano true/false conforme o parâmetro possua a descrição igual ou não a este produto.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Produto outro = (Produto) obj;
		return this.descricao.toLowerCase().equals(outro.descricao.toLowerCase());
	}

	/**
	 * Gera uma linha de texto a partir dos dados do produto
	 * @return Uma string no formato "tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
	 */
	public abstract String gerarDadosTexto();

	/**
	 * Cria um produto a partir de uma linha de dados em formato texto. A linha de dados deve estar de acordo com a formatação
	 * "tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
	 * ou o funcionamento não será garantido. Os tipos são 1 para produto não perecível e 2 para perecível.
	 * @param linha Linha com os dados do produto a ser criado.
	 * @return Um produto com os dados recebidos
	 */
	public static Produto criarDoTexto(String linha) {
		if (linha == null || linha.isBlank()) return null;
		String[] partes = linha.split(";");
		for (int i = 0; i < partes.length; i++) {
			partes[i] = partes[i].trim();
		}
		if (partes.length < 4) return null;
		int tipo = Integer.parseInt(partes[0]);
		String descricao = partes[1];
		double precoCusto = Double.parseDouble(partes[2].replace(",", "."));
		double margemLucro = Double.parseDouble(partes[3].replace(",", "."));
		if (tipo == 1) {
			return new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
		}
		if (tipo == 2 && partes.length >= 5) {
			// data no formato dd/mm/aaaa (carregamento de arquivo permite data passada)
			String[] dataPartes = partes[4].split("/");
			if (dataPartes.length != 3) return null;
			int dia = Integer.parseInt(dataPartes[0]);
			int mes = Integer.parseInt(dataPartes[1]);
			int ano = Integer.parseInt(dataPartes[2]);
			java.time.LocalDate dataValidade = java.time.LocalDate.of(ano, mes, dia);
			return new ProdutoPerecivel(descricao, precoCusto, margemLucro, dataValidade, true);
		}
		return null;
	}
}