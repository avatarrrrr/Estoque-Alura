package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProductRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private ListaProdutosAdapter adapter;
    private ProdutoDAO dao;
    private ProductRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        EstoqueDatabase db = EstoqueDatabase.getInstance(this);
        dao = db.getProdutoDAO();
        repository = new ProductRepository(dao);

        findProducts();
    }

    private void findProducts() {
        repository.searchProducts(new ProductRepository.ProductRepositoryListener<List<Produto>>() {
            @Override
            public void onSuccess(List<Produto> data) {
                adapter.atualiza(data);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ListaProdutosActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(this::remove);
    }

    private void remove(int posicao, Produto produtoRemovido) {
        new BaseAsyncTask<>(() -> {
            dao.remove(produtoRemovido);
            return null;
        }, resultado -> adapter.remove(posicao)).execute();
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, produto -> repository.save(produto, new ProductRepository.ProductRepositoryListener<Produto>() {
            @Override
            public void onSuccess(Produto data) {
                adapter.adiciona(data);
            }

            @Override
            public void onError(String message) {
                Toast
                        .makeText(
                                ListaProdutosActivity.this,
                                ListaProdutosActivity.this.getText(R.string.lista_produtos_activity_error_save_message),
                                Toast.LENGTH_SHORT
                        )
                        .show();
            }
        })).mostra();
    }

    private void abreFormularioEditaProduto(int position, Produto produto) {
        new EditaProdutoDialog(this, produto, productEdited -> repository.edit(productEdited, new ProductRepository.ProductRepositoryListener<Produto>() {
            @Override
            public void onSuccess(Produto data) {
                adapter.edita(position, data);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ListaProdutosActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        })).mostra();
    }


}
