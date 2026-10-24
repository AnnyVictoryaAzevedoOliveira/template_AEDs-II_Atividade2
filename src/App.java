import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    /** Pilha de pedidos (o mais recente fica no topo) */
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();

    /** Fila de pedidos (armazenados por ordem de chegada) */
    static Fila<Pedido> filaPedidos = new Fila<>();
        
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    /**
     * Lê um valor decimal do teclado, aceitando vírgula ou ponto como separador decimal.
     * @param mensagem Mensagem exibida ao usuário.
     * @return O valor lido, ou null caso o texto digitado não seja um número válido.
     */
    static Double lerValor(String mensagem) {

    	System.out.println(mensagem);
    	try {
    		return Double.parseDouble(teclado.nextLine().trim().replace(",", "."));
    	} catch (NumberFormatException excecao) {
    		return null;
    	}
    }

    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Listar produtos dos pedidos mais recentes");
        System.out.println("7 - Exibir o valor total médio dos N primeiros pedidos");
        System.out.println("8 - Exibir os primeiros pedidos com valor total acima de um determinado valor");
        System.out.println("9 - Exibir os primeiros pedidos que apresentam um determinado produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null 
     */
    static Produto localizarProduto() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
        int idProduto = lerOpcao("Digite o código identificador do produto desejado: ", Integer.class);
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].hashCode() == idProduto) {
        		produto = produtosCadastrados[i];
        		localizado = true;
        	}
        }
        
        return produto;   
    }
    
    /** Localiza um produto no vetor de produtos cadastrados, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null
     *  @return O produto encontrado ou null, caso o produto não tenha sido localizado no vetor de produtos cadastrados.
     */
    static Produto localizarProdutoDescricao() {
        
    	Produto produto = null;
    	Boolean localizado = false;
    	String descricao;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        for (int i = 0; (i < quantosProdutos && !localizado); i++) {
        	if (produtosCadastrados[i].descricao.toLowerCase().equals(descricao.toLowerCase())) {
        		produto = produtosCadastrados[i];
        		localizado = true;
    		}
        }
        
        return produto;
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {
    	
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - %s", (i + 1), produtosCadastrados[i].toString()));
        }
    }
    
    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
     */
    public static Pedido iniciarPedido() {
    	
    	int formaPagamento = lerOpcao("Digite a forma de pagamento do pedido, sendo 1 para pagamento à vista e 2 para pagamento a prazo", Integer.class);
    	Pedido pedido = new Pedido(LocalDate.now(), formaPagamento);
    	Produto produto;
    	int numProdutos;
    	int quantidade;
    	
    	listarTodosOsProdutos();
    	System.out.println("Incluindo produtos no pedido...");
    	numProdutos = lerOpcao("Quantos produtos serão incluídos no pedido?", Integer.class);
        for (int i = 0; i < numProdutos; i++) {
        	produto = localizarProdutoDescricao();
        	if (produto == null) {
        		System.out.println("Produto não encontrado");
        		i--;
        	} else {
        		quantidade = lerOpcao("Quantos itens desse produto serão incluídos no pedido?", Integer.class);
        		pedido.incluirProduto(produto, quantidade);
        	}
        }
    	
        return pedido;
    }
    
    /**
     * Finaliza um pedido, momento no qual ele deve ser armazenado na pilha e na fila de pedidos.
     * A pilha (LIFO) mantém o pedido finalizado mais recentemente no topo, sendo o primeiro a ser consultado.
     * A fila (FIFO) mantém os pedidos por ordem de chegada, com o mais antigo na frente.
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {

    	cabecalho();

    	if (pedido == null) {
    		System.out.println("Não há pedido em aberto. Inicie um novo pedido antes de finalizá-lo.");
    		return;
    	}

    	pilhaPedidos.empilhar(pedido);
    	filaPedidos.enfileirar(pedido);
    	System.out.println("Pedido finalizado e armazenado com sucesso!");
    	System.out.println(pedido);
    }

    /**
     * Lista os produtos incluídos nos pedidos mais recentemente finalizados.
     * O usuário informa quantos pedidos recentes deseja consultar. Os pedidos são exibidos
     * do mais recente para o mais antigo. A pilha de pedidos não é alterada.
     */
    public static void listarProdutosPedidosRecentes() {

    	cabecalho();

    	if (pilhaPedidos.vazia()) {
    		System.out.println("Nenhum pedido foi finalizado até o momento.");
    		return;
    	}

    	Integer numPedidos = lerOpcao("Quantos pedidos recentes deseja consultar?", Integer.class);
    	if (numPedidos == null || numPedidos <= 0) {
    		System.out.println("Quantidade inválida de pedidos.");
    		return;
    	}

    	Pilha<Pedido> pedidosRecentes;
    	try {
    		pedidosRecentes = pilhaPedidos.subPilha(numPedidos);
    	} catch (IllegalArgumentException excecao) {
    		System.out.println("Não há " + numPedidos + " pedidos finalizados para consultar.");
    		return;
    	}

    	if (numPedidos == 1) {
    		System.out.println("\nPRODUTOS DO PEDIDO MAIS RECENTE:");
    	} else {
    		System.out.println("\nPRODUTOS DOS " + numPedidos + " PEDIDOS MAIS RECENTES:");
    	}
    	while (!pedidosRecentes.vazia()) {
    		Pedido pedido = pedidosRecentes.desempilhar();
    		System.out.println("\nPedido " + pedido.hashCode() + ":");
    		for (ItemDePedido item : pedido.getItensDoPedido()) {
    			if (item != null) {
    				System.out.println(String.format("  - %s (quantidade: %02d)", item.getProduto(), item.getQuantidade()));
    			}
    		}
    	}
    }
    
    /**
     * Verifica se há pedidos na fila e lê do usuário quantos dos primeiros pedidos devem ser considerados.
     * @return A quantidade informada, ou null se a fila estiver vazia ou a quantidade for inválida.
     */
    static Integer lerQuantidadePrimeirosPedidos() {

    	if (filaPedidos.vazia()) {
    		System.out.println("Nenhum pedido foi finalizado até o momento.");
    		return null;
    	}

    	Integer quantidade = lerOpcao("Quantos dos primeiros pedidos deseja considerar?", Integer.class);
    	if (quantidade == null || quantidade <= 0) {
    		System.out.println("Quantidade inválida de pedidos.");
    		return null;
    	}

    	return quantidade;
    }

    /**
     * Exibe todos os pedidos de uma fila, do primeiro ao último.
     * A fila recebida é esvaziada durante a exibição (deve ser uma fila auxiliar, como a retornada por filtrar).
     * @param pedidos Fila com os pedidos a serem exibidos.
     */
    static void exibirPedidos(Fila<Pedido> pedidos) {

    	if (pedidos.vazia()) {
    		System.out.println("Nenhum pedido encontrado.");
    		return;
    	}

    	while (!pedidos.vazia()) {
    		System.out.println(pedidos.desenfileirar());
    	}
    }

    /**
     * Exibe o valor total médio dos N primeiros pedidos da fila de pedidos.
     * O usuário informa quantos pedidos devem ser considerados.
     */
    public static void exibirValorMedioPedidos() {

    	cabecalho();

    	Integer quantidade = lerQuantidadePrimeirosPedidos();
    	if (quantidade == null) {
    		return;
    	}

    	try {
    		double valorMedio = filaPedidos.calcularValorMedio(Pedido::valorFinal, quantidade);
    		System.out.println(String.format("Valor total médio dos primeiros pedidos (%d): R$ %.2f", quantidade, valorMedio));
    	} catch (IllegalArgumentException excecao) {
    		System.out.println("Não há " + quantidade + " pedidos na fila.");
    	}
    }

    /**
     * Exibe, dentre os N primeiros pedidos da fila, aqueles com valor total acima de um valor informado pelo usuário.
     */
    public static void exibirPedidosAcimaDeValor() {

    	cabecalho();

    	Integer quantidade = lerQuantidadePrimeirosPedidos();
    	if (quantidade == null) {
    		return;
    	}

    	Double valorMinimo = lerValor("Exibir pedidos com valor total acima de (R$):");
    	if (valorMinimo == null) {
    		System.out.println("Valor inválido.");
    		return;
    	}

    	try {
    		Fila<Pedido> pedidosFiltrados = filaPedidos.filtrar(pedido -> pedido.valorFinal() > valorMinimo, quantidade);
    		System.out.println(String.format("\nPedidos, dentre os primeiros (%d), com valor total acima de R$ %.2f:", quantidade, valorMinimo));
    		exibirPedidos(pedidosFiltrados);
    	} catch (IllegalArgumentException excecao) {
    		System.out.println("Não há " + quantidade + " pedidos na fila.");
    	}
    }

    /**
     * Exibe, dentre os N primeiros pedidos da fila, aqueles que contêm um produto informado pelo usuário.
     */
    public static void exibirPedidosComProduto() {

    	cabecalho();

    	Integer quantidade = lerQuantidadePrimeirosPedidos();
    	if (quantidade == null) {
    		return;
    	}

    	Produto produto = localizarProdutoDescricao();
    	if (produto == null) {
    		System.out.println("Produto não encontrado.");
    		return;
    	}

    	try {
    		Fila<Pedido> pedidosFiltrados = filaPedidos.filtrar(pedido -> pedido.existeNoPedido(produto) != null, quantidade);
    		System.out.println(String.format("\nPedidos, dentre os primeiros (%d), que contêm o produto %s:", quantidade, produto.descricao));
    		exibirPedidos(pedidosFiltrados);
    	} catch (IllegalArgumentException excecao) {
    		System.out.println("Não há " + quantidade + " pedidos na fila.");
    	}
    }

	public static void main(String[] args) {
		
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        
		nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> mostrarProduto(localizarProduto());
                case 3 -> mostrarProduto(localizarProdutoDescricao());
                case 4 -> pedido = iniciarPedido();
                case 5 -> {
                    finalizarPedido(pedido);
                    pedido = null; 
                }
                case 6 -> listarProdutosPedidosRecentes();
                case 7 -> exibirValorMedioPedidos();
                case 8 -> exibirPedidosAcimaDeValor();
                case 9 -> exibirPedidosComProduto();
            }
            pausa();
        }while(opcao != 0);       

        teclado.close();    
    }
}