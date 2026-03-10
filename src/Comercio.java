import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;

public class Comercio {
    static final int MAX_NOVOS_PRODUTOS = 10;

    static String nomeArquivoDados;
    
    static Scanner teclado;

    static Produto[] produtosCadastrados;

    static int quantosProdutos;

    static void pausa(){
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    static void cabecalho(){
        System.out.println("AEDII COMÉRCIO DE COISINHAS");
        System.out.println("===========================");
    }

    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * Perceba que poderia haver uma melhor modularização com a criação de uma classe Menu.
     * @return Um inteiro com a opção do usuário.
    */
    static int menu(){
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar e listar um produto");
        System.out.println("3 - Cadastrar novo produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de um arquivo texto e retorna um vetor de produtos. Arquivo no formato
     * N  (quantiade de produtos) <br/>
     * tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
        try (Scanner arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("ISO-8859-2"))) {
            int n = Integer.parseInt(arquivo.nextLine().trim());
            Produto[] vetorProdutos = new Produto[n + MAX_NOVOS_PRODUTOS];
            for (int i = 0; i < n; i++) {
                String linha = arquivo.nextLine();
                vetorProdutos[i] = Produto.criarDoTexto(linha);
            }
            quantosProdutos = n;
            return vetorProdutos;
        } catch (FileNotFoundException e) {
            quantosProdutos = 0;
            return new Produto[MAX_NOVOS_PRODUTOS];
        } catch (Exception e) {
            quantosProdutos = 0;
            return new Produto[MAX_NOVOS_PRODUTOS];
        }
    }

    static void listarTodosOsProdutos(){
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
            System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }

    /** Localiza um produto no vetor de cadastrados, a partir do nome, e imprime seus dados.
     *  A busca não é sensível ao caso.  Em caso de não encontrar o produto, imprime mensagem padrão */
    static void localizarProdutos(){
        cabecalho();
        System.out.print("\nDigite a descrição do produto a procurar: ");
        String busca = teclado.nextLine().trim();
        boolean encontrou = false;
        for (int i = 0; i < quantosProdutos; i++) {
            if (produtosCadastrados[i].getDescricao().equalsIgnoreCase(busca)) {
                System.out.println(produtosCadastrados[i].toString());
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("Produto não encontrado.");
        }
    }

    /**
     * Rotina de cadastro de um novo produto: pergunta ao usuário o tipo do produto, lê os dados correspondentes,
     * cria o objeto adequado de acordo com o tipo, inclui no vetor. Este método pode ser feito com um nível muito
     * melhor de modularização. As diversas fases da lógica poderiam ser encapsuladas em outros métodos.
     * Uma sugestão de melhoria mais significativa poderia ser o uso de padrão Factory Method para criação dos objetos.
     */
    static void cadastrarProduto(){
        if (quantosProdutos >= produtosCadastrados.length) {
            System.out.println("Não há espaço para mais produtos.");
            return;
        }
        cabecalho();
        System.out.println("\nCadastro de novo produto");
        System.out.println("1 - Não perecível");
        System.out.println("2 - Perecível");
        System.out.print("Escolha o tipo: ");
        int tipo = Integer.parseInt(teclado.nextLine());
        System.out.print("Descrição (mín. 3 caracteres): ");
        String desc = teclado.nextLine().trim();
        System.out.print("Preço de custo: ");
        double preco = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));
        System.out.print("Margem de lucro (ex: 0,20): ");
        double margem = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));
        try {
            if (tipo == 1) {
                produtosCadastrados[quantosProdutos] = new ProdutoNaoPerecivel(desc, preco, margem);
                quantosProdutos++;
                System.out.println("Produto não perecível cadastrado.");
            } else if (tipo == 2) {
                System.out.print("Data de validade (dd/mm/aaaa): ");
                String dataStr = teclado.nextLine().trim();
                String[] part = dataStr.split("/");
                int dia = Integer.parseInt(part[0]);
                int mes = Integer.parseInt(part[1]);
                int ano = Integer.parseInt(part[2]);
                LocalDate dataValidade = LocalDate.of(ano, mes, dia);
                produtosCadastrados[quantosProdutos] = new ProdutoPerecivel(desc, preco, margem, dataValidade);
                quantosProdutos++;
                System.out.println("Produto perecível cadastrado.");
            } else {
                System.out.println("Tipo inválido.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    /**
     * Salva os dados dos produtos cadastrados no arquivo csv informado. Sobrescreve todo o conteúdo do arquivo.
     * @param nomeArquivo Nome do arquivo a ser gravado.
     */
    public static void salvarProdutos(String nomeArquivo){
        try (FileWriter fw = new FileWriter(nomeArquivo)) {
            fw.write(quantosProdutos + "\n");
            for (int i = 0; i < quantosProdutos; i++) {
                fw.write(produtosCadastrados[i].gerarDadosTexto() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        teclado = new Scanner(System.in, Charset.forName("ISO-8859-2"));
        File arquivoDados = new File("src/dadosProdutos.csv");
        if (!arquivoDados.exists()) {
            arquivoDados = new File("dadosProdutos.csv");
        }
        nomeArquivoDados = arquivoDados.getPath();
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        int opcao = -1;
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> localizarProdutos();
                case 3 -> cadastrarProduto();
            }
            pausa();
        }while(opcao !=0);       

        salvarProdutos(nomeArquivoDados);
        teclado.close();    
    }
}
