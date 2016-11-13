package es.esy.rafaelsilva.tcc.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.esy.rafaelsilva.tcc.R;
import es.esy.rafaelsilva.tcc.adapters.Pesquisa;
import es.esy.rafaelsilva.tcc.controle.CtrlUsuario;
import es.esy.rafaelsilva.tcc.interfaces.CallbackListar;
import es.esy.rafaelsilva.tcc.modelo.Usuario;
import es.esy.rafaelsilva.tcc.util.DadosUsuario;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listView;
    private List<Usuario> listaUsuarios;
    private Pesquisa adapter;
    private View fragmentCabecalho;
    private View fragmentCorpo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (DadosUsuario.getUsuario() != null){
            setTitle("Olá "+ DadosUsuario.getUsuario().getNome() + "!");
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, HistoricoActivity.class);
                intent.putExtra("lote", String.valueOf(3));
                startActivity(intent);

//                QRCode qrCode = new QRCode(HomeActivity.this);
//                qrCode.lerQrcode();

//                Intent intent = new Intent(HomeActivity.this, QrcodeActivity.class);
//                startActivity(intent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Colocando o nome e email do usuario atual no menu lateral
        View view = navigationView.getHeaderView(0);
        TextView txtUsuarioNome = (TextView) view.findViewById(R.id.txtUsuarioNome);
        TextView txtUsuarioEmail = (TextView) view.findViewById(R.id.txtUsuarioEmail);
        if (DadosUsuario.getUsuario() != null) {
            txtUsuarioNome.setText(DadosUsuario.getUsuario().getNome());
            txtUsuarioEmail.setText(DadosUsuario.getUsuario().getEmail());
        }
        navigationView.setNavigationItemSelectedListener(this);

        fragmentCabecalho = findViewById(R.id.cabecalho_post);
        fragmentCorpo =  findViewById(R.id.corpo_home);

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle("Não vá!");
        mensagem.setMessage("Mas se quiser realmente sair \nfique á vontade \uD83D\uDC4D");

        mensagem.setPositiveButton("Permanecer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(HomeActivity.this, "\uD83D\uDE09", Toast.LENGTH_SHORT).show();
            }
        });

        mensagem.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        mensagem.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search){
            listView = (ListView) findViewById(R.id.listView);
            new CtrlUsuario(this).listar("", new CallbackListar() {
                @Override
                public void resultadoListar(List<Object> lista) {
                    listaUsuarios = new ArrayList<>();

                    for (Object obj : lista) {
                        listaUsuarios.add((Usuario) obj);
                    }


                    if (listaUsuarios != null){
                        adapter = new Pesquisa(HomeActivity.this, listaUsuarios);
                        listView.setAdapter(adapter);
                        //arrayAdapter = new UsuarioAdapter(HomeActivity.this, android.R.layout.simple_list_item_1, listaUsuarios);
                        listView.setAdapter(adapter);

                        listView.setVisibility(View.VISIBLE);
                        fragmentCabecalho.setVisibility(View.GONE);
                        fragmentCorpo.setVisibility(View.GONE);

                        monitorarPesquisa(item);

                    }

                }

                @Override
                public void falha() {

                }
            });

        }
        return super.onOptionsItemSelected(item);
    }


    /*Aqui é onde a grande magica da pesquisa acontece!*/
    private void monitorarPesquisa(MenuItem item){
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(item);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*Atravez do adapter criado especialmente para esse fim
                * o componente pega os cliques do teclado e manda para
                * o adapter filtara as opçoes dinamicamente */
                adapter.getFilter().filter(newText);
                return false;
            }

        });

        searchViewAndroidActionBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        /*Aqui verifica se o usuario iniciou ou saiu da busca
        * assim, utiliza-se manipulaçoes de visibilidade das view*/
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                listView.setVisibility(View.GONE);
                fragmentCabecalho.setVisibility(View.VISIBLE);
                fragmentCorpo.setVisibility(View.VISIBLE);
                return true;
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(HomeActivity.this, Welcome_Activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id == R.id.nav_edit_user){
    Intent intent = new Intent(HomeActivity.this, AtualizaCadastroUsuarioActivity.class);
            startActivity(intent);

            //aqui abre a tela de cadastro de usuario
//            Intent intent = new Intent(HomeActivity.this, CadastroUsuarioActivity.class);
//            startActivity(intent);
        }else if(id == R.id.nav_logout){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("error", true);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
