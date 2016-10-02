package es.esy.rafaelsilva.tcc.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.esy.rafaelsilva.tcc.activity.ComentariosPostActivity;
import es.esy.rafaelsilva.tcc.DAO.Dao;
import es.esy.rafaelsilva.tcc.R;
import es.esy.rafaelsilva.tcc.adapters.ComentariosPostAdapter;
import es.esy.rafaelsilva.tcc.modelo.ComentarioPost;
import es.esy.rafaelsilva.tcc.modelo.Usuario;
import es.esy.rafaelsilva.tcc.util.Config;

/**
 * Created by Rafael on 08/09/2016.
 */
public class ComentariosPostTask extends AsyncTask<String, Void, Boolean> {

    private ProgressBar bar;
    private Context contexto;
    private List<ComentarioPost> comentarios;

    public ComentariosPostTask(Context contexto) {
        this.contexto = contexto;
    }

    @Override
    protected void onPreExecute() {
        bar = (ProgressBar) ((ComentariosPostActivity) contexto).findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(String... values) {

        String[] params;
        JSONObject jsonObject;
        JSONArray jsonArray;
        Dao helper;

        params = new String[] { "acao", "tabela", "ordenacao" };

        helper = new Dao();

        try {
            jsonArray = helper.getJSONArray(Config.urlMaster, params, values);

            try {
                comentarios = new ArrayList<ComentarioPost>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);

                    ComentarioPost c = new ComentarioPost();
                    c.setComentario(jsonObject.getString("comentario"));
                    c.setData(jsonObject.getString("data"));
                    c.setStatus(jsonObject.getInt("status"));

                    //c.setUsuario(loadUsuario(String.valueOf(jsonObject.getInt("usuario"))));


                    comentarios.add(c);
                }

                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch (Exception e){

        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean flag) {

        bar.setVisibility(View.GONE);

        ComentariosPostAdapter adapter = new ComentariosPostAdapter(comentarios, contexto);
        RecyclerView recyclerView = (RecyclerView) ((ComentariosPostActivity) contexto).findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(contexto);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }



    private Usuario loadUsuario(String codigo) {

        JSONObject jsonObject;
        JSONArray jsonArray;
        Dao helper = new Dao();

        String[] p = new String[] { "acao", "tabela", "condicao", "valores"  };
        String[] v = new String[] { "R", "usuario", "codigo",  codigo};

        try {
            jsonArray = helper.getJSONArray(Config.urlMaster, p, v);

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);

                    Usuario u = new Usuario();
                    u.setCodigo(jsonObject.getInt("codigo"));
                    u.setNome(jsonObject.getString("nome"));
                    u.setImagem(jsonObject.getString("imagem"));

                    return u;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch (Exception e){

        }

        return null;
    }
}
