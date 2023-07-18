package br.com.alura.estoque.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import br.com.alura.estoque.model.Produto;

@Dao
public interface ProdutoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Produto produto);

    @Update
    void atualiza(Produto produto);

    @Query("SELECT * FROM Produto")
    List<Produto> buscaTodos();

    @Query("SELECT * FROM Produto WHERE id = :id")
    Produto findProductById(long id);

    @Delete
    void remove(Produto produto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Produto> body);
}
