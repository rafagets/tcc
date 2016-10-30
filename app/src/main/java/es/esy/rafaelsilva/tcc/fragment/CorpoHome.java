package es.esy.rafaelsilva.tcc.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.esy.rafaelsilva.tcc.R;
import es.esy.rafaelsilva.tcc.adapters.Atividades;
import es.esy.rafaelsilva.tcc.controle.CtrlPost;
import es.esy.rafaelsilva.tcc.interfaces.CallbackListar;
import es.esy.rafaelsilva.tcc.modelo.Post;
import es.esy.rafaelsilva.tcc.task.MontarView;

/**
 * Created by Rafael on 25/08/2016.
 */
public class CorpoHome extends Fragment {

    public SwipeRefreshLayout recarregar;
    private LinearLayout layout;
    private List<Post> posts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_corpo_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layout = (LinearLayout) getActivity().findViewById(R.id.relativeLayout);
        recarregar = (SwipeRefreshLayout) getActivity().findViewById(R.id.recarregar);
        recarregar.setRefreshing(true);

        carregarComentarios();

        recarregar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                layout.removeAllViews();
                carregarComentarios();
            }
        });

    }


    public void carregarComentarios() {

        new CtrlPost(getActivity()).listar("ORDER BY data DESC", new CallbackListar() {
            @Override
            public void resultadoListar(List<Object> lista) {
                posts = new ArrayList<>();
                for (Object obj : lista)
                    posts.add((Post) obj);

                new MontarView(getActivity(), layout, posts, recarregar).execute();
            }

            @Override
            public void falha() {
                ImageView falha = new ImageView(getActivity());
                falha.setImageResource(R.drawable.back_falha_carregar);
                layout.addView(falha);

                falha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        carregarComentarios();
                        layout.removeAllViews();
                        recarregar.setRefreshing(true);
                    }
                });
                recarregar.setRefreshing(false);
            }
        });

    }


}
